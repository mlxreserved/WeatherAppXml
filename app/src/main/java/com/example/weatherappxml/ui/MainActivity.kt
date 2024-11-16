package com.example.weatherappxml.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherappxml.R
import com.example.weatherappxml.databinding.ActivityMainBinding
import com.example.weatherappxml.databinding.ItemForecastBinding
import com.example.weatherappxml.ui.main.ForecastFragment
import com.example.weatherappxml.ui.main.SuccessFragment
import com.example.weatherappxml.ui.search.SearchFragment
import com.example.weatherappxml.ui.settings.SettingsFragment


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}

