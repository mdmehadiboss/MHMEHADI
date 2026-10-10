package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppSettings
import com.example.data.model.CustomerField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("glass_pos_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val shopName = prefs.getString("shop_name", "Glass POS Store") ?: "Glass POS Store"
        val shopPhone = prefs.getString("shop_phone", "+880 1700-000000") ?: "+880 1700-000000"
        val shopAddress = prefs.getString("shop_address", "Dhaka, Bangladesh") ?: "Dhaka, Bangladesh"
        val currencyCode = prefs.getString("currency_code", "BDT") ?: "BDT"
        val currencySymbol = prefs.getString("currency_symbol", "৳") ?: "৳"
        val ownerSignature = prefs.getString("owner_signature", "Mehadi Hasan") ?: "Mehadi Hasan"
        val receiptTagline = prefs.getString("receipt_tagline", "Thank you for shopping with us! Please come again.") ?: "Thank you for shopping with us! Please come again."
        val language = prefs.getString("language", "bn") ?: "bn"
        val customerFieldsJson = prefs.getString("customer_fields", "") ?: ""
        val customerFields = CustomerField.listFromJson(customerFieldsJson)

        return AppSettings(
            shopName = shopName,
            shopPhone = shopPhone,
            shopAddress = shopAddress,
            currencyCode = currencyCode,
            currencySymbol = currencySymbol,
            ownerSignature = ownerSignature,
            receiptTagline = receiptTagline,
            language = language,
            customerFields = customerFields
        )
    }

    fun updateSettings(newSettings: AppSettings) {
        prefs.edit().apply {
            putString("shop_name", newSettings.shopName)
            putString("shop_phone", newSettings.shopPhone)
            putString("shop_address", newSettings.shopAddress)
            putString("currency_code", newSettings.currencyCode)
            putString("currency_symbol", newSettings.currencySymbol)
            putString("owner_signature", newSettings.ownerSignature)
            putString("receipt_tagline", newSettings.receiptTagline)
            putString("language", newSettings.language)
            putString("customer_fields", CustomerField.listToJson(newSettings.customerFields))
            apply()
        }
        _settings.value = newSettings
    }

    fun setLanguage(lang: String) {
        val current = _settings.value
        updateSettings(current.copy(language = lang))
    }

    fun setCurrency(code: String, symbol: String) {
        val current = _settings.value
        updateSettings(current.copy(currencyCode = code, currencySymbol = symbol))
    }

    fun updateCustomerFields(fields: List<CustomerField>) {
        val current = _settings.value
        updateSettings(current.copy(customerFields = fields))
    }
}
