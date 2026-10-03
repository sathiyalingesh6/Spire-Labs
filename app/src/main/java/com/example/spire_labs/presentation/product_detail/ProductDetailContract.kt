package com.example.spire_labs.presentation.product_detail

import com.example.spire_labs.core.base.UiEffect
import com.example.spire_labs.core.base.UiIntent
import com.example.spire_labs.core.base.UiState
import com.example.spire_labs.domain.model.Product

sealed interface ProductDetailIntent : UiIntent {
    data class LoadProductDetail(val productId: Int) : ProductDetailIntent
    data object AddToCart : ProductDetailIntent
    data object NavigateBack : ProductDetailIntent
    data object NavigateToCart : ProductDetailIntent
}

@androidx.compose.runtime.Immutable
data class ProductDetailState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null,
    val isAddedToCart: Boolean = false,
    val cartItemCount: Int = 0
) : UiState

sealed interface ProductDetailEffect : UiEffect {
    data object NavigateBack : ProductDetailEffect
    data object NavigateToCart : ProductDetailEffect
    data class ShowSnackbar(val message: String) : ProductDetailEffect
}
