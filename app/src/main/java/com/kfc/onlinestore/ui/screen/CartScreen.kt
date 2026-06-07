package com.kfc.onlinestore.ui.screen

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kfc.onlinestore.model.CartLine
import com.kfc.onlinestore.ui.theme.ActionBrown
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.GreyTag
import com.kfc.onlinestore.ui.theme.PriceText
import com.kfc.onlinestore.ui.theme.SelectedBrown
import com.kfc.onlinestore.ui.theme.UnselectedLight
import com.kfc.onlinestore.viewmodel.StoreViewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: StoreViewModel,
    onGoHome: () -> Unit
) {
    val store by viewModel.store.collectAsState()
    val cartEntries by viewModel.cartEntries.collectAsState()

    val cartLines = remember(store, cartEntries) {
        viewModel.getCartLines()
    }
    val totalKopecks = remember(store, cartEntries) {
        viewModel.getCartTotalKopecks()
    }

    var showClearConfirm by remember { mutableStateOf(false) }
    var showOrderSuccess by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    val isNameValid = name.isNotBlank()
    val isEmailValid = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val canPlaceOrder = cartLines.isNotEmpty() && isNameValid && isEmailValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = { Text("Корзина", color = BlackText) },
                actions = {
                    IconButton(onClick = { showClearConfirm = true }) {
                        Icon(
                            Icons.Filled.DeleteOutline,
                            contentDescription = "Очистить корзину",
                            tint = ActionBrown
                        )
                    }
                }
            )

            if (cartLines.isEmpty()) {
                EmptyCartState()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Имя*") },
                            singleLine = true,
                            isError = name.isBlank() && email.isNotBlank(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ActionBrown,
                                unfocusedBorderColor = GreyTag,
                                focusedLabelColor = ActionBrown,
                                cursorColor = ActionBrown
                            )
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Почта*") },
                            singleLine = true,
                            isError = email.isNotBlank() && !isEmailValid,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ActionBrown,
                                unfocusedBorderColor = GreyTag,
                                focusedLabelColor = ActionBrown,
                                cursorColor = ActionBrown
                            )
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 96.dp),
                            label = { Text("Комментарий к заказу") },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ActionBrown,
                                unfocusedBorderColor = GreyTag,
                                focusedLabelColor = ActionBrown,
                                cursorColor = ActionBrown
                            )
                        )

                        Spacer(Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Итого",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BlackText
                            )
                            Text(
                                text = formatRubles(totalKopecks),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PriceText
                            )
                        }

                        if (!isNameValid || !isEmailValid) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Заполните обязательные поля: имя и корректную почту.",
                                color = GreyTag,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (canPlaceOrder) {
                                    viewModel.clearCart()
                                    showOrderSuccess = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ActionBrown,
                                contentColor = androidx.compose.ui.graphics.Color.White,
                                disabledContainerColor = UnselectedLight,
                                disabledContentColor = GreyTag
                            ),
                            enabled = canPlaceOrder
                        ) {
                            Text("Оформить заказ")
                        }
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
                    Text("Очистить", color = ActionBrown)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Отмена", color = ActionBrown)
                }
            }
        )
    }

    if (showOrderSuccess) {
        ModalBottomSheet(
            onDismissRequest = {
                showOrderSuccess = false
                onGoHome()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SuccessBagIcon()

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Заказ успешно оформлен",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BlackText,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Подтверждение и чек отправили на вашу почту.",
                    color = BlackText.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        showOrderSuccess = false
                        onGoHome()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ActionBrown,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Text("Вернуться в меню")
                }
            }
        }
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
            color = GreyTag
        )
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
        shape = RoundedCornerShape(18.dp),
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
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = line.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = BlackText,
                    maxLines = 2
                )

                Text(
                    text = line.size.name,
                    color = BlackText,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = formatRubles(
                        line.product.priceInKopecks * line.quantity
                    ),
                    color = PriceText,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease) {
                        Icon(
                            Icons.Filled.Remove,
                            contentDescription = "Уменьшить",
                            tint = ActionBrown
                        )
                    }

                    Text(
                        text = line.quantity.toString(),
                        modifier = Modifier.widthIn(min = 24.dp),
                        textAlign = TextAlign.Center,
                        color = BlackText
                    )

                    IconButton(onClick = onIncrease) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Увеличить",
                            tint = ActionBrown
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.DeleteOutline,
                    contentDescription = "Удалить",
                    tint = ActionBrown
                )
            }
        }
    }
}

@Composable
private fun SuccessBagIcon() {
    Canvas(modifier = Modifier.size(92.dp)) {
        val stroke = 5f
        val bagWidth = size.width * 0.58f
        val bagHeight = size.height * 0.55f
        val left = (size.width - bagWidth) / 2f
        val top = size.height * 0.24f
        val handleTop = size.height * 0.12f
        val handleBottom = size.height * 0.24f
        val corner = 14f

        drawRoundRect(
            color = ActionBrown,
            topLeft = Offset(left, top),
            size = androidx.compose.ui.geometry.Size(bagWidth, bagHeight),
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = stroke)
        )

        drawLine(
            color = ActionBrown,
            start = Offset(left + bagWidth * 0.28f, handleBottom),
            end = Offset(left + bagWidth * 0.28f, handleTop),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = ActionBrown,
            start = Offset(left + bagWidth * 0.72f, handleBottom),
            end = Offset(left + bagWidth * 0.72f, handleTop),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        val handlePath = Path().apply {
            moveTo(left + bagWidth * 0.28f, handleTop)
            quadraticBezierTo(
                left + bagWidth * 0.50f,
                handleTop - size.height * 0.05f,
                left + bagWidth * 0.72f,
                handleTop
            )
        }
        drawPath(
            path = handlePath,
            color = ActionBrown,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        val checkPath = Path().apply {
            moveTo(left + bagWidth * 0.38f, top + bagHeight * 0.55f)
            lineTo(left + bagWidth * 0.48f, top + bagHeight * 0.68f)
            lineTo(left + bagWidth * 0.66f, top + bagHeight * 0.43f)
        }
        drawPath(
            path = checkPath,
            color = ActionBrown,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

private fun formatRubles(kopecks: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    formatter.currency = Currency.getInstance("RUB")
    return formatter.format(kopecks / 100.0)
}