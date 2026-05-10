package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    val priceRub = product.priceInKopecks / 100.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                LazyRow(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(product.tags.size) { index ->
                        Text(
                            text = product.tags[index],
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(GreyTag.copy(alpha = 0.9f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = FilterButtonText
                        )
                    }
                }
            }

            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )

            Text(
                text = product.longDescription,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )

            Text(
                text = "%.0f ₽".format(priceRub),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GreyTag)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = MainIndigo,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}