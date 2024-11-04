package com.example.weatherappxml.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.weatherappxml.R
import com.example.weatherappxml.data.api.model.SettingsModel
import com.example.weatherappxml.data.api.model.ThemeTypeState
import com.example.weatherappxml.data.repository.ThemeType
import com.example.weatherappxml.di.ModelProvider
import com.example.weatherappxml.ui.search.SearchFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.StateFlow

class SettingsFragment: Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var radioButtonGroup: RadioGroup
    private lateinit var light: RadioButton
    private lateinit var dark: RadioButton
    private var controller: NavController? = null
    private lateinit var settingsState: StateFlow<ThemeTypeState>
    private lateinit var unlogUser: Button
    private lateinit var logUser: LinearLayout
    private lateinit var logout: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var userName: TextView

    private val settingsViewModel: SettingsModel by activityViewModels {
        ModelProvider.Factory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = findNavController()
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

        auth = Firebase.auth
        logUser = view.findViewById(R.id.log_user)
        unlogUser = view.findViewById(R.id.unlog_user)
        logout = view.findViewById(R.id.logout)
        userName = view.findViewById(R.id.user_name)

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

        val currentUser = auth.currentUser
        if(currentUser != null) {
            logUser.visibility = View.VISIBLE
            userName.text = getString(R.string.user, auth.currentUser?.email)
            unlogUser.visibility = View.GONE
        } else {
            unlogUser.visibility = View.VISIBLE
            logUser.visibility = View.GONE
        }

        logout.setOnClickListener {
            auth.signOut()
            logUser.visibility = View.GONE
            unlogUser.visibility = View.VISIBLE
        }

        unlogUser.setOnClickListener {
            controller?.navigate(R.id.action_settingsFragment_to_loginFragment)
        }

        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == android.R.id.home){
                    controller?.navigateUp()
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
        controller = null
    }
}