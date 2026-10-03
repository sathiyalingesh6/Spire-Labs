package com.example.spire_labs.presentation.product_list

import com.example.spire_labs.core.base.UiEffect
import com.example.spire_labs.core.base.UiIntent
import com.example.spire_labs.core.base.UiState
import com.example.spire_labs.domain.model.Product

sealed interface ProductListIntent : UiIntent {
    data object LoadProducts : ProductListIntent
    data class SearchQueryChanged(val query: String) : ProductListIntent
    data object Refresh : ProductListIntent
    data class OnProductClicked(val productId: Int) : ProductListIntent
    data object OnCartClicked : ProductListIntent
}

@androidx.compose.runtime.Immutable
data class ProductListState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val isEmpty: Boolean = false,
    val cartItemCount: Int = 0
) : UiState

sealed interface ProductListEffect : UiEffect {
    data class NavigateToDetail(val productId: Int) : ProductListEffect
    data object NavigateToCart : ProductListEffect
    data class ShowSnackbar(val message: String) : ProductListEffect
}
