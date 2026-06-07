package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.PriceBg
import com.kfc.onlinestore.ui.theme.PriceText
import com.kfc.onlinestore.ui.theme.PinkBack

@Composable
fun ProductCard(
    product: Product,
    onClick: (Product) -> Unit
) {
    val priceRub = product.priceInKopecks / 100

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(product) }
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(width = 78.dp, height = 96.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = BlackText,
                    maxLines = 2
                )

                Text(
                    text = product.shortDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = BlackText,
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PriceBg
                ) {
                    Text(
                        text = "$priceRub ₽",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = PriceText,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}