package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.ProductEntity
import com.example.data.entity.SaleWithItems
import com.example.data.model.AppSettings
import com.example.data.model.CartItem
import com.example.data.model.CustomerField
import com.example.data.model.ReportSummary
import com.example.data.preferences.PreferenceManager
import com.example.data.repository.PosRepository
import com.example.util.Localization
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PosViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val preferenceManager = PreferenceManager(application)
    val repository = PosRepository(database.productDao(), database.saleDao(), preferenceManager)

    val settings: StateFlow<AppSettings> = repository.appSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    // Product search and filter
    val productSearchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")

    val products: StateFlow<List<ProductEntity>> = combine(
        productSearchQuery,
        selectedCategory,
        repository.allProducts
    ) { query, cat, allProducts ->
        allProducts.filter { product ->
            val matchesQuery = if (query.isBlank()) true else {
                product.name.contains(query, ignoreCase = true) ||
                product.category.contains(query, ignoreCase = true) ||
                product.sku.contains(query, ignoreCase = true)
            }
            val matchesCategory = if (cat == "All" || cat == "সব") true else {
                product.category.equals(cat, ignoreCase = true)
            }
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart State
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    // Discount state
    val discountValue = MutableStateFlow(0.0)
    val isPercentageDiscount = MutableStateFlow(false)

    // Customer form field values (field.id -> entered value)
    private val _customerFieldValues = MutableStateFlow<Map<String, String>>(emptyMap())
    val customerFieldValues: StateFlow<Map<String, String>> = _customerFieldValues.asStateFlow()

    val paymentMethod = MutableStateFlow("Cash")
    val saleNote = MutableStateFlow("")

    // Active receipt preview dialog state
    val activeReceiptForPreview = MutableStateFlow<SaleWithItems?>(null)

    // Sales history search
    val historySearchQuery = MutableStateFlow("")
    val salesHistory: StateFlow<List<SaleWithItems>> = historySearchQuery
        .flatMapLatest { query -> repository.searchSales(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reports state
    val selectedReportType = MutableStateFlow("today") // "all", "today", "month", "custom"
    val customReportStart = MutableStateFlow(0L)
    val customReportEnd = MutableStateFlow(0L)
    val currentReport = MutableStateFlow<ReportSummary?>(null)

    // Developer modal state
    val showDeveloperModal = MutableStateFlow(false)

    // Toast/Alert message event
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        loadReport("today")
    }

    fun setLanguage(lang: String) {
        repository.setLanguage(lang)
    }

    fun setCurrency(code: String, symbol: String) {
        repository.setCurrency(code, symbol)
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
            _toastEvent.emit(Localization.get("settings_saved_msg", newSettings.language))
        }
    }

    fun updateCustomerFields(fields: List<CustomerField>) {
        repository.updateCustomerFields(fields)
    }

    fun setCustomerFieldValue(fieldId: String, value: String) {
        val current = _customerFieldValues.value.toMutableMap()
        current[fieldId] = value
        _customerFieldValues.value = current
    }

    fun addToCart(product: ProductEntity) {
        if (product.stock <= 0) {
            viewModelScope.launch {
                _toastEvent.emit(Localization.get("out_of_stock", settings.value.language))
            }
            return
        }

        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.productId == product.id }
        if (index >= 0) {
            val item = currentList[index]
            if (item.quantity < product.stock) {
                currentList[index] = item.copy(quantity = item.quantity + 1, maxStock = product.stock)
                _cart.value = currentList
            } else {
                viewModelScope.launch {
                    _toastEvent.emit(Localization.get("validation_stock_limit", settings.value.language))
                }
            }
        } else {
            currentList.add(
                CartItem(
                    productId = product.id,
                    productName = product.name,
                    unitPrice = product.price,
                    quantity = 1,
                    maxStock = product.stock
                )
            )
            _cart.value = currentList
        }
    }

    fun decreaseCartItem(productId: Long) {
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            val item = currentList[index]
            if (item.quantity > 1) {
                currentList[index] = item.copy(quantity = item.quantity - 1)
                _cart.value = currentList
            } else {
                currentList.removeAt(index)
                _cart.value = currentList
            }
        }
    }

    fun removeFromCart(productId: Long) {
        _cart.value = _cart.value.filter { it.productId != productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
        discountValue.value = 0.0
        _customerFieldValues.value = emptyMap()
        saleNote.value = ""
    }

    fun checkCustomerFieldValidation(): Pair<Boolean, String?> {
        val lang = settings.value.language
        val fields = settings.value.customerFields.filter { it.isEnabled }
        for (field in fields) {
            if (field.isRequired) {
                val value = _customerFieldValues.value[field.id]?.trim().orEmpty()
                if (value.isBlank()) {
                    val fieldName = field.getLabel(lang)
                    val errorMsg = "$fieldName ${Localization.get("validation_required_field", lang)}"
                    return Pair(false, errorMsg)
                }
            }
        }
        return Pair(true, null)
    }

    fun generateReceiptAndProcessSale(onSuccess: (SaleWithItems) -> Unit) {
        val validation = checkCustomerFieldValidation()
        if (!validation.first) {
            viewModelScope.launch {
                _toastEvent.emit(validation.second ?: "Please fill required fields")
            }
            return
        }

        if (_cart.value.isEmpty()) {
            viewModelScope.launch {
                _toastEvent.emit(Localization.get("cart_empty", settings.value.language))
            }
            return
        }

        viewModelScope.launch {
            val processedSale = repository.processSale(
                cartItems = _cart.value,
                discountValue = discountValue.value,
                isPercentageDiscount = isPercentageDiscount.value,
                customerInfoMap = _customerFieldValues.value,
                paymentMethod = paymentMethod.value,
                note = saleNote.value
            )
            activeReceiptForPreview.value = processedSale
            clearCart()
            _toastEvent.emit(Localization.get("sale_success_msg", settings.value.language))
            // Refresh current reports
            loadReport(selectedReportType.value)
            onSuccess(processedSale)
        }
    }

    // Product CRUD
    fun saveProduct(product: ProductEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            if (product.id == 0L) {
                repository.addProduct(product)
            } else {
                repository.updateProduct(product)
            }
            onDone()
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun loadSampleData() {
        viewModelScope.launch {
            repository.resetWithSampleData()
            _toastEvent.emit("Sample products loaded successfully!")
        }
    }

    // Sales History Actions
    fun deleteSale(saleId: Long) {
        viewModelScope.launch {
            repository.deleteSale(saleId)
            loadReport(selectedReportType.value)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.deleteAllSales()
            loadReport(selectedReportType.value)
            _toastEvent.emit("All sales history cleared.")
        }
    }

    // Reports
    fun loadReport(type: String, start: Long = 0, end: Long = 0) {
        selectedReportType.value = type
        viewModelScope.launch {
            val summary = repository.generateReport(type, start, end)
            currentReport.value = summary
        }
    }
}
