package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.GreyTag
import com.kfc.onlinestore.ui.theme.PinkPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetM3(
    onDismiss: () -> Unit,
    product: Product
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val priceRub = product.priceInKopecks / 100
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier.height(250.dp).fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Text(
            text = product.name,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
        Text(
            text = product.longDescription,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
        Text(
            text = priceRub.toString(),
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            color = PinkPrice
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(product.tags.size) { index ->
                val item = product.tags[index]
                Text(
                    text = item,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(GreyTag, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = BlackText
                )
            }
        }
    }
}