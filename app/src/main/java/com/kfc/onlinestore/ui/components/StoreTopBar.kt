package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kfc.onlinestore.model.Category
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.PinkBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreTopBar(
    categories: List<Category>,
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
                .padding(12.dp)
        ) {
            FilterButton(
                text = "Все",
                isSelected = selectedId == null,
                onClick = { onCategoryClick(null) }
            )

            categories.forEach {
                FilterButton(
                    text = it.name,
                    isSelected = selectedId == it.id,
                    onClick = { onCategoryClick(it.id) }
                )
            }
        }
    }
}