package com.example.data.repository

import com.example.data.dao.ProductDao
import com.example.data.dao.SaleDao
import com.example.data.entity.ProductEntity
import com.example.data.entity.SaleEntity
import com.example.data.entity.SaleItemEntity
import com.example.data.entity.SaleWithItems
import com.example.data.model.AppSettings
import com.example.data.model.CartItem
import com.example.data.model.CustomerField
import com.example.data.model.ReportSummary
import com.example.data.model.TopProductItem
import com.example.data.preferences.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PosRepository(
    private val productDao: ProductDao,
    private val saleDao: SaleDao,
    private val preferenceManager: PreferenceManager
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val allSales: Flow<List<SaleWithItems>> = saleDao.getAllSales()
    val appSettings: Flow<AppSettings> = preferenceManager.settings

    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        return if (query.isBlank()) {
            productDao.getAllProducts()
        } else {
            productDao.searchProducts(query.trim())
        }
    }

    fun searchSales(query: String): Flow<List<SaleWithItems>> {
        return if (query.isBlank()) {
            saleDao.getAllSales()
        } else {
            saleDao.searchSales(query.trim())
        }
    }

    suspend fun addProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }

    suspend fun deleteProductById(id: Long) {
        productDao.deleteProductById(id)
    }

    suspend fun resetWithSampleData() {
        productDao.deleteAllProducts()
        com.example.data.database.AppDatabase.populateInitialProducts(productDao)
    }

    suspend fun processSale(
        cartItems: List<CartItem>,
        discountValue: Double,
        isPercentageDiscount: Boolean,
        customerInfoMap: Map<String, String>,
        paymentMethod: String = "Cash",
        note: String = ""
    ): SaleWithItems = withContext(Dispatchers.IO) {
        val subtotal = cartItems.sumOf { it.totalPrice }
        val discountAmount = if (isPercentageDiscount) {
            (subtotal * (discountValue.coerceIn(0.0, 100.0) / 100.0))
        } else {
            discountValue.coerceAtLeast(0.0).coerceAtMost(subtotal)
        }
        val grandTotal = (subtotal - discountAmount).coerceAtLeast(0.0)

        // Generate clean unique invoice number like INV-20260815-1234
        val dateFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
        val invoiceNumber = "INV-${dateFormat.format(Date())}"

        val customerJson = JSONObject()
        customerInfoMap.forEach { (k, v) ->
            if (v.isNotBlank()) customerJson.put(k, v.trim())
        }

        val saleEntity = SaleEntity(
            invoiceNumber = invoiceNumber,
            timestamp = System.currentTimeMillis(),
            subtotal = subtotal,
            discountValue = discountValue,
            isPercentageDiscount = isPercentageDiscount,
            discountAmount = discountAmount,
            grandTotal = grandTotal,
            customerInfoJson = customerJson.toString(),
            paymentMethod = paymentMethod,
            note = note
        )

        val saleId = saleDao.insertSale(saleEntity)

        val saleItemEntities = cartItems.map { item ->
            SaleItemEntity(
                saleId = saleId,
                productId = item.productId,
                productName = item.productName,
                unitPrice = item.unitPrice,
                quantity = item.quantity,
                totalPrice = item.totalPrice
            )
        }
        saleDao.insertSaleItems(saleItemEntities)

        // Decrease stock in inventory
        for (item in cartItems) {
            productDao.decreaseStock(item.productId, item.quantity)
        }

        SaleWithItems(
            sale = saleEntity.copy(id = saleId),
            items = saleItemEntities
        )
    }

    suspend fun deleteSale(id: Long) {
        saleDao.deleteSaleById(id)
    }

    suspend fun deleteAllSales() {
        saleDao.deleteAllSales()
    }

    fun updateSettings(settings: AppSettings) {
        preferenceManager.updateSettings(settings)
    }

    fun setLanguage(lang: String) {
        preferenceManager.setLanguage(lang)
    }

    fun setCurrency(code: String, symbol: String) {
        preferenceManager.setCurrency(code, symbol)
    }

    fun updateCustomerFields(fields: List<CustomerField>) {
        preferenceManager.updateCustomerFields(fields)
    }

    // Reports Generation
    suspend fun generateReport(reportType: String, customStart: Long = 0, customEnd: Long = 0): ReportSummary = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        val (start, end) = when (reportType) {
            "today" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val s = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val e = calendar.timeInMillis
                Pair(s, e)
            }
            "month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val s = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val e = calendar.timeInMillis
                Pair(s, e)
            }
            "custom" -> {
                Pair(customStart, if (customEnd > 0) customEnd else System.currentTimeMillis())
            }
            else -> { // "all"
                Pair(0L, Long.MAX_VALUE)
            }
        }

        val allSalesList = saleDao.getAllSales().first()
        val filteredSales = allSalesList.filter { it.sale.timestamp in start..end }

        val totalTransactions = filteredSales.size
        val totalSales = filteredSales.sumOf { it.sale.grandTotal }
        val totalDiscount = filteredSales.sumOf { it.sale.discountAmount }
        val averageSale = if (totalTransactions > 0) totalSales / totalTransactions else 0.0

        // Aggregate top products
        val productSalesMap = mutableMapOf<String, Pair<Int, Double>>()
        for (sale in filteredSales) {
            for (item in sale.items) {
                val current = productSalesMap[item.productName] ?: Pair(0, 0.0)
                productSalesMap[item.productName] = Pair(
                    current.first + item.quantity,
                    current.second + item.totalPrice
                )
            }
        }

        val topProducts = productSalesMap.map { (name, pair) ->
            TopProductItem(productName = name, quantitySold = pair.first, totalRevenue = pair.second)
        }.sortedByDescending { it.quantitySold }.take(10)

        ReportSummary(
            reportType = reportType,
            startDate = start,
            endDate = end,
            totalTransactions = totalTransactions,
            totalSales = totalSales,
            totalDiscount = totalDiscount,
            averageSale = averageSale,
            topProducts = topProducts
        )
    }

    // Export entire database as JSON
    suspend fun exportDataAsJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportDate", System.currentTimeMillis())

        val currentSettings = preferenceManager.settings.value
        val settingsObj = JSONObject().apply {
            put("shopName", currentSettings.shopName)
            put("shopPhone", currentSettings.shopPhone)
            put("shopAddress", currentSettings.shopAddress)
            put("currencyCode", currentSettings.currencyCode)
            put("currencySymbol", currentSettings.currencySymbol)
            put("ownerSignature", currentSettings.ownerSignature)
            put("receiptTagline", currentSettings.receiptTagline)
            put("language", currentSettings.language)
            put("customerFields", CustomerField.listToJson(currentSettings.customerFields))
        }
        root.put("settings", settingsObj)

        val productsList = productDao.getAllProducts().first()
        val productsArr = JSONArray()
        for (p in productsList) {
            val pObj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("price", p.price)
                put("stock", p.stock)
                put("category", p.category)
                put("sku", p.sku)
                put("createdAt", p.createdAt)
            }
            productsArr.put(pObj)
        }
        root.put("products", productsArr)

        val salesList = saleDao.getAllSales().first()
        val salesArr = JSONArray()
        for (s in salesList) {
            val sObj = JSONObject().apply {
                put("id", s.sale.id)
                put("invoiceNumber", s.sale.invoiceNumber)
                put("timestamp", s.sale.timestamp)
                put("subtotal", s.sale.subtotal)
                put("discountValue", s.sale.discountValue)
                put("isPercentageDiscount", s.sale.isPercentageDiscount)
                put("discountAmount", s.sale.discountAmount)
                put("grandTotal", s.sale.grandTotal)
                put("customerInfoJson", s.sale.customerInfoJson)
                put("paymentMethod", s.sale.paymentMethod)
                put("note", s.sale.note)

                val itemsArr = JSONArray()
                for (it in s.items) {
                    val itObj = JSONObject().apply {
                        put("productId", it.productId)
                        put("productName", it.productName)
                        put("unitPrice", it.unitPrice)
                        put("quantity", it.quantity)
                        put("totalPrice", it.totalPrice)
                    }
                    itemsArr.put(itObj)
                }
                put("items", itemsArr)
            }
            salesArr.put(sObj)
        }
        root.put("sales", salesArr)

        root.toString(2)
    }

    // Import and restore from JSON
    suspend fun importDataFromJson(jsonString: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)

            if (root.has("settings")) {
                val sObj = root.getJSONObject("settings")
                val importedSettings = AppSettings(
                    shopName = sObj.optString("shopName", "Glass POS Store"),
                    shopPhone = sObj.optString("shopPhone", "+880 1700-000000"),
                    shopAddress = sObj.optString("shopAddress", "Dhaka, Bangladesh"),
                    currencyCode = sObj.optString("currencyCode", "BDT"),
                    currencySymbol = sObj.optString("currencySymbol", "৳"),
                    ownerSignature = sObj.optString("ownerSignature", "Mehadi Hasan"),
                    receiptTagline = sObj.optString("receiptTagline", "Thank you!"),
                    language = sObj.optString("language", "bn"),
                    customerFields = CustomerField.listFromJson(sObj.optString("customerFields", ""))
                )
                preferenceManager.updateSettings(importedSettings)
            }

            if (root.has("products")) {
                val pArr = root.getJSONArray("products")
                val products = mutableListOf<ProductEntity>()
                for (i in 0 until pArr.length()) {
                    val p = pArr.getJSONObject(i)
                    products.add(
                        ProductEntity(
                            id = if (p.has("id")) p.getLong("id") else 0,
                            name = p.getString("name"),
                            price = p.getDouble("price"),
                            stock = p.getInt("stock"),
                            category = p.optString("category", "General"),
                            sku = p.optString("sku", ""),
                            createdAt = p.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (products.isNotEmpty()) {
                    productDao.deleteAllProducts()
                    productDao.insertProducts(products)
                }
            }

            if (root.has("sales")) {
                val sArr = root.getJSONArray("sales")
                saleDao.deleteAllSales()
                for (i in 0 until sArr.length()) {
                    val s = sArr.getJSONObject(i)
                    val saleEntity = SaleEntity(
                        id = if (s.has("id")) s.getLong("id") else 0,
                        invoiceNumber = s.getString("invoiceNumber"),
                        timestamp = s.getLong("timestamp"),
                        subtotal = s.getDouble("subtotal"),
                        discountValue = s.optDouble("discountValue", 0.0),
                        isPercentageDiscount = s.optBoolean("isPercentageDiscount", false),
                        discountAmount = s.optDouble("discountAmount", 0.0),
                        grandTotal = s.getDouble("grandTotal"),
                        customerInfoJson = s.optString("customerInfoJson", "{}"),
                        paymentMethod = s.optString("paymentMethod", "Cash"),
                        note = s.optString("note", "")
                    )
                    val insertedSaleId = saleDao.insertSale(saleEntity)

                    if (s.has("items")) {
                        val itemsArr = s.getJSONArray("items")
                        val items = mutableListOf<SaleItemEntity>()
                        for (j in 0 until itemsArr.length()) {
                            val it = itemsArr.getJSONObject(j)
                            items.add(
                                SaleItemEntity(
                                    saleId = insertedSaleId,
                                    productId = it.optLong("productId", 0),
                                    productName = it.getString("productName"),
                                    unitPrice = it.getDouble("unitPrice"),
                                    quantity = it.getInt("quantity"),
                                    totalPrice = it.getDouble("totalPrice")
                                )
                            )
                        }
                        if (items.isNotEmpty()) {
                            saleDao.insertSaleItems(items)
                        }
                    }
                }
            }

            Result.success("Backup successfully restored!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
