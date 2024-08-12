package com.example.weatherappxml.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherappxml.R
import com.example.weatherappxml.data.api.model.Forecastday
import com.example.weatherappxml.data.api.model.Hour
import kotlin.math.roundToInt

/*
class ForecastAdapter(private val items: List<Hour>): RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val partOfDay: TextView = itemView.findViewById(R.id.part_of_day)
        private val imageOfDay: ImageView = itemView.findViewById(R.id.image_of_part)
        private val tempOfDay: TextView = itemView.findViewById(R.id.temp_of_part)

        fun bind(item: Hour, position: Int){

            partOfDay.text = when(position){
                0 -> "Morning"
                1 -> "Day"
                2 -> "Evening"
                else -> "Night"
            }
            imageOfDay.load("https://${item.condition.icon}")
            tempOfDay.text = "${item.temp_c.roundToInt()}°"
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_forecast, parent, false)
        return ForecastViewHolder(item)
    }

    override fun onBindViewHolder(holder: ForecastAdapter.ForecastViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = items.size
}*/
