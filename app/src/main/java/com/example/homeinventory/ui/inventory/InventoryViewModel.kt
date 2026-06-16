package com.example.homeinventory.ui.inventory

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeinventory.R
import com.example.homeinventory.data.AppDatabase
import com.example.homeinventory.data.InventoryItem
import com.example.homeinventory.data.ShoppingListItem
import com.example.homeinventory.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).inventoryDao()
    private val repository = ProductRepository()

    val inventoryItems: StateFlow<List<InventoryItem>> = dao.getAllItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val shoppingListItems: StateFlow<List<ShoppingListItem>> = dao.getShoppingList()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        createNotificationChannel(application)
    }

    fun incrementQuantity(item: InventoryItem) {
        viewModelScope.launch {
            dao.updateQuantity(item.id, item.quantity + 1)
        }
    }

    fun decrementQuantity(item: InventoryItem) {
        if (item.quantity > 0) {
            val newQuantity = item.quantity - 1
            viewModelScope.launch {
                dao.updateQuantity(item.id, newQuantity)

                if (newQuantity <= item.minQuantityThreshold) {
                    dao.insertShoppingListItem(
                        ShoppingListItem(
                            itemName = item.name,
                            linkedBarcode = item.barcode
                        )
                    )
                    triggerLowStockNotification(item.name)
                }
            }
        }
    }

    fun addScannedItem(barcode: String, minThreshold: Int = 2) {
        viewModelScope.launch {
            val existing = dao.getItemByBarcode(barcode)
            if (existing != null) {
                incrementQuantity(existing)
            } else {
                val result = repository.getProductDetails(barcode)
                result.fold(
                    onSuccess = { (name, imageUrl) ->
                        val newItem = InventoryItem(
                            barcode = barcode,
                            name = name,
                            quantity = 1,
                            minQuantityThreshold = minThreshold,
                            imageUrl = imageUrl,
                            category = null
                        )
                        dao.insertItem(newItem)
                    },
                    onFailure = {
                        // Handle error or add manually
                    }
                )
            }
        }
    }

    fun restockCheckedItems() {
        viewModelScope.launch {
            // In a real app we might want to map shopping items back to inventory and increment.
            // For now, we simply delete them as per manifest "removes it from the list".
            dao.deleteCheckedShoppingListItems()
        }
    }

    fun toggleShoppingListItem(item: ShoppingListItem, isChecked: Boolean) {
        viewModelScope.launch {
            dao.updateShoppingListItem(item.copy(isChecked = isChecked))
        }
    }

    private fun triggerLowStockNotification(itemName: String) {
        val context = getApplication<Application>()
        val builder = NotificationCompat.Builder(context, "LOW_STOCK_CHANNEL")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Stock Alert")
            .setContentText("Running low on $itemName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(itemName.hashCode(), builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Low Stock Alerts"
            val descriptionText = "Notifications for when inventory items run low"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("LOW_STOCK_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
