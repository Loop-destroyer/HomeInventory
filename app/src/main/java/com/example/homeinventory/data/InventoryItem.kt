package com.example.homeinventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val barcode: String,
    val name: String,
    val quantity: Int,
    val minQuantityThreshold: Int,
    val imageUrl: String?,
    val category: String?
)
