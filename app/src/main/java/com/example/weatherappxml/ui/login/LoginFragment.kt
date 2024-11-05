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
import com.example.weatherappxml.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth
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

        binding = FragmentLoginBinding.inflate(layoutInflater)


        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            if(checkAllFields()) {
                loginUser(auth, binding.loginEt.text.toString(), binding.passwordEt.text.toString())
            }
        }

        binding.signUpButton.setOnClickListener {
            binding.loginEt.setText("")
            binding.errorTv.setText("")
            controller?.navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    private fun loginUser(auth: FirebaseAuth, login: String, password: String) {
        auth.signInWithEmailAndPassword(login, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                binding.errorTv.visibility = View.GONE
                controller?.navigateUp()
            } else {
                binding.errorTv.visibility = View.VISIBLE
            }
        }
    }

    private fun checkAllFields(): Boolean {
        if(binding.loginEt.text.isBlank()) {
            binding.loginEt.error = getString(R.string.error_login)
            return false
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.loginEt.text.toString()).matches()){
            binding.loginEt.error = getString(R.string.error_email)
            return false
        }
        if(binding.passwordEt.text.isBlank()) {
            binding.passwordEt.error = getString(R.string.error_pass)
            return false
        } else if (binding.passwordEt.length() < 8) {
            binding.passwordEt.error = getString(R.string.error_pass_len)
            return false
        }
        return true
    }


    override fun onDetach() {
        super.onDetach()
        controller = null
    }
}