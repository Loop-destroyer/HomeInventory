package com.example.homeinventory

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.homeinventory.ui.inventory.InventoryViewModel
import com.example.homeinventory.ui.main.MainScreen
import com.example.homeinventory.ui.scanner.BarcodeScannerScreen

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)
  val context = LocalContext.current
  val viewModel: InventoryViewModel = viewModel(
    factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
  )

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(
              viewModel = viewModel,
              onNavigateToScanner = { backStack.add(Scanner) }
          )
        }
        entry<Scanner> {
          BarcodeScannerScreen(
              onBarcodeScanned = { barcode ->
                  viewModel.addScannedItem(barcode)
                  backStack.removeLastOrNull() // Go back after scanning
              }
          )
        }
      },
  )
}
