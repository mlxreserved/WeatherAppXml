package com.example.weatherappxml.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.transform.CircleCropTransformation
import com.example.weatherappxml.R
import com.example.weatherappxml.data.api.model.ThemeModel
import com.example.weatherappxml.data.api.model.Weather
import com.example.weatherappxml.data.repository.ThemeType
import com.example.weatherappxml.di.ModelProvider
import com.example.weatherappxml.utils.WeatherResult
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SuccessFragment: Fragment() {

     interface Callbacks{
         fun onSearchSelected()
         fun onForecastSelected()
         fun onSettingsSelected()
     }

     private var callbacks: Callbacks? = null

     private lateinit var cityNameTextView: TextView
     private lateinit var loading: CircularProgressIndicator
     private lateinit var weatherState: StateFlow<WeatherState>
     private lateinit var searchState: StateFlow<SearchState>
     private lateinit var weatherRecyclerView: RecyclerView
     private lateinit var toolbar: androidx.appcompat.widget.Toolbar
     private lateinit var swipeRefreshLayout: SwipeRefreshLayout
     private lateinit var image: ImageView
     private var adapter: WeatherAdapter? = null

     private val weatherViewModel: WeatherViewModel by activityViewModels {
         WeatherViewModel.Factory
     }

     private val themeModel: ThemeModel by activityViewModels{
         ModelProvider.Factory
     }

     override fun onAttach(context: Context) {
         super.onAttach(context)
         callbacks = context as Callbacks?
     }

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         weatherState = weatherViewModel.weatherState
         searchState = weatherViewModel.searchState
     }

     override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View? {
         val view = inflater.inflate(R.layout.fragment_success, container, false)

         toolbar = view.findViewById(R.id.toolbar) as androidx.appcompat.widget.Toolbar

         val activity: AppCompatActivity = activity as AppCompatActivity

         activity.setSupportActionBar(toolbar)
         activity.supportActionBar?.apply{
             setTitle("")
             setHomeAsUpIndicator(R.drawable.baseline_settings_24)
             setDisplayHomeAsUpEnabled(true)
         }

         loading = view.findViewById(R.id.loadingbar)
         cityNameTextView = view.findViewById(R.id.city_name) as TextView
         weatherRecyclerView = view.findViewById(R.id.weather_recycler_view) as RecyclerView
         swipeRefreshLayout = view.findViewById(R.id.refreshLayout) as SwipeRefreshLayout
         image = view.findViewById(R.id.image) as ImageView

         weatherRecyclerView.layoutManager = LinearLayoutManager(context)


         return view
     }


    private val menuHost: MenuHost get() = requireActivity()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        menuHost.addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == R.id.action_search){
                    callbacks?.onSearchSelected()
                    return true
                }
                if(menuItem.itemId == android.R.id.home){
                    callbacks?.onSettingsSelected()
                    return true
                }

                return false
            }
        }, viewLifecycleOwner)


        swipeRefreshLayout.setOnRefreshListener {
            val city = when(weatherState.value.result){
                is WeatherResult.Success -> (weatherState.value.result as WeatherResult.Success).data.location.name
                is WeatherResult.Error, WeatherResult.Loading -> ""
            }
            weatherViewModel.getWeather(city, true)

            swipeRefreshLayout.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                themeModel.isDarkTheme.collect { theme ->
                    val isDarkTheme = when (theme) {
                        ThemeType.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        ThemeType.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                        ThemeType.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    }

                    AppCompatDelegate.setDefaultNightMode(isDarkTheme)
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                weatherState.collect{ res ->
                    when(val weather = res.result){
                        is WeatherResult.Success -> {
                            updateUiSuccess(weather)
                        }
                        is WeatherResult.Loading -> {
                            updateUiLoading()
                        }
                        is WeatherResult.Error -> {
                            updateUiError()
                        }
                    }
                }
            }
        }
    }

     fun updateUiSuccess(res: WeatherResult.Success) {
         val list: MutableList<ListItem> = mutableListOf(ListItem.WeatherItem(res.data))
         list.add(ListItem.StringItem(activity?.getString(R.string.three_day_forecast) ?: ""))
         list.add(ListItem.DayItem(res.data.forecast.forecastday[0]))
         list.add(ListItem.StringItem(activity?.getString(R.string.today) ?: ""))
         for(i in weatherState.value.hourList){
             if(i.time.substring(i.time.length-5)  == "00:00"){
                 list.add(ListItem.StringItem(activity?.getString(R.string.tomorrow) ?: ""))
             }
             list.add(ListItem.HourItem(i))
         }
         /*val list: List<ListItem> = listOf(ListItem.WeatherItem(res.data)) + listOf(ListItem.StringItem(activity?.getString(R.string.three_day_forecast) ?: "")) + listOf(ListItem.DayItem(res.data.forecast.forecastday[0])) +
                 listOf(ListItem.StringItem(activity?.getString(R.string.today) ?: "")) + weatherState.value.hourList.map{ListItem.HourItem(it)}*/
         loading.visibility = View.GONE
         image.visibility = View.GONE
         weatherRecyclerView.visibility = View.VISIBLE
         cityNameTextView.text = res.data.location.name
         adapter = WeatherAdapter(list, res.data.forecast.forecastday.map { ListItem.DayItem(it) })
         weatherRecyclerView.adapter = adapter
         adapter?.onItemClick = { it, index ->
             weatherViewModel.setCurrentItem(index)
             callbacks?.onForecastSelected()
             Log.e("Forecast", it.forecast.date)
         }
     }

    fun updateUiLoading(){
        weatherRecyclerView.visibility = View.GONE
        image.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    fun updateUiError(){
        weatherRecyclerView.visibility = View.GONE
        loading.visibility = View.GONE
        image.visibility = View.VISIBLE
    }
     override fun onDetach() {
         super.onDetach()
         callbacks = null
     }
}
