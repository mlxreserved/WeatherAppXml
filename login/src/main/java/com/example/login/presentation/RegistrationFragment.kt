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
import com.example.login.R
import com.example.auth.data.AuthRepositoryImpl
import com.example.login.databinding.FragmentRegistrationBinding
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

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(
            loginUseCase = LoginUseCase(authRepository = AuthRepositoryImpl()),
            registrationUseCase = RegistrationUseCase(authRepository = AuthRepositoryImpl()),
            logoutUseCase = LogoutUseCase(authRepository = AuthRepositoryImpl())
        )
    }
    private lateinit var registrationUiState: StateFlow<LoginUiState.RegistrationScreen>

    private var controller: NavController? = null
    private lateinit var auth: FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            loginViewModel.onBackRegistrationScreen()
            controller?.navigateUp()
        }

        registrationUiState = loginViewModel.stateRegistration
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)


        binding.apply {
            loginEt.setText(registrationUiState.value.loginField)
            passwordEt.setText(registrationUiState.value.passwordField)
            confirmPasswordEt.setText(registrationUiState.value.confirmationPasswordField)
        }

        auth = Firebase.auth

        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.enterButton.setOnClickListener {
            if (loginViewModel.checkAllFieldsRegistration(
                    errorLogin = getString(R.string.error_login),
                    errorEmail = getString(R.string.error_email),
                    errorPass = getString(R.string.error_pass),
                    errorPassLength = getString(R.string.error_pass_len),
                    errorConfPass = getString(R.string.error_conf_pass)
                )
            ) {
                if (loginViewModel.comparePasswords()) {
                    binding.errorReg.visibility = View.GONE
                    loginViewModel.registrationUser(
                        auth,
                        binding.loginEt.text.toString(),
                        binding.passwordEt.text.toString()
                    )
                } else {
                    binding.apply {
                        errorReg.text = getString(R.string.error_reg)
                        errorReg.visibility = View.VISIBLE
                    }
                }
            } else {
                if(registrationUiState.value.errorLogin != "") {
                    binding.loginEt.error = registrationUiState.value.errorLogin
                }
                if(registrationUiState.value.errorPassword != "") {
                    binding.passwordEt.error = registrationUiState.value.errorPassword
                }
                if(registrationUiState.value.errorConfirmationPassword != "") {
                    binding.confirmPasswordEt.error = registrationUiState.value.errorConfirmationPassword
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registrationUiState.collect { uiState ->
                    if(uiState.result is LoginResult.Error) {
                        binding.errorReg.visibility = View.VISIBLE
                    } else if(uiState.result is LoginResult.Success) {
                        binding.errorReg.visibility = View.GONE
                        loginViewModel.logout(auth)
                        loginViewModel.onBackRegistrationScreen()
                        controller?.navigateUp()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val titleWatcherLogin = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginViewModel.onChangeLoginFieldRS(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        }
        val titleWatcherPassword = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginViewModel.onChangePasswordFieldRS(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        }
        val titleWatcherConfirmationPassword = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginViewModel.onChangeConfirmationPasswordFieldRS(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.apply {
            loginEt.addTextChangedListener(titleWatcherLogin)
            passwordEt.addTextChangedListener(titleWatcherPassword)
            confirmPasswordEt.addTextChangedListener(titleWatcherConfirmationPassword)
        }
    }

    override fun onDetach() {
        super.onDetach()
        controller = null
    }

}