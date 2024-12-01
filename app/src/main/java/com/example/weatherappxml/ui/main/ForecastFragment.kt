package com.example.weatherappxml.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.weatherappxml.R
import com.example.weatherappxml.databinding.FragmentForecastBinding
import com.example.weatherappxml.ui.WeatherViewModel
import com.example.weatherappxml.ui.model.WeatherUiState
import com.example.weatherappxml.utils.WeatherResult
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.StateFlow

class ForecastFragment: Fragment() {

    private lateinit var binding: FragmentForecastBinding

    private var controller: NavController? = null
    private var adapter: ViewPagerAdapter? = null
    private lateinit var weatherState: StateFlow<WeatherUiState>

    private val weatherViewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.Factory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherState = weatherViewModel.weatherState
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForecastBinding.inflate(layoutInflater, container, false)

        val activity: AppCompatActivity = activity as AppCompatActivity

        activity.setSupportActionBar(binding.forecastToolbar)
        activity.supportActionBar?.setTitle("")
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)


        return binding.root
    }

    private val menuHost: MenuHost get() = requireActivity()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        menuHost.addMenuProvider(object: MenuProvider{
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

        updateUi((weatherState.value.result as WeatherResult.Success))
    }

    fun updateUi(res: WeatherResult.Success){
        adapter = ViewPagerAdapter(res.data.forecast.forecastday.map { ListItem.DayItem(it) })
        binding.viewPager2.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2){tab, position ->
            tab.text = weatherViewModel.formatDate(res.data.forecast.forecastday[position].date, week = true, short = true) + "\n" + weatherViewModel.formatDate(res.data.forecast.forecastday[position].date, week = false, short = true)
        }.attach()
        binding.viewPager2.setCurrentItem(weatherState.value.currentItem, false)
    }

    override fun onDetach() {
        super.onDetach()
        controller = null
    }
}