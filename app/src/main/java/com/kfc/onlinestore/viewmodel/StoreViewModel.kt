package com.kfc.onlinestore.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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
}