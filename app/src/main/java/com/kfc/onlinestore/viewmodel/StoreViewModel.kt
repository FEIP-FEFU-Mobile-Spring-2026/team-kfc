package com.kfc.onlinestore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kfc.onlinestore.model.StoreResponse
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.model.CartItem
import com.kfc.onlinestore.network.RetrofitClient
import com.kfc.onlinestore.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface StoreUiState {
    object Loading : StoreUiState
    object Success : StoreUiState
    data class Error(val message: String) : StoreUiState
}

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<StoreUiState>(StoreUiState.Loading)
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    private val _store = MutableStateFlow<StoreResponse?>(null)
    val store: StateFlow<StoreResponse?> = _store.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val cartRepository = CartRepository(application)

    init {
        loadCatalog()
        loadCart()
    }

    fun setCategory(id: String?) {
        _selectedCategoryId.value = id
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _uiState.value = StoreUiState.Loading
            try {
                val response = RetrofitClient.apiService.getCatalog()
                _store.value = response

                if (_selectedCategoryId.value == null) {
                    _selectedCategoryId.value = "new"
                }

                _uiState.value = StoreUiState.Success
            } catch (e: Exception) {
                _uiState.value = StoreUiState.Error(e.localizedMessage ?: "Ошибка сети")
            }
        }
    }

    fun getFilteredProducts(): List<Product> {
        val storeData = _store.value ?: return emptyList()
        val selectedId = _selectedCategoryId.value ?: return storeData.items

        return when {
            selectedId == "new" -> {
                storeData.items.filter { product ->
                    product.tags.any { it.lowercase() == "new" }
                }
            }
            storeData.categories.any { it.id == selectedId } -> {
                storeData.items.filter { it.categoryId == selectedId }
            }
            else -> {
                storeData.items.filter { product ->
                    product.tags.any { it == selectedId }
                }
            }
        }
    }

    fun getOrderedFilterItems(): List<Pair<String, String>> {
        val storeData = _store.value ?: return emptyList()
        val items = mutableListOf<Pair<String, String>>()

        items.add("new" to "Новинки")

        val categories = storeData.categories.map { it.id to it.name }
        items.addAll(categories)

        val otherTags = storeData.items
            .flatMap { it.tags }
            .distinct()
            .filter { it.lowercase() != "new" }
            .map { it to it }
        items.addAll(otherTags)

        return items.distinctBy { it.first }
    }

    private fun loadCart() {
        _cart.value = cartRepository.getCart()
    }

    fun addToCart(productId: String, sizeId: String) {
        cartRepository.addToCart(productId, sizeId)
        loadCart()
    }

    fun updateCartItemQuantity(productId: String, sizeId: String, quantity: Int) {
        cartRepository.updateQuantity(productId, sizeId, quantity)
        loadCart()
    }

    fun removeFromCart(productId: String, sizeId: String) {
        cartRepository.removeFromCart(productId, sizeId)
        loadCart()
    }

    fun clearCart() {
        cartRepository.clearCart()
        loadCart()
    }

    fun getCartItemCount(): Int = _cart.value.sumOf { it.quantity }

    fun getCartWithProducts(): List<Pair<CartItem, Product?>> {
        val products = _store.value?.items ?: emptyList()
        return _cart.value.map { cartItem ->
            cartItem to products.find { it.id == cartItem.productId }
        }
    }
}