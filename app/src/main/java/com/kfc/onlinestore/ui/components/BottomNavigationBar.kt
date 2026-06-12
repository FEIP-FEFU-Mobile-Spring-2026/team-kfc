package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.BottomNavBarColor
import com.kfc.onlinestore.ui.theme.ClothesCountColor
import com.kfc.onlinestore.ui.theme.FilterButtonColor
import com.kfc.onlinestore.ui.theme.FilterButtonText
import com.kfc.onlinestore.ui.theme.MainIndigo
import com.kfc.onlinestore.ui.theme.PinkBack
@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onCatalogClick: () -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int = 0
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onCatalogClick,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Каталог"
                )
            },
            label = { Text("Каталог") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MainIndigo,
                selectedTextColor = MainIndigo,
                unselectedIconColor = MainIndigo,
                unselectedTextColor = MainIndigo,
                indicatorColor = FilterButtonColor
            )
        )

        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = onCartClick,
            icon = {
                Box {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Корзина"
                    )
                    if (cartItemCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-4).dp)
                                .background(
                                    color = ClothesCountColor,
                                    shape = RoundedCornerShape(50)
                                )
                                .size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            },
            label = { Text("Корзина") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MainIndigo,
                selectedTextColor = MainIndigo,
                unselectedIconColor = MainIndigo,
                unselectedTextColor = MainIndigo,
                indicatorColor = FilterButtonColor
            )
        )
    }
}

@Composable
private fun NavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) FilterButtonColor else BlackText

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = contentColor
        )
    }
}