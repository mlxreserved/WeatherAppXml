package com.example.auth.domain.usecases

import com.example.auth.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth

class LogoutUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(auth: FirebaseAuth) {
        authRepository.logout(auth)
    }

}