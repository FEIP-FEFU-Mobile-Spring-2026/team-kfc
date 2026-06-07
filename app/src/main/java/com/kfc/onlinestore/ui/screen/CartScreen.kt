package com.kfc.onlinestore.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kfc.onlinestore.model.CartLine
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.PinkBack
import com.kfc.onlinestore.ui.theme.PinkPrice
import com.kfc.onlinestore.viewmodel.StoreViewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun CartScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val cartLines = viewModel.getCartLines()
    val totalKopecks = viewModel.getCartTotalKopecks()

    var showClearConfirm by remember { mutableStateOf(false) }
    var showOrderSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (cartLines.isEmpty()) {
            EmptyCartState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SummaryCard(
                        totalText = formatRubles(totalKopecks),
                        onClearClick = { showClearConfirm = true }
                    )
                }

                items(
                    items = cartLines,
                    key = { "${it.product.id}_${it.size.id}" }
                ) { line ->
                    CartItemCard(
                        line = line,
                        onIncrease = {
                            viewModel.changeCartItemQuantity(
                                productId = line.product.id,
                                sizeId = line.size.id,
                                delta = 1
                            )
                        },
                        onDecrease = {
                            viewModel.changeCartItemQuantity(
                                productId = line.product.id,
                                sizeId = line.size.id,
                                delta = -1
                            )
                        },
                        onDelete = {
                            viewModel.removeCartItem(
                                productId = line.product.id,
                                sizeId = line.size.id
                            )
                        }
                    )
                }

                item {
                    Button(
                        onClick = {
                            viewModel.clearCart()
                            showOrderSuccess = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinkPrice,
                            contentColor = BlackText
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Оформить")
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Очистить корзину?") },
            text = { Text("Все товары будут удалены из корзины.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearCart()
                    showClearConfirm = false
                }) {
                    Text("Очистить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showOrderSuccess) {
        AlertDialog(
            onDismissRequest = { showOrderSuccess = false },
            title = { Text("Заказ оформлен") },
            text = { Text("Корзина очищена, заказ успешно отправлен.") },
            confirmButton = {
                TextButton(onClick = { showOrderSuccess = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun EmptyCartState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Корзина пуста",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = BlackText
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Добавьте товары из каталога, чтобы оформить заказ.",
            textAlign = TextAlign.Center,
            color = BlackText
        )
    }
}

@Composable
private fun SummaryCard(
    totalText: String,
    onClearClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PinkBack)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Итого",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = totalText,
                    style = MaterialTheme.typography.titleLarge,
                    color = PinkPrice
                )
            }

            TextButton(onClick = onClearClick) {
                Text("Очистить корзину")
            }
        }
    }
}

@Composable
private fun CartItemCard(
    line: CartLine,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = line.product.imageUrl,
                contentDescription = line.product.name,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = line.product.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Размер: ${line.size.name}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = formatRubles(line.product.priceInKopecks * line.quantity),
                    color = PinkPrice,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease) {
                        Icon(Icons.Filled.Remove, contentDescription = "Уменьшить")
                    }

                    Text(
                        text = line.quantity.toString(),
                        modifier = Modifier.widthIn(min = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(onClick = onIncrease) {
                        Icon(Icons.Filled.Add, contentDescription = "Увеличить")
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Удалить")
            }
        }
    }
}

private fun formatRubles(kopecks: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    formatter.currency = Currency.getInstance("RUB")
    return formatter.format(kopecks / 100.0)
}