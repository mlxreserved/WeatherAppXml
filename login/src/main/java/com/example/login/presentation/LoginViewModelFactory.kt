package com.example.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.auth.domain.usecases.LoginUseCase
import com.example.auth.domain.usecases.LogoutUseCase
import com.example.auth.domain.usecases.RegistrationUseCase

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val registrationUseCase: RegistrationUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(
            loginUseCase = loginUseCase,
            registrationUseCase = registrationUseCase,
            logoutUseCase = logoutUseCase
        ) as T
    }

}