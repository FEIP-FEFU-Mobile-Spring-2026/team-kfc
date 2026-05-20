package com.kfc.onlinestore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfc.onlinestore.model.StoreResponse
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.network.RetrofitClient
// Импортируйте ваш RetrofitClient или синглтон с apiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Состояния экрана для обработки загрузки и ошибок
sealed interface StoreUiState {
    object Loading : StoreUiState
    object Success : StoreUiState
    data class Error(val message: String) : StoreUiState
}

class StoreViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<StoreUiState>(StoreUiState.Loading)
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    private val _store = MutableStateFlow<StoreResponse?>(null)
    val store: StateFlow<StoreResponse?> = _store.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    init {
        // Загружаем данные сразу при создании ViewModel, без привязки к Context
        loadCatalog()
    }

    fun setCategory(id: String?) {
        _selectedCategoryId.value = id
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _uiState.value = StoreUiState.Loading
            try {
                // Делаем сетевой запрос вместо чтения из ассетов
                val response = RetrofitClient.apiService.getCatalog()
                _store.value = response

                // Опционально: устанавливаем первый фильтр по умолчанию ("new")
                if (_selectedCategoryId.value == null) {
                    _selectedCategoryId.value = "new"
                }

                _uiState.value = StoreUiState.Success
            } catch (e: Exception) {
                _uiState.value = StoreUiState.Error(e.localizedMessage ?: "Ошибка сети")
            }
        }
    }

    // Возвращает отфильтрованный список товаров в зависимости от выбранного id (категории или тега)
    fun getFilteredProducts(): List<Product> {
        val storeData = _store.value ?: return emptyList()
        val selectedId = _selectedCategoryId.value ?: return storeData.items

        return when {
            // Если выбран фильтр "Новинки", ищем товары с тегом "new"
            selectedId == "new" -> {
                storeData.items.filter { product ->
                    product.tags.any { it.lowercase() == "new" }
                }
            }
            // Проверяем, совпадает ли selectedId с какой-либо категорией
            storeData.categories.any { it.id == selectedId } -> {
                storeData.items.filter { it.categoryId == selectedId }
            }
            // В противном случае считаем, что выбран кастомный тег
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

        // 1. Добавляем "Новинки"
        items.add("new" to "Новинки")

        // 2. Добавляем категории из API
        val categories = storeData.categories.map { it.id to it.name }
        items.addAll(categories)

        // 3. Добавляем остальные теги из товаров
        val otherTags = storeData.items
            .flatMap { it.tags }
            .distinct()
            .filter { it.lowercase() != "new" }
            .map { it to it }
        items.addAll(otherTags)

        return items.distinctBy { it.first }
    }
}