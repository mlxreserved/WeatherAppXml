package com.example.login.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.login.databinding.FragmentLoginBinding
import com.example.login.R
import com.example.auth.data.AuthRepositoryImpl
import com.example.auth.domain.usecases.LoginUseCase
import com.example.auth.domain.usecases.LogoutUseCase
import com.example.auth.domain.usecases.RegistrationUseCase
import com.example.login.presentation.model.LoginResult
import com.example.login.presentation.model.LoginUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(
            loginUseCase = LoginUseCase(authRepository = AuthRepositoryImpl()),
            registrationUseCase = RegistrationUseCase(authRepository = AuthRepositoryImpl()),
            logoutUseCase = LogoutUseCase(authRepository = AuthRepositoryImpl())
        )
    }

    private lateinit var loginUiState: StateFlow<LoginUiState.LoginScreen>

    private lateinit var auth: FirebaseAuth
    private var controller: NavController? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            loginViewModel.onBackLoginScreen()
            controller?.navigateUp()
        }


        loginUiState = loginViewModel.stateLogin
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            loginEt.setText(loginUiState.value.loginField)
            passwordEt.setText(loginUiState.value.passwordField)
            if (loginUiState.value.errorLogin != "") {
                loginEt.error = loginUiState.value.errorLogin
            } else {
                loginEt.error = null
            }
            if (loginUiState.value.errorPassword != "") {
                loginEt.error = loginUiState.value.errorPassword
            } else {
                loginEt.error = null
            }
        }

        binding.signInButton.setOnClickListener {
            if (loginViewModel.checkAllFieldsLogin(
                    loginError = getString(R.string.error_login),
                    emailError = getString(R.string.error_email),
                    errorPass = getString(R.string.error_pass),
                    errorPassLength = getString(R.string.error_pass_len)
                )
            ) {
                loginViewModel.loginUser(
                    auth,
                    binding.loginEt.text.toString(),
                    binding.passwordEt.text.toString()
                )
            } else {
                if (loginUiState.value.errorLogin != "") {
                    binding.loginEt.error = loginUiState.value.errorLogin
                }
                if (loginUiState.value.errorPassword != "") {
                    binding.passwordEt.error = loginUiState.value.errorPassword
                }
            }

        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginUiState.collect { uiState ->
                    if (uiState.result is LoginResult.Error) {
                        binding.errorTv.visibility = View.VISIBLE
                    } else if (uiState.result is LoginResult.Success) {
                        binding.errorTv.visibility = View.GONE
                        loginViewModel.onBackLoginScreen()
                        controller?.navigateUp()
                    }
                }
            }
        }

        binding.signUpButton.setOnClickListener {
            loginViewModel.onBackLoginScreen()
            binding.apply {
                errorTv.visibility = View.GONE
                loginEt.text.clear()
                passwordEt.text.clear()
            }

            controller?.navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        val titleWatcherLogin = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginViewModel.onChangeLoginFieldLS(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        val titleWatcherPassword = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginViewModel.onChangePasswordFieldLS(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        }

        binding.apply {
            loginEt.addTextChangedListener(titleWatcherLogin)
            passwordEt.addTextChangedListener(titleWatcherPassword)
        }
    }

    override fun onDetach() {
        super.onDetach()
        controller = null
    }
}