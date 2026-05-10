package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.ui.theme.FilterButtonText
import com.kfc.onlinestore.ui.theme.GreyTag
import com.kfc.onlinestore.ui.theme.MainIndigo

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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth(),
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

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = priceRub.toString(),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(GreyTag, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MainIndigo
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
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
                            color = FilterButtonText
                        )
                    }
                }
            }
        }
    }
}