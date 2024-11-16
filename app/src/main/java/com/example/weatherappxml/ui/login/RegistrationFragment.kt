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
import com.example.weatherappxml.databinding.FragmentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationFragment: Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private var controller: NavController? = null
    private lateinit var auth: FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    private fun comparePasswords(pass: String, confPass: String): Boolean {
        return pass == confPass
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
        if(binding.confirmPasswordEt.text.isBlank()) {
            binding.confirmPasswordEt.error = getString(R.string.error_conf_pass)
            return false
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.enterButton.setOnClickListener {
            if(checkAllFields()) {
                if (comparePasswords(
                        binding.passwordEt.text.toString(),
                        binding.confirmPasswordEt.text.toString()
                    )
                ) {
                    binding.errorReg.visibility = View.GONE
                    registrationUser(
                        auth,
                        binding.loginEt.text.toString(),
                        binding.passwordEt.text.toString()
                    )
                } else {
                    binding.errorReg.visibility = View.VISIBLE
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