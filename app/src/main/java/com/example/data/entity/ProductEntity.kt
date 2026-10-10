package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val stock: Int,
    val category: String = "General",
    val sku: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
