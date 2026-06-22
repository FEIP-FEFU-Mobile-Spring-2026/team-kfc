package com.kfc.onlinestore.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.ui.components.ModalBottomSheetM3
import com.kfc.onlinestore.ui.components.ProductCard
import com.kfc.onlinestore.viewmodel.StoreViewModel
import com.kfc.onlinestore.viewmodel.StoreUiState

@Composable
fun HomeScreen(
    viewModel: StoreViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val store by viewModel.store.collectAsState()
    val selectedId by viewModel.selectedCategoryId.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    when (uiState) {
        is StoreUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is StoreUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ошибка при загрузке каталога",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = (uiState as StoreUiState.Error).message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(onClick = { viewModel.loadCatalog() }) {
                        Text("Повторить")
                    }
                }
            }
        }
        is StoreUiState.Success -> {
            val products = store?.items ?: emptyList()

            val filtered = remember(products, selectedId) {
                if (selectedId == null) {
                    products
                } else {
                    val filteredList = products.filter {
                        it.categoryId == selectedId || it.tags.any { tag -> tag.equals(selectedId, true) }
                    }
                    filteredList
                }
            }

            LazyColumn {
                items(filtered) { product ->
                    ProductCard(
                        product = product,
                        onClick = { selectedProduct = it }
                    )
                }
            }
        }
    }

    if (selectedProduct != null) {
        ModalBottomSheetM3(
            product = selectedProduct!!,
            onDismiss = { selectedProduct = null }
        )
    }
}