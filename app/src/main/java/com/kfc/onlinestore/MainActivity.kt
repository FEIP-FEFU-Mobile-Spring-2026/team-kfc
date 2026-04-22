package com.kfc.onlinestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.kfc.onlinestore.ui.components.BottomNavigationBar
import com.kfc.onlinestore.ui.components.StoreTopBar
import com.kfc.onlinestore.ui.screen.CartScreen
import com.kfc.onlinestore.ui.screen.HomeScreen
import com.kfc.onlinestore.viewmodel.StoreViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: StoreViewModel = viewModel()
            val navController = rememberNavController()
            val context = LocalContext.current

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val store by viewModel.store.collectAsState()
            val selectedId by viewModel.selectedCategoryId.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.load(context)
            }

            val allTags = remember(store) {
                store?.items
                    ?.flatMap { it.tags }
                    ?.distinct()
                    ?: emptyList()
            }

            MaterialTheme {
                Scaffold(
                    topBar = {
                        if (currentRoute == "home") {
                            StoreTopBar(
                                categories = store?.categories ?: emptyList(),
                                selectedId = selectedId,
                                onCategoryClick = { viewModel.setCategory(it) },
                                filterTags = allTags
                            )
                        }
                    },
                    bottomBar = {
                        BottomNavigationBar(
                            currentRoute = currentRoute,
                            onCatalogClick = {
                                if (currentRoute != "home") {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            },
                            onCartClick = {
                                if (currentRoute != "cart") {
                                    navController.navigate("cart")
                                }
                            }
                        )
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("home") {
                            HomeScreen(viewModel = viewModel)
                        }
                        composable("cart") {
                            CartScreen()
                        }
                    }
                }
            }
        }
    }
}