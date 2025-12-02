package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.domain.FoodRepository
import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.models.FoodModel
import com.bakery_tm.bakery.models.FoodType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodViewModel(
    val foodRepository: FoodRepository,
    val orderRepository: OrderRepository,
): ViewModel() {

    private val _state = MutableStateFlow<List<FoodModel>>(emptyList())
    val state: StateFlow<List<FoodModel>> = _state

    private val _selected = MutableStateFlow<FoodModel?>(null)
    val selected: StateFlow<FoodModel?> = _selected
    private val _orderCount = MutableStateFlow<Int>(0)
    val orderCount: StateFlow<Int> = _orderCount

    private var order: MutableList<FoodModel> = mutableListOf()

    init {
        viewModelScope.launch {
            foodRepository.getProducts().collect { _state.emit(it) }
        }
    }

    fun initSelected(foodId: Long) {
        viewModelScope.launch {
            val food = _state.value.find { it.id == foodId } ?: return@launch
            _selected.emit(food)
        }
    }

    fun updateOrder(model: FoodModel, count: Int) {
        orderRepository.updateOrder(model, count)
    }

    fun getFood() {
        //getFromSharedPref//in parallel get data from server=>updateLocal
    }

    fun getSelectedFood(foodId: Int) {
        //getFromSharedPref//in parallel get data from server=>updateLocal
    }
}