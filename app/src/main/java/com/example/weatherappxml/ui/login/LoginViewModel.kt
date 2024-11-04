package com.example.weatherappxml.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class LoginState(
    val loginField: String = "",
    val passwordField: String = ""
)


class LoginViewModel: ViewModel() {

    private val _stateLogin: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val stateLogin: StateFlow<LoginState> = _stateLogin.asStateFlow()


}