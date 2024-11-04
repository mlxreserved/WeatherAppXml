package com.example.weatherappxml.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.weatherappxml.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment: Fragment() {

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var errorTextView: TextView
    private var controller: NavController? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        auth = Firebase.auth
        loginEditText = view.findViewById(R.id.login_et)
        passwordEditText = view.findViewById(R.id.password_et)
        signInButton = view.findViewById(R.id.sign_in_button)
        signUpButton = view.findViewById(R.id.sign_up_button)
        errorTextView = view.findViewById(R.id.error_tv)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInButton.setOnClickListener {
            if(checkAllFields()) {
                loginUser(auth, loginEditText.text.toString(), passwordEditText.text.toString())
            }
        }

        signUpButton.setOnClickListener {
            loginEditText.setText("")
            passwordEditText.setText("")
            controller?.navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    private fun loginUser(auth: FirebaseAuth, login: String, password: String) {
        auth.signInWithEmailAndPassword(login, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                errorTextView.visibility = View.GONE
                controller?.navigateUp()
            } else {
                errorTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun checkAllFields(): Boolean {
        if(loginEditText.text.isBlank()) {
            loginEditText.error = getString(R.string.error_login)
            return false
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(loginEditText.text.toString()).matches()){
            loginEditText.error = getString(R.string.error_email)
            return false
        }
        if(passwordEditText.text.isBlank()) {
            passwordEditText.error = getString(R.string.error_pass)
            return false
        } else if (passwordEditText.length() < 8) {
            passwordEditText.error = getString(R.string.error_pass_len)
            return false
        }
        return true
    }


    override fun onDetach() {
        super.onDetach()
        controller = null
    }
}