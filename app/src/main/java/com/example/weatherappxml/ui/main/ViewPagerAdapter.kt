package com.example.weatherappxml.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherappxml.R
import com.example.weatherappxml.data.api.model.Hour
import com.example.weatherappxml.databinding.ItemForecastBinding
import kotlin.math.roundToInt

class ViewPagerAdapter(private val items: List<ListItem.DayItem>): RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder>() {


    inner class ViewPagerHolder(private val itemForecastBinding: ItemForecastBinding): RecyclerView.ViewHolder(itemForecastBinding.root){
        fun bind(item: List<Hour>){
            itemForecastBinding.partOfDay1.setText(R.string.morning)
            itemForecastBinding.partOfDay2.setText(R.string.day)
            itemForecastBinding.partOfDay3.setText(R.string.evening)
            itemForecastBinding.partOfDay4.setText(R.string.night)

            itemForecastBinding.imageOfPart1.load("https://${item[5].condition.icon}")
            itemForecastBinding.imageOfPart2.load("https://${item[11].condition.icon}")
            itemForecastBinding.imageOfPart3.load("https://${item[17].condition.icon}")
            itemForecastBinding.imageOfPart4.load("https://${item[23].condition.icon}")

            itemForecastBinding.tempOfPart1.text = "${item[5].temp_c.roundToInt()}°"
            itemForecastBinding.tempOfPart2.text = "${item[11].temp_c.roundToInt()}°"
            itemForecastBinding.tempOfPart3.text = "${item[17].temp_c.roundToInt()}°"
            itemForecastBinding.tempOfPart4.text = "${item[23].temp_c.roundToInt()}°"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemForecastBinding: ItemForecastBinding = ItemForecastBinding.inflate(layoutInflater, parent, false)
        return ViewPagerHolder(itemForecastBinding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(items[position].forecast.hour)
    }
}