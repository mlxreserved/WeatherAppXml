package com.example.auth.data

import com.example.auth.domain.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class AuthRepositoryImpl: AuthRepository {
    override fun login(auth: FirebaseAuth, login: String, password: String): Task<AuthResult> =
        auth.signInWithEmailAndPassword(login, password)


    override fun registration(auth: FirebaseAuth, login: String, password: String): Task<AuthResult> =
        auth.createUserWithEmailAndPassword(login, password)


    override fun logout(auth: FirebaseAuth) {
        auth.signOut()
    }
}