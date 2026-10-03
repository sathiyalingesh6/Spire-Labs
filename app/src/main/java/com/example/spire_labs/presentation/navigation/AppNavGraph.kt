package com.example.spire_labs.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spire_labs.presentation.cart.CartScreen
import com.example.spire_labs.presentation.product_detail.ProductDetailScreen
import com.example.spire_labs.presentation.product_list.ProductListScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ProductList.route
    ) {
        composable(route = Screen.ProductList.route) {
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                }
            )
        ) {
            ProductDetailScreen(
                onNavigateBack = {
                    navController.safePopBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(route = Screen.Cart.route) {
            CartScreen(
                onNavigateBack = {
                    navController.safePopBackStack()
                }
            )
        }
    }
}

/**
 * Safely navigates back only if there is a valid previous back stack entry
 * and the current entry is in at least the RESUMED state, preventing duplicate pops / null pointer crashes.
 */
fun NavHostController.safePopBackStack() {
    val isResumed = currentBackStackEntry?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) ?: true
    if (previousBackStackEntry != null && isResumed) {
        popBackStack()
    }
}
