package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kfc.onlinestore.model.Category
fun tagToDisplayName(tag: String): String {
    return when (tag.lowercase()) {
        "popular" -> "Популярное"
        "new" -> "Новинки"
        "sale" -> "Скидки"
        else -> tag
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreTopBar(
    categories: List<Category>,
    selectedId: String?,
    onCategoryClick: (String?) -> Unit,
    filterTags: List<String>
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text("Jaded") }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            FilterButton(
                text = "Все",
                isSelected = selectedId == null,
                onClick = { onCategoryClick(null) }
            )

            filterTags.forEach { tag ->
                FilterButton(
                    text = tagToDisplayName(tag),
                    isSelected = selectedId == tag,
                    onClick = { onCategoryClick(tag) }
                )
            }

            categories.forEach { category ->
                FilterButton(
                    text = category.name,
                    isSelected = selectedId == category.id,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }
    }
}