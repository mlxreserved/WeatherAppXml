package com.example.login.presentation.model

sealed interface LoginUiState{

    val loginField: String
    val passwordField: String
    val result: LoginResult
    val errorLogin: String
    val errorPassword: String


    data class LoginScreen (
        override val loginField: String = "",
        override val passwordField: String = "",
        override val result: LoginResult = LoginResult.Loading,
        override val errorLogin: String = "",
        override val errorPassword: String = ""
    ) : LoginUiState

    data class RegistrationScreen (
        override val loginField: String = "",
        override val passwordField: String = "",
        val confirmationPasswordField: String = "",
        override val result: LoginResult = LoginResult.Loading,
        override val errorLogin: String = "",
        override val errorPassword: String = "",
        val errorConfirmationPassword: String = ""
    ) : LoginUiState
}


