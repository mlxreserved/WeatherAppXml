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
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherappxml.R
import com.example.weatherappxml.ui.search.SearchFragment
import com.example.weatherappxml.utils.WeatherResult
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.StateFlow

class ForecastFragment: Fragment() {


    private lateinit var toolbar: Toolbar
    private var controller: NavController? = null
    private lateinit var viewPager: ViewPager2
    private var adapter: ViewPagerAdapter? = null
    private lateinit var weatherState: StateFlow<WeatherState>
    private lateinit var tabLayout: TabLayout

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
        val view = inflater.inflate(R.layout.fragment_forecast, container, false)

        toolbar = view.findViewById(R.id.forecast_toolbar) as Toolbar
        viewPager = view.findViewById(R.id.viewPager2) as ViewPager2
        tabLayout = view.findViewById(R.id.tab_layout) as TabLayout

        val activity: AppCompatActivity = activity as AppCompatActivity

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setTitle("")
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)


        return view
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
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            tab.text = weatherViewModel.formatDate(res.data.forecast.forecastday[position].date, week = true, short = true) + "\n" + weatherViewModel.formatDate(res.data.forecast.forecastday[position].date, week = false, short = true)
        }.attach()
        viewPager.setCurrentItem(weatherState.value.currentItem, false)
    }

    override fun onDetach() {
        super.onDetach()
        controller = null
    }
}