package com.example.login.presentation.model

sealed interface LoginResult{

    data object Success: LoginResult
    data class Error(val message: String): LoginResult
    data object Loading: LoginResult

}