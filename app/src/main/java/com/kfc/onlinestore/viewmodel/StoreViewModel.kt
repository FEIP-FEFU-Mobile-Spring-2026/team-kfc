package com.kfc.onlinestore.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kfc.onlinestore.model.Category
import com.kfc.onlinestore.model.StoreResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoreViewModel : ViewModel() {

    private val _store = MutableStateFlow<StoreResponse?>(null)
    val store: StateFlow<StoreResponse?> = _store

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId

    fun setCategory(id: String?) {
        _selectedCategoryId.value = id
    }

    fun load(context: Context) {
        if (_store.value != null) return

        viewModelScope.launch {
            val json = context.assets.open("products.json")
                .bufferedReader()
                .use { it.readText() }

            _store.value = Gson().fromJson(json, StoreResponse::class.java)
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
}