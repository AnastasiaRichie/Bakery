package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import com.bakery_tm.bakery.models.UserStateModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel: ViewModel() {

    private val _state = MutableStateFlow<UserStateModel>(UserStateModel())
    val state: StateFlow<UserStateModel> = _state

    private val _isRegistered = MutableStateFlow<Boolean>(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered

    fun isRegistered(): Boolean {
        //getFromSharedPref => isRegistered false => return false
        //else
        //getFromRepo //send token
        //isValid => return true else false
        return true
    }

    fun getUserInfo(
        email: String,
        password: String
    ) {
        //return
        //token
        //name/surname/email/birth date/userId(?)
    }

    fun getOrders(
        userId: Int
    ): List<String> {
        //getLocalsFromSharedPrefs
        //updateLocalsWithServer
        //date, location, cost, description (состав)

        return emptyList()
    }

    fun registrate(
        model: String
    ) {
        //repoToServer -> if success save data locally
    }

    fun updateName() {
        //repo Update Name
        //sharedPref Update Name
    }

    fun updateSurname() {
        //repo Update Surname
        //sharedPref Update Surname
    }

    fun forgetPassword() {
        //repoToServer try Update -> if success save pass encrypted locally
    }
}