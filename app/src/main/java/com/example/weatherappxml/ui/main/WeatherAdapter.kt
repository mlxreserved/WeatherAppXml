package com.example.weatherappxml.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherappxml.R
import com.example.weatherappxml.data.api.model.Forecastday
import com.example.weatherappxml.data.api.model.Hour
import com.example.weatherappxml.data.api.model.Weather
import com.example.weatherappxml.databinding.ListItemHourBinding
import com.example.weatherappxml.databinding.ListItemSearchBinding
import com.example.weatherappxml.databinding.ListItemStringBinding
import com.example.weatherappxml.databinding.ListItemWeatherBinding
import com.example.weatherappxml.databinding.ListRecyclerViewItemBinding
import java.text.SimpleDateFormat
import kotlin.math.roundToInt


class WeatherAdapter(private val items: List<ListItem>, private val secondRecItems: List<ListItem.DayItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClick: ((ListItem.DayItem, Int) -> Unit)? = null

    private val viewPool = RecyclerView.RecycledViewPool()

    fun formatTime(time: String, isNextDay: Boolean): String{
        if(!isNextDay){
            val timeFormatter = SimpleDateFormat("HH:mm")
            val formattedTime = timeFormatter.format(SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time))
            return formattedTime
        } else {
            val timeFormatter = SimpleDateFormat("HH:mm d MMM")
            val formattedTime = timeFormatter.format(SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time))
            return formattedTime
        }
    }

    inner class HourHolder(private val listItemHourBinding: ListItemHourBinding): RecyclerView.ViewHolder(listItemHourBinding.root){
        fun bind(item: ListItem.HourItem) {
            listItemHourBinding.itemHour.text=formatTime(item.hour.time, false)
            listItemHourBinding.itemHourTemp.text = "${item.hour.temp_c.roundToInt()}°"
            listItemHourBinding.itemHourImage.load("https://${item.hour.condition.icon}")
        }
    }

    inner class WeatherHolder(private val listItemWeatherBinding: ListItemWeatherBinding): RecyclerView.ViewHolder(listItemWeatherBinding.root){
        fun bind(item: ListItem.WeatherItem) {
            listItemWeatherBinding.temperature.text = "${item.currentWeather.current.tempCelsius.roundToInt()}°"
            listItemWeatherBinding.weatherForecast.text = item.currentWeather.current.condition.text
            listItemWeatherBinding.weatherImage.load("https://${item.currentWeather.current.condition.icon}")
        }
    }


    inner class DayHolder(private val listRecyclerViewItemBinding: ListRecyclerViewItemBinding): RecyclerView.ViewHolder(listRecyclerViewItemBinding.root){
        val recyclerView: RecyclerView = listRecyclerViewItemBinding.subRecyclerView

        fun bind(childLayoutManager: LinearLayoutManager){
            recyclerView.layoutManager = childLayoutManager
            val adapter = DayAdapter(secondRecItems)
            recyclerView.adapter = adapter
            adapter.onItemClick = onItemClick

            listRecyclerViewItemBinding.subRecyclerView.setRecycledViewPool(viewPool)
        }
    }

    inner class StringHolder(private val listItemStringBinding: ListItemStringBinding): RecyclerView.ViewHolder(listItemStringBinding.root){
        fun bind(item: ListItem.StringItem){
            listItemStringBinding.stringItem.text = item.string
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){
            is ListItem.WeatherItem -> 0
            is ListItem.DayItem -> 1
            is ListItem.HourItem -> 2
            is ListItem.StringItem -> 3
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {
                0 -> {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val listItemWeatherBinding: ListItemWeatherBinding = ListItemWeatherBinding.inflate(layoutInflater, parent, false)
                    WeatherHolder(listItemWeatherBinding)
                }
                1 -> {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val listRecyclerViewItemBinding: ListRecyclerViewItemBinding = ListRecyclerViewItemBinding.inflate(layoutInflater, parent, false)
                    DayHolder(listRecyclerViewItemBinding)
                }
                2 -> {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val listItemHourBinding: ListItemHourBinding = ListItemHourBinding.inflate(layoutInflater, parent, false)
                    HourHolder(listItemHourBinding)
                }
                3 -> {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val listItemStringBinding: ListItemStringBinding = ListItemStringBinding.inflate(layoutInflater, parent, false)
                    StringHolder(listItemStringBinding)
                }
                else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(val item = items[position]){
            is ListItem.WeatherItem -> (holder as WeatherHolder).bind(item)
            is ListItem.DayItem -> {
                val childlayoutManager = LinearLayoutManager(
                    (holder as DayHolder).recyclerView.context, RecyclerView.HORIZONTAL, false)
                childlayoutManager.initialPrefetchItemCount = secondRecItems.size

                holder.bind(childlayoutManager)
            }
            is ListItem.HourItem -> (holder as HourHolder).bind(item)
            is ListItem.StringItem -> (holder as StringHolder).bind(item)
        }
    }
}



sealed class ListItem{
    data class WeatherItem(val currentWeather: Weather): ListItem()
    data class DayItem(val forecast: Forecastday): ListItem()
    data class HourItem(val hour: Hour): ListItem()
    data class StringItem(val string: String): ListItem()
}