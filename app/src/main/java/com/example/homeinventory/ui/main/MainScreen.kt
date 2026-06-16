package com.example.homeinventory.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.homeinventory.ui.inventory.InventoryScreen
import com.example.homeinventory.ui.inventory.InventoryViewModel
import com.example.homeinventory.ui.shopping.ShoppingListScreen

@Composable
fun MainScreen(
    viewModel: InventoryViewModel,
    onNavigateToScanner: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Inventory") },
                    label = { Text("Inventory") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping List") },
                    label = { Text("Shopping List") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }
    ) { innerPadding ->
        if (selectedTab == 0) {
            // Inventory Tab
            Surface(modifier = Modifier.padding(innerPadding)) {
                InventoryScreen(
                    viewModel = viewModel,
                    onNavigateToScanner = onNavigateToScanner
                )
            }
        } else {
            // Shopping List Tab
            Surface(modifier = Modifier.padding(innerPadding)) {
                ShoppingListScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
