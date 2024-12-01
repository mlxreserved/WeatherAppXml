package com.example.auth.domain.usecases

import com.example.auth.domain.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class RegistrationUseCase (
    private val authRepository: AuthRepository
) {

    operator fun invoke(auth: FirebaseAuth, login: String, password: String): Task<AuthResult> =
        authRepository.registration(auth = auth, login = login, password = password)


}