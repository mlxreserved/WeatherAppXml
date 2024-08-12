package com.example.weatherappxml.ui

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
import kotlin.math.roundToInt

class ViewPagerAdapter(private val items: List<ListItem.DayItem>): RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder>() {


    inner class ViewPagerHolder(view: View): RecyclerView.ViewHolder(view){
        val partOfDay1: TextView = itemView.findViewById(R.id.part_of_day1)
        val partOfDay2: TextView = itemView.findViewById(R.id.part_of_day2)
        val partOfDay3: TextView = itemView.findViewById(R.id.part_of_day3)
        val partOfDay4: TextView = itemView.findViewById(R.id.part_of_day4)

        val imageOfPart1: ImageView = itemView.findViewById(R.id.image_of_part1)
        val imageOfPart2: ImageView = itemView.findViewById(R.id.image_of_part2)
        val imageOfPart3: ImageView = itemView.findViewById(R.id.image_of_part3)
        val imageOfPart4: ImageView = itemView.findViewById(R.id.image_of_part4)

        val tempOfPart1: TextView = itemView.findViewById(R.id.temp_of_part1)
        val tempOfPart2: TextView = itemView.findViewById(R.id.temp_of_part2)
        val tempOfPart3: TextView = itemView.findViewById(R.id.temp_of_part3)
        val tempOfPart4: TextView = itemView.findViewById(R.id.temp_of_part4)

        fun bind(item: List<Hour>){
            partOfDay1.setText(R.string.morning)
            partOfDay2.setText(R.string.day)
            partOfDay3.setText(R.string.evening)
            partOfDay4.setText(R.string.night)

            imageOfPart1.load("https://${item[5].condition.icon}")
            imageOfPart2.load("https://${item[11].condition.icon}")
            imageOfPart3.load("https://${item[17].condition.icon}")
            imageOfPart4.load("https://${item[23].condition.icon}")

            tempOfPart1.text = "${item[5].temp_c.roundToInt()}°"
            tempOfPart2.text = "${item[11].temp_c.roundToInt()}°"
            tempOfPart3.text = "${item[17].temp_c.roundToInt()}°"
            tempOfPart4.text = "${item[23].temp_c.roundToInt()}°"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false)
        return ViewPagerHolder(item)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(items[position].forecast.hour)
    }
}