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
        "new" -> "Новинки"
        "popular" -> "Популярное"
        "sale" -> "Скидки"
        else -> tag
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreTopBar(
    filterItems: List<Pair<String, String>>,
    selectedId: String?,
    onCategoryClick: (String?) -> Unit
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text("Jaded") }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterButton(
                text = "Все",
                isSelected = selectedId == null,
                onClick = { onCategoryClick(null) }
            )

            filterItems.forEach { (id, name) ->
                FilterButton(
                    text = tagToDisplayName(name),
                    isSelected = selectedId == id,
                    onClick = { onCategoryClick(id) }
                )
            }
        }
    }
}