package com.example.homeinventory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE barcode = :barcode LIMIT 1")
    suspend fun getItemByBarcode(barcode: String): InventoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Query("UPDATE inventory_items SET quantity = :newQuantity WHERE id = :itemId")
    suspend fun updateQuantity(itemId: Int, newQuantity: Int)

    @Delete
    suspend fun deleteItem(item: InventoryItem)

    @Query("SELECT * FROM inventory_items WHERE quantity <= minQuantityThreshold")
    fun getLowStockItems(): Flow<List<InventoryItem>>

    // Shopping List Methods
    @Query("SELECT * FROM shopping_list_items")
    fun getShoppingList(): Flow<List<ShoppingListItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingListItem(item: ShoppingListItem)

    @Update
    suspend fun updateShoppingListItem(item: ShoppingListItem)

    @Delete
    suspend fun deleteShoppingListItem(item: ShoppingListItem)
    
    @Query("DELETE FROM shopping_list_items WHERE isChecked = 1")
    suspend fun deleteCheckedShoppingListItems()
}
