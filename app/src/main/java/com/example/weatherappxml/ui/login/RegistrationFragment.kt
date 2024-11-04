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
import com.example.weatherappxml.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationFragment: Fragment() {

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var enterButton: Button
    private var controller: NavController? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var errorReg: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        auth = Firebase.auth
        errorReg = view.findViewById(R.id.error_reg)
        loginEditText = view.findViewById(R.id.login_et)
        passwordEditText = view.findViewById(R.id.password_et)
        confirmPasswordEditText = view.findViewById(R.id.confirm_password_et)
        enterButton = view.findViewById(R.id.enter_button)

        return view
    }

    private fun comparePasswords(pass: String, confPass: String): Boolean {
        return pass == confPass
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
        if(confirmPasswordEditText.text.isBlank()) {
            confirmPasswordEditText.error = getString(R.string.error_conf_pass)
            return false
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        enterButton.setOnClickListener {
            if(checkAllFields()) {
                if (comparePasswords(
                        passwordEditText.text.toString(),
                        confirmPasswordEditText.text.toString()
                    )
                ) {
                    errorReg.visibility = View.GONE
                    registrationUser(
                        auth,
                        loginEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                } else {
                    errorReg.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun registrationUser(auth: FirebaseAuth, login: String, password: String ) {
        auth.createUserWithEmailAndPassword(login, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                auth.signOut()
                controller?.navigateUp()
            }

        }
    }

    override fun onDetach() {
        super.onDetach()
        controller = null
    }

}