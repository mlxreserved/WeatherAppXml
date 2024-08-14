package com.example.weatherappxml.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappxml.R
import com.example.weatherappxml.data.api.model.Coordinate
import com.example.weatherappxml.utils.WeatherResult
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchFragment: Fragment() {

    interface Callbacks{
        fun onBackPressedSearch()
    }

    private lateinit var titleField: EditText
    private lateinit var clearButton: Button
    private lateinit var weatherState: StateFlow<WeatherState>
    private lateinit var searchState: StateFlow<SearchState>
    private lateinit var databaseState: StateFlow<DatabaseState>
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerView: RecyclerView
    private var callbacks: Callbacks? = null
    private var adapter: SearchAdapter? = SearchAdapter(emptyList())

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
        databaseState = weatherViewModel.databaseState
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        titleField = view.findViewById(R.id.search_edit_text) as EditText
        clearButton = view.findViewById(R.id.btn_clear) as Button
        recyclerView = view.findViewById(R.id.search_recycler_view) as RecyclerView

        recyclerView.layoutManager = LinearLayoutManager(context)

        toolbar = view.findViewById(R.id.toolbar) as androidx.appcompat.widget.Toolbar

        val activity: AppCompatActivity = activity as AppCompatActivity

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)


        clearButton.setOnClickListener {
            titleField.setText("")
            weatherViewModel.updateSearchText("")
        }

        titleField.setOnEditorActionListener{_, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                try{
                    val city = searchState.value.textFieldCity
                    weatherViewModel.getMultiCoordinate(city)
                } catch (e: Exception) {
                    Log.e("SearchFragment", e.message.toString())
                }
                true
            }
            false
        }

        return view
    }

    private val menuHost: MenuHost get() = requireActivity()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater){
                menuInflater.inflate(R.menu.search_menu, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == android.R.id.home) {
                    weatherViewModel.closeSearchScreen()
                    Log.e("Home", activity?.supportFragmentManager?.backStackEntryCount.toString())
                    callbacks?.onBackPressedSearch()
                    return true
                }

                return false
            }
        }, viewLifecycleOwner)


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                searchState.collect{ res ->
                    if(res.textFieldCity.isNotBlank()){
                        updateUiSearched()
                    }
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                databaseState.collect{
                    if(searchState.value.textFieldCity.isBlank()){
                        updateUiEmpty()
                    }
                }
            }
        }
    }

    override fun onStart(){
        super.onStart()
        val titleWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?,
                                           start: Int,
                                           count: Int,
                                           after: Int) {

            }

            override fun onTextChanged(s: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                weatherViewModel.updateSearchText(s.toString())
                if(s.toString()!=""){
                    clearButton.visibility = View.VISIBLE
                } else {
                    clearButton.visibility = View.GONE
                    updateUiEmpty()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)
    }


    private fun updateUiEmpty(){
        val listCityName = databaseState.value.storyOfSearch.map { it.name }
        adapter = SearchAdapter(listCityName)
        recyclerView.adapter = adapter
        adapter?.onItemClick = { item ->
            weatherViewModel.getWeather(item, false)
            callbacks?.onBackPressedSearch()
        }
    }

    private fun updateUiSearched(){
        adapter = SearchAdapter(searchState.value.coordinateList)
        recyclerView.adapter = adapter
        adapter?.onItemClick = { item ->
            weatherViewModel.getWeather(item, false)
            callbacks?.onBackPressedSearch()
            Log.e("TAG", item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

}