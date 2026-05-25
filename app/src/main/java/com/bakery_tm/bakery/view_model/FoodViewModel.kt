package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.common.mapper.toModel
import com.bakery_tm.bakery.domain.FoodRepository
import com.bakery_tm.bakery.models.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.collections.filter
import kotlin.collections.map

class FoodViewModel(
    val foodRepository: FoodRepository,
): ViewModel() {

    private val _state = MutableStateFlow<List<ProductModel>>(emptyList())
    val state: StateFlow<List<ProductModel>> = _state

    private val _unavailableProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val unavailableProducts: StateFlow<List<ProductModel>> = _unavailableProducts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selected = MutableStateFlow<ProductModel?>(null)
    val selected: StateFlow<ProductModel?> = _selected

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _error.emit(null)
            foodRepository
                .getProducts()
                .catch { e ->
                    _error.emit("")
                }
                .collect { products ->
                    if (products.isNotEmpty()) {
                        _error.emit(null)
                        _state.emit(products.filter { it.isActive }.map { it.toModel() })
                        _unavailableProducts.emit(products.filter { !it.isActive }.map { it.toModel() })
                    } else {
                        _error.emit("")
                    }
                }
        }
    }

    fun initSelected(productId: Long) {
        viewModelScope.launch {
            val food = _state.value.find { it.productId == productId } ?: return@launch
            _selected.emit(food)
        }
    }

    fun removeProduct(productId: Long) {
        viewModelScope.launch {
            try {
                val newProducts = foodRepository.removeProduct(productId)
                _state.emit(newProducts.filter { it.isActive }.map { it.toModel() })
                _unavailableProducts.emit(newProducts.filter { !it.isActive }.map { it.toModel() })
            } catch (e: Exception) {
            }
        }
    }

    fun returnBackProduct(productId: Long) {
        viewModelScope.launch {
            try {
                val newProducts = foodRepository.returnBackProduct(productId)
                _state.emit(newProducts.filter { it.isActive }.map { it.toModel() })
                _unavailableProducts.emit(newProducts.filter { !it.isActive }.map { it.toModel() })
            } catch (e: Exception) {
            }
        }
    }
}