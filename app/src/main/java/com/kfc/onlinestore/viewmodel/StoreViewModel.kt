package com.kfc.onlinestore.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kfc.onlinestore.model.CartEntry
import com.kfc.onlinestore.model.CartLine
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.model.Size
import com.kfc.onlinestore.model.StoreResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreViewModel : ViewModel() {

    private val gson = Gson()

    private var appContext: Context? = null
    private var prefs: SharedPreferences? = null

    private val _store = MutableStateFlow<StoreResponse?>(null)
    val store: StateFlow<StoreResponse?> = _store.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _cartEntries = MutableStateFlow<List<CartEntry>>(emptyList())
    val cartEntries: StateFlow<List<CartEntry>> = _cartEntries.asStateFlow()

    private val _cartBadgeCount = MutableStateFlow(0)
    val cartBadgeCount: StateFlow<Int> = _cartBadgeCount.asStateFlow()

    private val _cartTotalKopecks = MutableStateFlow(0)
    val cartTotalKopecks: StateFlow<Int> = _cartTotalKopecks.asStateFlow()

    fun setCategory(id: String?) {
        _selectedCategoryId.value = id
    }

    fun load(context: Context) {
        appContext = context.applicationContext
        if (prefs == null) {
            prefs = appContext!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        if (_store.value != null) {
            loadCartFromStorage()
            return
        }

        viewModelScope.launch {
            val json = appContext!!.assets.open("products.json")
                .bufferedReader()
                .use { it.readText() }

            _store.value = gson.fromJson(json, StoreResponse::class.java)
            loadCartFromStorage()
        }
    }

    fun getOrderedFilterItems(): List<Pair<String, String>> {
        val storeData = _store.value ?: return emptyList()
        val items = mutableListOf<Pair<String, String>>()

        items.add("new" to "Новинки")
        items.addAll(storeData.categories.map { it.id to it.name })

        val otherTags = storeData.items
            .flatMap { it.tags }
            .distinct()
            .filter { it.lowercase() != "new" }
            .map { it to it }

        items.addAll(otherTags)

        return items.distinctBy { it.first }
    }

    fun addToCart(productId: String, sizeId: String) {
        val updated = _cartEntries.value.toMutableList()
        val index = updated.indexOfFirst { it.productId == productId && it.sizeId == sizeId }

        if (index >= 0) {
            val current = updated[index]
            updated[index] = current.copy(quantity = current.quantity + 1)
        } else {
            updated.add(CartEntry(productId = productId, sizeId = sizeId, quantity = 1))
        }

        syncCartState(updated)
    }

    fun changeCartItemQuantity(productId: String, sizeId: String, delta: Int) {
        val updated = _cartEntries.value.toMutableList()
        val index = updated.indexOfFirst { it.productId == productId && it.sizeId == sizeId }
        if (index == -1) return

        val current = updated[index]
        val newQuantity = current.quantity + delta

        if (newQuantity <= 0) {
            updated.removeAt(index)
        } else {
            updated[index] = current.copy(quantity = newQuantity)
        }

        syncCartState(updated)
    }

    fun removeCartItem(productId: String, sizeId: String) {
        val updated = _cartEntries.value.filterNot {
            it.productId == productId && it.sizeId == sizeId
        }
        syncCartState(updated)
    }

    fun clearCart() {
        syncCartState(emptyList())
    }

    fun getCartLines(): List<CartLine> {
        val storeData = _store.value ?: return emptyList()

        return _cartEntries.value.mapNotNull { entry ->
            val product = storeData.items.find { it.id == entry.productId } ?: return@mapNotNull null
            val size = product.sizes.find { it.id == entry.sizeId } ?: return@mapNotNull null
            CartLine(product = product, size = size, quantity = entry.quantity)
        }
    }

    fun getCartTotalKopecks(): Int {
        val storeData = _store.value ?: return 0
        return _cartEntries.value.sumOf { entry ->
            val product = storeData.items.find { it.id == entry.productId } ?: return@sumOf 0
            product.priceInKopecks * entry.quantity
        }
    }

    fun getProductById(productId: String): Product? {
        val storeData = _store.value ?: return null
        return storeData.items.find { it.id == productId }
    }

    private fun syncCartState(entries: List<CartEntry>) {
        val normalized = entries.filter { it.quantity > 0 }

        _cartEntries.value = normalized
        _cartBadgeCount.value = normalized.sumOf { it.quantity }
        _cartTotalKopecks.value = calculateTotal(normalized)
        persistCart(normalized)
    }

    private fun calculateTotal(entries: List<CartEntry>): Int {
        val storeData = _store.value ?: return 0
        return entries.sumOf { entry ->
            val product = storeData.items.find { it.id == entry.productId } ?: return@sumOf 0
            product.priceInKopecks * entry.quantity
        }
    }

    private fun loadCartFromStorage() {
        val sharedPreferences = prefs ?: return
        val savedJson = sharedPreferences.getString(CART_PREFS_KEY, null)
        val type = object : TypeToken<List<CartEntry>>() {}.type

        val loaded = if (savedJson.isNullOrBlank()) {
            emptyList()
        } else {
            runCatching {
                gson.fromJson<List<CartEntry>>(savedJson, type)
            }.getOrDefault(emptyList())
        }

        syncCartState(loaded)
    }

    private fun persistCart(entries: List<CartEntry>) {
        prefs?.edit()
            ?.putString(CART_PREFS_KEY, gson.toJson(entries))
            ?.apply()
    }

    companion object {
        private const val PREFS_NAME = "cart_prefs"
        private const val CART_PREFS_KEY = "cart_entries"
    }
}