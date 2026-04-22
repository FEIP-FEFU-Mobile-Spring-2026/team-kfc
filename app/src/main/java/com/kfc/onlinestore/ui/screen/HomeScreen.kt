package com.kfc.onlinestore.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.ui.components.ModalBottomSheetM3
import com.kfc.onlinestore.ui.components.ProductCard
import com.kfc.onlinestore.viewmodel.StoreViewModel

@Composable
fun HomeScreen(
    viewModel: StoreViewModel
) {
    val store by viewModel.store.collectAsState()
    val selectedId by viewModel.selectedCategoryId.collectAsState()

    val products = store?.items ?: emptyList()

    val filtered = remember(products, selectedId) {
        when {
            selectedId == null -> products

            products.any { it.categoryId == selectedId } ->
                products.filter { it.categoryId == selectedId }

            else ->
                products.filter { it.tags.contains(selectedId) }
        }
    }

    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    LazyColumn {
        items(filtered) { product ->
            ProductCard(
                product = product,
                onClick = { selectedProduct = it }
            )
        }
    }

    if (selectedProduct != null) {
        ModalBottomSheetM3(
            product = selectedProduct!!,
            onDismiss = { selectedProduct = null }
        )
    }
}