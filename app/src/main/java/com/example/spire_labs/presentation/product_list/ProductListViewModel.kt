package com.example.spire_labs.presentation.product_list

import androidx.lifecycle.viewModelScope
import com.example.spire_labs.core.base.BaseViewModel
import com.example.spire_labs.core.network.Resource
import com.example.spire_labs.domain.usecase.GetCartItemsUseCase
import com.example.spire_labs.domain.usecase.GetProductsUseCase
import com.example.spire_labs.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase
) : BaseViewModel<ProductListState, ProductListIntent, ProductListEffect>() {

    private var searchJob: Job? = null

    init {
        observeCart()
        setIntent(ProductListIntent.LoadProducts)
    }

    override fun createInitialState(): ProductListState = ProductListState()

    override fun handleIntent(intent: ProductListIntent) {
        when (intent) {
            is ProductListIntent.LoadProducts -> fetchProducts()
            is ProductListIntent.SearchQueryChanged -> handleSearch(intent.query)
            is ProductListIntent.Refresh -> refresh()
            is ProductListIntent.OnProductClicked -> setEffect {
                ProductListEffect.NavigateToDetail(intent.productId)
            }
            is ProductListIntent.OnCartClicked -> setEffect {
                ProductListEffect.NavigateToCart
            }
        }
    }

    private fun observeCart() {
        getCartItemsUseCase().onEach { items ->
            val totalCount = items.sumOf { it.quantity }
            setState { copy(cartItemCount = totalCount) }
        }.launchIn(viewModelScope)
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            getProductsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        setState { copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val products = resource.data
                        setState {
                            copy(
                                isLoading = false,
                                products = products,
                                isEmpty = products.isEmpty(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = resource.message,
                                isEmpty = products.isEmpty()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleSearch(query: String) {
        setState { copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            fetchProducts()
            return
        }

        searchJob = viewModelScope.launch {
            delay(300.milliseconds) // Debounce typing
            searchProductsUseCase(query).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        setState { copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val products = resource.data
                        setState {
                            copy(
                                isLoading = false,
                                products = products,
                                isEmpty = products.isEmpty(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = resource.message,
                                isEmpty = products.isEmpty()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun refresh() {
        if (currentState.searchQuery.isNotBlank()) {
            handleSearch(currentState.searchQuery)
        } else {
            fetchProducts()
        }
    }
}
