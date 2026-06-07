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

    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val products = store?.items ?: emptyList()
    val filtered = remember(products, selectedId) {
        if (selectedId == null) {
            products
        } else {
            products.filter {
                it.categoryId == selectedId || it.tags.any { tag -> tag.equals(selectedId, true) }
            }
        }
    }

    LazyColumn {
        items(filtered, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onClick = { selectedProduct = it }
            )
        }
    }

    selectedProduct?.let { product ->
        ModalBottomSheetM3(
            product = product,
            onDismiss = { selectedProduct = null },
            onAddToCart = { addedProduct, size ->
                viewModel.addToCart(addedProduct.id, size.id)
            }
        )
    }
}