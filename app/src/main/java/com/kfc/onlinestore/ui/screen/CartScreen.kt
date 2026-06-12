package com.kfc.onlinestore.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kfc.onlinestore.model.CartItem
import com.kfc.onlinestore.model.Product
import com.kfc.onlinestore.ui.theme.FilterButtonText
import com.kfc.onlinestore.ui.theme.GreyTag
import com.kfc.onlinestore.ui.theme.MainIndigo
import com.kfc.onlinestore.viewmodel.StoreViewModel

@Composable
fun CartScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cart.collectAsState()
    val store by viewModel.store.collectAsState()
    var showClearConfirm by remember { mutableStateOf(false) }
    var showOrderSuccess by remember { mutableStateOf(false) }

    val products = store?.items ?: emptyList()
    val cartWithProducts = cartItems.map { cartItem ->
        cartItem to products.find { it.id == cartItem.productId }
    }

    val totalPrice = cartWithProducts.sumOf { (cartItem, product) ->
        (product?.priceInKopecks ?: 0) * cartItem.quantity
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Корзина пуста",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Добавьте товары из каталога",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(cartWithProducts) { (cartItem, product) ->
                    if (product != null) {
                        CartItemRow(
                            cartItem = cartItem,
                            product = product,
                            onQuantityChange = { newQty ->
                                viewModel.updateCartItemQuantity(
                                    cartItem.productId,
                                    cartItem.sizeId,
                                    newQty
                                )
                            },
                            onRemove = {
                                viewModel.removeFromCart(
                                    cartItem.productId,
                                    cartItem.sizeId
                                )
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Итого:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "%.2f ₽".format(totalPrice / 100.0),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MainIndigo
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showOrderSuccess = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainIndigo
                    )
                ) {
                    Text("Оформить", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showClearConfirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreyTag.copy(alpha = 0.6f)
                    )
                ) {
                    Text("Очистить корзину", color = Color.Gray)
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Очистить корзину?") },
            text = { Text("Все товары будут удалены из корзины") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCart()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FilterButtonText,
                        contentColor = GreyTag
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearConfirm = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FilterButtonText,
                        contentColor = GreyTag
                    ),
                    ) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showOrderSuccess) {
        AlertDialog(
            onDismissRequest = { showOrderSuccess = false },
            title = { Text("Заказ оформлен!") },
            text = { Text("Спасибо за вашу покупку. Ваш заказ успешно создан.") },
            confirmButton = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(
                        onClick = {
                            viewModel.clearCart()
                            showOrderSuccess = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FilterButtonText,
                            contentColor = GreyTag
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ОК")
                    }
                }
            }
        )
    }
}

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    product: Product,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val size = product.sizes.find { it.id == cartItem.sizeId }
    val itemTotalPrice = product.priceInKopecks * cartItem.quantity

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .size(80.dp)
                .background(GreyTag.copy(alpha = 0.2f)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
            Text(
                text = "Размер: ${size?.name ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "%.2f ₽".format(itemTotalPrice / 100.0),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MainIndigo
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { if (cartItem.quantity > 1) onQuantityChange(cartItem.quantity - 1) },
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreyTag.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("−", color = Color.Black, fontSize = 12.sp)
                }

                Text(
                    text = cartItem.quantity.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = { onQuantityChange(cartItem.quantity + 1) },
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreyTag.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("+", color = Color.Black, fontSize = 12.sp)
                }
            }
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = FilterButtonText
            )
        }
    }
}
