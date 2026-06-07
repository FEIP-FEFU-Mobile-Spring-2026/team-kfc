package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.model.Size
import com.kfc.onlinestore.ui.theme.ActionBrown
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.GreyTag
import com.kfc.onlinestore.ui.theme.SelectedBrown
import com.kfc.onlinestore.ui.theme.UnselectedLight
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetM3(
    onDismiss: () -> Unit,
    product: Product,
    onAddToCart: (Product, Size) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSizeId by remember(product.id) {
        mutableStateOf(product.sizes.firstOrNull()?.id)
    }

    LaunchedEffect(product.id) {
        selectedSizeId = product.sizes.firstOrNull()?.id
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = BlackText
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = product.longDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BlackText
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.sizes.forEach { size ->
                        val selected = size.id == selectedSizeId
                        Button(
                            onClick = { selectedSizeId = size.id },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) SelectedBrown else UnselectedLight,
                                contentColor = if (selected) Color.White else BlackText,
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(size.name)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val selectedSize = product.sizes.firstOrNull { it.id == selectedSizeId }
                            ?: return@Button
                        onAddToCart(product, selectedSize)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ActionBrown,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    ),
                    enabled = product.sizes.isNotEmpty()
                ) {
                    Text("В корзину · ${formatRubles(product.priceInKopecks)}")
                }
            }
        }
    }
}

private fun formatRubles(kopecks: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    formatter.currency = Currency.getInstance("RUB")
    return formatter.format(kopecks / 100.0)
}