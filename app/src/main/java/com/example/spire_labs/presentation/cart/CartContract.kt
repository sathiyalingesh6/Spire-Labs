package com.example.spire_labs.presentation.cart

import com.example.spire_labs.core.base.UiEffect
import com.example.spire_labs.core.base.UiIntent
import com.example.spire_labs.core.base.UiState
import com.example.spire_labs.domain.model.CartItem

sealed interface CartIntent : UiIntent {
    data object LoadCart : CartIntent
    data class IncreaseQuantity(val cartItemId: Int, val currentQuantity: Int, val stock: Int) : CartIntent
    data class DecreaseQuantity(val cartItemId: Int, val currentQuantity: Int) : CartIntent
    data class RemoveItem(val cartItemId: Int) : CartIntent
    data object ClearCart : CartIntent
    data object NavigateBack : CartIntent
}

@androidx.compose.runtime.Immutable
data class CartState(
    val isLoading: Boolean = false,
    val items: List<CartItem> = emptyList(),
    val totalItems: Int = 0,
    val totalPrice: Double = 0.0
) : UiState

sealed interface CartEffect : UiEffect {
    data object NavigateBack : CartEffect
    data class ShowSnackbar(val message: String) : CartEffect
}
