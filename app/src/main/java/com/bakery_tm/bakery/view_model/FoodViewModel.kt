package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.domain.FoodRepository
import com.bakery_tm.bakery.models.FoodModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodViewModel(
    val foodRepository: FoodRepository,
): ViewModel() {

    private val _state = MutableStateFlow<List<FoodModel>>(emptyList())
    val state: StateFlow<List<FoodModel>> = _state

    private val _selected = MutableStateFlow<FoodModel?>(null)
    val selected: StateFlow<FoodModel?> = _selected

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
}