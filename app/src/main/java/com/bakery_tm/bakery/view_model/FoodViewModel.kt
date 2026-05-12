package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.common.mapper.toModel
import com.bakery_tm.bakery.domain.FoodRepository
import com.bakery_tm.bakery.models.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodViewModel(
    val foodRepository: FoodRepository,
): ViewModel() {

    private val _state = MutableStateFlow<List<ProductModel>>(emptyList())
    val state: StateFlow<List<ProductModel>> = _state

    private val _selected = MutableStateFlow<ProductModel?>(null)
    val selected: StateFlow<ProductModel?> = _selected

    init {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.getProducts().collect { _state.emit(it.map { it.toModel() }) }
        }
    }

    fun initSelected(productId: Long) {
        viewModelScope.launch {
            val food = _state.value.find { it.productId == productId } ?: return@launch
            _selected.emit(food)
        }
    }
}