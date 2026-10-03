package com.example.spire_labs.presentation.navigation

sealed class Screen(val route: String) {
    data object ProductList : Screen("product_list")
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int): String = "product_detail/$productId"
    }
    data object Cart : Screen("cart")
}
