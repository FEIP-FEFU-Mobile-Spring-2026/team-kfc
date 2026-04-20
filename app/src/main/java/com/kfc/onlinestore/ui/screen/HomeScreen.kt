package com.kfc.onlinestore.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kfc.onlinestore.viewmodel.StoreViewModel
import com.kfc.onlinestore.ui.components.ProductCard

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel
) {
    val store by viewModel.store.collectAsState()
    val selectedId by viewModel.selectedCategoryId.collectAsState()

    val products = store?.items ?: emptyList()

    val filtered = if (selectedId == null) {
        products
    } else {
        products.filter { it.categoryId == selectedId }
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(filtered, key = { it.id }) {
            ProductCard(it)
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}