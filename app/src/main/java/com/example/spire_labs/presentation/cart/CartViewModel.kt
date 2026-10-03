package com.example.spire_labs.presentation.cart

import androidx.lifecycle.viewModelScope
import com.example.spire_labs.core.base.BaseViewModel
import com.example.spire_labs.domain.usecase.GetCartItemsUseCase
import com.example.spire_labs.domain.usecase.RemoveFromCartUseCase
import com.example.spire_labs.domain.usecase.UpdateCartQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
) : BaseViewModel<CartState, CartIntent, CartEffect>() {

    init {
        setIntent(CartIntent.LoadCart)
    }

    override fun createInitialState(): CartState = CartState()

    override fun handleIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.LoadCart -> observeCartItems()
            is CartIntent.IncreaseQuantity -> handleIncreaseQuantity(intent.cartItemId, intent.currentQuantity, intent.stock)
            is CartIntent.DecreaseQuantity -> handleDecreaseQuantity(intent.cartItemId, intent.currentQuantity)
            is CartIntent.RemoveItem -> handleRemoveItem(intent.cartItemId)
            is CartIntent.ClearCart -> handleClearCart()
            is CartIntent.NavigateBack -> setEffect { CartEffect.NavigateBack }
        }
    }

    private fun observeCartItems() {
        getCartItemsUseCase().onEach { items ->
            val totalItems = items.sumOf { it.quantity }
            val totalPrice = items.sumOf { it.totalPrice }
            setState {
                copy(
                    isLoading = false,
                    items = items,
                    totalItems = totalItems,
                    totalPrice = totalPrice
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun handleIncreaseQuantity(cartItemId: Int, currentQuantity: Int, stock: Int) {
        if (currentQuantity >= stock) {
            setEffect { CartEffect.ShowSnackbar("Cannot add more than available stock ($stock)") }
            return
        }
        viewModelScope.launch {
            updateCartQuantityUseCase(cartItemId, currentQuantity + 1)
        }
    }

    private fun handleDecreaseQuantity(cartItemId: Int, currentQuantity: Int) {
        viewModelScope.launch {
            updateCartQuantityUseCase(cartItemId, currentQuantity - 1)
        }
    }

    private fun handleRemoveItem(cartItemId: Int) {
        viewModelScope.launch {
            removeFromCartUseCase(cartItemId)
            setEffect { CartEffect.ShowSnackbar("Item removed from cart") }
        }
    }

    private fun handleClearCart() {
        viewModelScope.launch {
            currentState.items.forEach { item ->
                removeFromCartUseCase(item.id)
            }
            setEffect { CartEffect.ShowSnackbar("Cart cleared") }
        }
    }
}
