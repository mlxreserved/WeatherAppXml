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

    inner class HourHolder(view: View): RecyclerView.ViewHolder(view){
        private val hourTextView: TextView = itemView.findViewById(R.id.item_hour)
        private val hourTempTextView: TextView = itemView.findViewById(R.id.item_hour_temp)
        private val hourImage: ImageView = itemView.findViewById(R.id.item_hour_image)

        fun bind(item: ListItem.HourItem) {


            hourTextView.text=formatTime(item.hour.time, false)
            /*hourTextView.text = if(item.hour.time.substring(item.hour.time.length-5) != "00:00"){

                formatTime(item.hour.time, false)
            } else {
                formatTime(item.hour.time, true)
            }*/
            hourTempTextView.text = "${item.hour.temp_c.roundToInt()}°"
            hourImage.load("https://${item.hour.condition.icon}")
        }
    }

    inner class WeatherHolder(view: View): RecyclerView.ViewHolder(view){
        private val weatherTempTextView: TextView = itemView.findViewById(R.id.temperature) as TextView
        private val weatherForecastTextView: TextView = itemView.findViewById(R.id.weather_forecast) as TextView
        private val weatherImage: ImageView = itemView.findViewById(R.id.weather_image) as ImageView

        fun bind(item: ListItem.WeatherItem) {
            weatherTempTextView.text = "${item.currentWeather.current.tempCelsius.roundToInt()}°"
            weatherForecastTextView.text = item.currentWeather.current.condition.text
            weatherImage.load("https://${item.currentWeather.current.condition.icon}")
        }
    }


    inner class DayHolder(view: View): RecyclerView.ViewHolder(view){
        val recyclerView: RecyclerView = itemView.findViewById(R.id.sub_recycler_view)

        fun bind(childLayoutManager: LinearLayoutManager){
            recyclerView.layoutManager = childLayoutManager
            val adapter = DayAdapter(secondRecItems)
            recyclerView.adapter = adapter
            adapter.onItemClick = onItemClick

            recyclerView.setRecycledViewPool(viewPool)
        }
    }

    inner class StringHolder(view: View): RecyclerView.ViewHolder(view){
        private val stringItem: TextView = itemView.findViewById(R.id.string_item)

        fun bind(item: ListItem.StringItem){
            stringItem.text = item.string
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
                0 -> WeatherHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_weather, parent, false)
                )
                1 -> DayHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_recycler_view_item, parent, false)
                )
                2 -> HourHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_hour, parent, false)
                )
                3 -> StringHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_string, parent, false)
                )
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
                /*(holder as DayHolder).bind(item)*/
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