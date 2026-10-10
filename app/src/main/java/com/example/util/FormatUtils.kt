package com.example.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    private val decimalFormat = DecimalFormat("#,##0.00")
    private val integerFormat = DecimalFormat("#,##0")

    fun formatMoney(amount: Double, symbol: String = "৳"): String {
        return "$symbol ${decimalFormat.format(amount)}"
    }

    fun formatMoneyNoSymbol(amount: Double): String {
        return decimalFormat.format(amount)
    }

    fun formatNumber(amount: Double): String {
        return decimalFormat.format(amount)
    }

    fun formatNumber(amount: Int): String {
        return integerFormat.format(amount)
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTimeOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
