package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val subtotal: Double,
    val discountValue: Double = 0.0,
    val isPercentageDiscount: Boolean = false,
    val discountAmount: Double = 0.0,
    val grandTotal: Double,
    val customerInfoJson: String = "{}",
    val paymentMethod: String = "Cash",
    val note: String = ""
)
