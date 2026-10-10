package com.example.data.model

data class CartItem(
    val productId: Long,
    val productName: String,
    val unitPrice: Double,
    var quantity: Int,
    val maxStock: Int
) {
    val totalPrice: Double
        get() = unitPrice * quantity
}

data class AppSettings(
    val shopName: String = "Glass POS Store",
    val shopPhone: String = "+880 1700-000000",
    val shopAddress: String = "Dhaka, Bangladesh",
    val currencyCode: String = "BDT",
    val currencySymbol: String = "৳",
    val ownerSignature: String = "Mehadi Hasan",
    val receiptTagline: String = "Thank you for shopping with us! Please come again.",
    val language: String = "bn", // "bn" or "en"
    val customerFields: List<CustomerField> = CustomerField.defaultFields()
)

data class ReportSummary(
    val reportType: String,
    val startDate: Long,
    val endDate: Long,
    val totalTransactions: Int,
    val totalSales: Double,
    val totalDiscount: Double,
    val averageSale: Double,
    val topProducts: List<TopProductItem> = emptyList()
)

data class TopProductItem(
    val productName: String,
    val quantitySold: Int,
    val totalRevenue: Double
)
