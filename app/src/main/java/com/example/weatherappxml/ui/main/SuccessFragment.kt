package com.example.weatherappxml.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherappxml.R
import com.example.weatherappxml.data.api.model.ThemeModel
import com.example.weatherappxml.data.repository.ThemeType
import com.example.weatherappxml.databinding.FragmentSuccessBinding
import com.example.weatherappxml.di.ModelProvider
import com.example.weatherappxml.ui.WeatherViewModel
import com.example.weatherappxml.ui.model.SearchUiState
import com.example.weatherappxml.ui.model.WeatherUiState
import com.example.weatherappxml.utils.WeatherResult
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SuccessFragment : Fragment() {


    private lateinit var binding: FragmentSuccessBinding
    private lateinit var weatherState: StateFlow<WeatherUiState>
    private lateinit var searchState: StateFlow<SearchUiState>
    private var controller: NavController? = null
    private var adapter: WeatherAdapter? = null

    private val weatherViewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.Factory
    }

    private val themeModel: ThemeModel by activityViewModels {
        ModelProvider.Factory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller = findNavController()
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
        binding = FragmentSuccessBinding.inflate(layoutInflater, container, false)

        val activity: AppCompatActivity = activity as AppCompatActivity

        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.apply {
            setTitle("")
            setHomeAsUpIndicator(R.drawable.baseline_settings_24)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.weatherRecyclerView.layoutManager = LinearLayoutManager(context)


        return binding.root
    }


    private val menuHost: MenuHost get() = requireActivity()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_search) {
                    controller?.navigate(R.id.action_successFragment_to_searchFragment)
                    return true
                }
                if (menuItem.itemId == android.R.id.home) {
                    controller?.navigate(R.id.action_successFragment_to_settingsFragment)
                    return true
                }

                return false
            }
        }, viewLifecycleOwner)


        binding.refreshLayout.setOnRefreshListener {
            val city = when (weatherState.value.result) {
                is WeatherResult.Success -> (weatherState.value.result as WeatherResult.Success).data.location.name
                is WeatherResult.Error, WeatherResult.Loading -> ""
            }
            weatherViewModel.getWeather(city, true)

            binding.refreshLayout.isRefreshing = false
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherState.collect { res ->
                    when (val weather = res.result) {
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
        for (i in weatherState.value.hourList) {
            if (i.time.substring(i.time.length - 5) == "00:00") {
                list.add(ListItem.StringItem(activity?.getString(R.string.tomorrow) ?: ""))
            }
            list.add(ListItem.HourItem(i))
        }
        /*val list: List<ListItem> = listOf(ListItem.WeatherItem(res.data)) + listOf(ListItem.StringItem(activity?.getString(R.string.three_day_forecast) ?: "")) + listOf(ListItem.DayItem(res.data.forecast.forecastday[0])) +
                listOf(ListItem.StringItem(activity?.getString(R.string.today) ?: "")) + weatherState.value.hourList.map{ListItem.HourItem(it)}*/
        binding.loadingbar.visibility = View.GONE
        binding.image.visibility = View.GONE
        binding.weatherRecyclerView.visibility = View.VISIBLE
        binding.cityName.text = res.data.location.name
        adapter = WeatherAdapter(list, res.data.forecast.forecastday.map { ListItem.DayItem(it) })
        binding.weatherRecyclerView.adapter = adapter
        adapter?.onItemClick = { it, index ->
            weatherViewModel.setCurrentItem(index)

            controller?.navigate(R.id.action_successFragment_to_forecastFragment)



            Log.e("Forecast", it.forecast.date)
        }
    }

    fun updateUiLoading() {
        binding.weatherRecyclerView.visibility = View.GONE
        binding.image.visibility = View.GONE
        binding.loadingbar.visibility = View.VISIBLE
    }

    fun updateUiError() {
        binding.weatherRecyclerView.visibility = View.GONE
        binding.loadingbar.visibility = View.GONE
        binding.image.visibility = View.VISIBLE
    }

    override fun onDetach() {
        super.onDetach()
        controller = null
    }
}
