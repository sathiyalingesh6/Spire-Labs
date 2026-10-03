package com.example.spire_labs.presentation.product_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.spire_labs.core.base.BaseViewModel
import com.example.spire_labs.core.network.Resource
import com.example.spire_labs.domain.usecase.AddToCartUseCase
import com.example.spire_labs.domain.usecase.GetCartItemsUseCase
import com.example.spire_labs.domain.usecase.GetProductDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<ProductDetailState, ProductDetailIntent, ProductDetailEffect>() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    init {
        observeCart()
        setIntent(ProductDetailIntent.LoadProductDetail(productId))
    }

    override fun createInitialState(): ProductDetailState = ProductDetailState()

    override fun handleIntent(intent: ProductDetailIntent) {
        when (intent) {
            is ProductDetailIntent.LoadProductDetail -> fetchProductDetails(intent.productId)
            is ProductDetailIntent.AddToCart -> handleAddToCart()
            is ProductDetailIntent.NavigateBack -> setEffect { ProductDetailEffect.NavigateBack }
            is ProductDetailIntent.NavigateToCart -> setEffect { ProductDetailEffect.NavigateToCart }
        }
    }

    private fun observeCart() {
        getCartItemsUseCase().onEach { items ->
            val totalCount = items.sumOf { it.quantity }
            setState { copy(cartItemCount = totalCount) }
        }.launchIn(viewModelScope)
    }

    private fun fetchProductDetails(id: Int) {
        viewModelScope.launch {
            getProductDetailsUseCase(id).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        setState { copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        setState { copy(isLoading = false, product = resource.data, error = null) }
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false, error = resource.message) }
                    }
                }
            }
        }
    }

    private fun handleAddToCart() {
        val currentProduct = currentState.product ?: return
        if (currentProduct.stock <= 0) {
            setEffect { ProductDetailEffect.ShowSnackbar("Product is out of stock") }
            return
        }
        viewModelScope.launch {
            addToCartUseCase(currentProduct)
            setState { copy(isAddedToCart = true) }
            setEffect { ProductDetailEffect.ShowSnackbar("${currentProduct.title} added to cart") }
        }
    }
}
