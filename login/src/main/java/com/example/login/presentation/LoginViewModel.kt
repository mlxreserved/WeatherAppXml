package com.example.login.presentation

import androidx.lifecycle.ViewModel
import com.example.auth.domain.usecases.LoginUseCase
import com.example.auth.domain.usecases.LogoutUseCase
import com.example.auth.domain.usecases.RegistrationUseCase
import com.example.login.presentation.model.LoginResult
import com.example.login.presentation.model.LoginUiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val registrationUseCase: RegistrationUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _stateLogin: MutableStateFlow<LoginUiState.LoginScreen> = MutableStateFlow(
        LoginUiState.LoginScreen())
    val stateLogin: StateFlow<LoginUiState.LoginScreen> = _stateLogin.asStateFlow()

    private val _stateRegistration: MutableStateFlow<LoginUiState.RegistrationScreen> =
        MutableStateFlow(LoginUiState.RegistrationScreen())
    val stateRegistration: StateFlow<LoginUiState.RegistrationScreen> = _stateRegistration.asStateFlow()


    fun loginUser(auth: FirebaseAuth, login: String, password: String) {
        _stateLogin.update {
            it.copy(
                result = LoginResult.Loading
            )
        }
        loginUseCase(auth = auth, login = login, password = password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _stateLogin.update {
                    it.copy(
                        result = LoginResult.Success
                    )
                }
            } else {
                _stateLogin.update {
                    it.copy(
                        result = LoginResult.Error(message = task.exception?.message ?: "Not successful")
                    )
                }
            }
        }
    }

    fun registrationUser(auth: FirebaseAuth, login: String, password: String) {
        _stateRegistration.update {
            it.copy(
                result = LoginResult.Loading
            )
        }
        registrationUseCase(auth = auth, login = login, password = password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _stateRegistration.update {
                    it.copy(
                        result = LoginResult.Success
                    )
                }
            } else {
                _stateRegistration.update {
                    it.copy(
                        result = LoginResult.Error(message = task.exception?.message ?: "Not successful")
                    )
                }
            }

        }
    }

    fun logout(auth: FirebaseAuth) {
        logoutUseCase(auth)
    }

    fun checkAllFieldsLogin(
        loginError: String,
        emailError: String,
        errorPass: String,
        errorPassLength: String
    ): Boolean {
        if (_stateLogin.value.loginField.isBlank()) {
            _stateLogin.update {
                it.copy(errorLogin = loginError)
            }
            return false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_stateLogin.value.loginField)
                .matches()
        ) {
            _stateLogin.update {
                it.copy(errorLogin = emailError)
            }

            return false
        }
        _stateLogin.update {
            it.copy(errorLogin = "")
        }
        if (_stateLogin.value.passwordField.isBlank()) {
            _stateLogin.update {
                it.copy(errorPassword = errorPass)
            }
            return false
        } else if (_stateLogin.value.passwordField.length < 8) {
            _stateLogin.update {
                it.copy(errorPassword = errorPassLength)
            }

            return false
        }
        _stateLogin.update {
            it.copy(errorPassword = "")
        }
        return true
    }

    fun comparePasswords(): Boolean {
        return _stateRegistration.value.passwordField == _stateRegistration.value.confirmationPasswordField
    }

    fun checkAllFieldsRegistration(errorLogin: String, errorEmail: String, errorPass: String, errorPassLength: String, errorConfPass: String): Boolean {
        if(_stateRegistration.value.loginField.isBlank()) {
            _stateRegistration.update {
                it.copy(
                    errorLogin = errorLogin
                )
            }
            return false
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(_stateRegistration.value.loginField).matches()){
            _stateRegistration.update {
                it.copy(
                    errorLogin = errorEmail
                )
            }
            return false
        }

        _stateRegistration.update {
            it.copy(
                errorLogin = ""
            )
        }

        if(_stateRegistration.value.passwordField.isBlank()) {
            _stateRegistration.update {
                it.copy(
                    errorPassword = errorPass
                )
            }
            return false
        } else if (_stateRegistration.value.passwordField.length < 8) {
            _stateRegistration.update {
                it.copy(
                    errorPassword = errorPassLength
                )
            }
            return false
        }

        _stateRegistration.update {
            it.copy(
                errorPassword = ""
            )
        }

        if(_stateRegistration.value.confirmationPasswordField.isBlank()) {
            _stateRegistration.update {
                it.copy(
                    errorConfirmationPassword = errorConfPass
                )
            }
            return false
        }

        _stateRegistration.update {
            it.copy(
                errorPassword = ""
            )
        }

        return true
    }


    fun onChangeLoginFieldLS(newLogin: String) {
        _stateLogin.update {
            it.copy(
                loginField = newLogin
            )
        }
    }

    fun onChangePasswordFieldLS(newPassword: String) {
        _stateLogin.update {
            it.copy(
                passwordField = newPassword
            )
        }
    }

    fun onChangeLoginFieldRS(newLogin: String) {
        _stateRegistration.update {
            it.copy(
                loginField = newLogin
            )
        }
    }

    fun onChangePasswordFieldRS(newPassword: String) {
        _stateRegistration.update {
            it.copy(
                passwordField = newPassword
            )
        }
    }

    fun onChangeConfirmationPasswordFieldRS(newConfPassword: String) {
        _stateRegistration.update {
            it.copy(
                confirmationPasswordField = newConfPassword
            )
        }
    }

    fun onBackLoginScreen() {
        _stateLogin.update {
            it.copy(
                loginField = "",
                passwordField = "",
                result = LoginResult.Loading,
                errorLogin = "",
                errorPassword = ""
            )
        }
    }

    fun onBackRegistrationScreen() {
        _stateRegistration.update {
            it.copy(
                loginField = "",
                passwordField = "",
                confirmationPasswordField = "",
                result = LoginResult.Loading,
                errorLogin = "",
                errorPassword = "",
                errorConfirmationPassword = ""
            )
        }
    }



}