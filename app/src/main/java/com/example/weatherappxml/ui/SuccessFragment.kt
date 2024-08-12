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
import androidx.appcompat.app.AppCompatActivity
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
import coil.transform.CircleCropTransformation
import com.example.weatherappxml.R
import com.example.weatherappxml.utils.WeatherResult
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SuccessFragment: Fragment() {

     interface Callbacks{
         fun onSearchSelected()
         fun onForecastSelected()
     }

     private var callbacks: Callbacks? = null

     private lateinit var cityNameTextView: TextView
     private lateinit var loading: CircularProgressIndicator
     private lateinit var weatherState: StateFlow<WeatherState>
     private lateinit var searchState: StateFlow<SearchState>
     private lateinit var weatherRecyclerView: RecyclerView
     private lateinit var toolbar: androidx.appcompat.widget.Toolbar
     private var adapter: WeatherAdapter? = null

     private val weatherViewModel: WeatherViewModel by activityViewModels {
         WeatherViewModel.Factory
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
                    Log.e("Home", activity?.supportFragmentManager?.backStackEntryCount.toString())
                    return true
                }

                return false
            }
        }, viewLifecycleOwner)



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
                 list.add(ListItem.StringItem("Tomorrow"))
             }
             list.add(ListItem.HourItem(i))
         }
         /*val list: List<ListItem> = listOf(ListItem.WeatherItem(res.data)) + listOf(ListItem.StringItem(activity?.getString(R.string.three_day_forecast) ?: "")) + listOf(ListItem.DayItem(res.data.forecast.forecastday[0])) +
                 listOf(ListItem.StringItem(activity?.getString(R.string.today) ?: "")) + weatherState.value.hourList.map{ListItem.HourItem(it)}*/
         loading.visibility = View.GONE
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
        loading.visibility = View.VISIBLE
    }

     override fun onDetach() {
         super.onDetach()
         callbacks = null
     }
}
