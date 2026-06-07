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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.model.Size
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.GreyTag
import com.kfc.onlinestore.ui.theme.PinkBack
import com.kfc.onlinestore.ui.theme.PinkPrice
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
                .padding(bottom = 24.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(16.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = product.longDescription,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = formatRubles(product.priceInKopecks),
                    color = PinkPrice,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Размер",
                    style = MaterialTheme.typography.titleSmall
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.sizes.forEach { size ->
                        val isSelected = size.id == selectedSizeId

                        Button(
                            onClick = { selectedSizeId = size.id },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) BlackText else PinkBack,
                                contentColor = if (isSelected) PinkBack else BlackText
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(size.name, fontSize = 14.sp)
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
                    enabled = product.sizes.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PinkPrice,
                        contentColor = BlackText
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("В корзину")
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