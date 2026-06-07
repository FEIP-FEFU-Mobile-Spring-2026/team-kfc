package com.kfc.onlinestore.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kfc.onlinestore.ui.theme.ActionBrown
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.UnselectedLight
import com.kfc.onlinestore.ui.theme.Indicator
import com.kfc.onlinestore.ui.theme.UnselectedBottom

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    cartBadgeCount: Int,
    onCatalogClick: () -> Unit,
    onCartClick: () -> Unit
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
                    contentDescription = "Меню"
                )
            },
            label = { Text("Меню") },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ActionBrown,
                selectedTextColor = ActionBrown,
                unselectedIconColor = UnselectedBottom,
                unselectedTextColor = UnselectedBottom,
                indicatorColor = Indicator
            )
        )

        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = onCartClick,
            icon = {
                if (cartBadgeCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = ActionBrown,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (cartBadgeCount > 99) "99+" else cartBadgeCount.toString()
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Корзина"
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Корзина"
                    )
                }
            },
            label = { Text("Корзина") },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ActionBrown,
                selectedTextColor = ActionBrown,
                unselectedIconColor = UnselectedBottom,
                unselectedTextColor = UnselectedBottom,
                indicatorColor = Indicator
            )
        )
    }
}