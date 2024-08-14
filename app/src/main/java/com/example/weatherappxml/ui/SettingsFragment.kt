package com.example.weatherappxml.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherappxml.R
import com.example.weatherappxml.data.api.model.SettingsModel
import com.example.weatherappxml.data.api.model.ThemeModel
import com.example.weatherappxml.data.api.model.ThemeTypeState
import com.example.weatherappxml.data.repository.ThemeType
import com.example.weatherappxml.di.ModelProvider
import com.example.weatherappxml.utils.WeatherResult
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsFragment: Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var radioButtonGroup: RadioGroup
    private lateinit var light: RadioButton
    private lateinit var dark: RadioButton
    private var callbacks: SearchFragment.Callbacks? = null
    private lateinit var settingsState: StateFlow<ThemeTypeState>

    private val settingsViewModel: SettingsModel by activityViewModels {
        ModelProvider.Factory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as SearchFragment.Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsState = settingsViewModel.themeType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        radioButtonGroup = view.findViewById(R.id.theme_group) as RadioGroup

        radioButtonGroup.check(when(AppCompatDelegate.getDefaultNightMode()){
            AppCompatDelegate.MODE_NIGHT_NO -> R.id.light
            AppCompatDelegate.MODE_NIGHT_YES -> R.id.dark
            else -> R.id.auto
        })


        val activity: AppCompatActivity = activity as AppCompatActivity

        toolbar = view.findViewById(R.id.settings_toolbar) as Toolbar

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        activity.supportActionBar?.setTitle("")

        light = view.findViewById(R.id.light)
        dark = view.findViewById(R.id.dark)



        return view
    }

    private val menuHost: MenuHost get() = requireActivity()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == android.R.id.home){
                    callbacks?.onBackPressedSearch()
                    return true
                }
                return false
            }

        }, viewLifecycleOwner)



        radioButtonGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.auto -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    settingsViewModel.updateThemeType(ThemeType.SYSTEM)
                }
                R.id.light -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    settingsViewModel.updateThemeType(ThemeType.LIGHT)
                }
                R.id.dark -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    settingsViewModel.updateThemeType(ThemeType.DARK)
                }
        }
        }


    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }
}