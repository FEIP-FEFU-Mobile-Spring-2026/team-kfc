package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.FilterButtonColor

@Composable
fun StoreTopBar(
    filterItems: List<Pair<String, String>>,
    selectedId: String?,
    onCategoryClick: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Jaded",
                color = BlackText,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterPill(
                text = "Все",
                selected = selectedId == null,
                onClick = { onCategoryClick(null) }
            )

            FilterPill(
                text = "Новинки",
                selected = selectedId == "new",
                onClick = { onCategoryClick("new") }
            )

            filterItems
                .filter { it.first != "new" }
                .forEach { (id, name) ->
                    FilterPill(
                        text = name,
                        selected = selectedId == id,
                        onClick = { onCategoryClick(id) }
                    )
                }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun FilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) BlackText else FilterButtonColor,
            contentColor = if (selected) FilterButtonColor else BlackText,
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text)
    }
}