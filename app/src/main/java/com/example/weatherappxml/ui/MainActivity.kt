package com.example.weatherappxml.ui

import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherappxml.R
import com.example.weatherappxml.data.api.model.SettingsModel
import com.example.weatherappxml.data.api.model.ThemeModel
import com.example.weatherappxml.data.repository.ThemeType
import com.example.weatherappxml.di.ModelProvider
import com.example.weatherappxml.utils.WeatherResult
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), SuccessFragment.Callbacks, SearchFragment.Callbacks {

    override fun onForecastSelected() {
        val fragment = ForecastFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressedSearch() {

        supportFragmentManager.popBackStack()
    }

    override fun onSettingsSelected() {
        val fragment = SettingsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onSearchSelected() {
        val fragment = SearchFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if(currentFragment == null){
            val fragment = SuccessFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

}

