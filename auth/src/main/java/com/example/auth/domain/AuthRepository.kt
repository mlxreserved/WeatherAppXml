package com.example.auth.domain

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

interface AuthRepository {

    fun login(auth: FirebaseAuth, login: String, password: String): Task<AuthResult>

    fun registration(auth: FirebaseAuth, login: String, password: String): Task<AuthResult>

    fun logout(auth: FirebaseAuth)

}