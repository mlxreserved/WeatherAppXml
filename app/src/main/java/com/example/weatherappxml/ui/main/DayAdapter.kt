package com.example.weatherappxml.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherappxml.R
import com.example.weatherappxml.databinding.ListItemDayBinding
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class DayAdapter(val items: List<ListItem.DayItem>): RecyclerView.Adapter<DayAdapter.ViewHolder>() {

    var onItemClick: ((ListItem.DayItem, Int) -> Unit)? = null

    fun formatDate(date: String, week: Boolean, short: Boolean): String{
        val dateFormatter = if(week) {
            if(short){
                SimpleDateFormat("E")
            } else {
                SimpleDateFormat("EEEE")
            }
        }
        else {
            if(short){
                SimpleDateFormat("dd")
            } else {
                SimpleDateFormat("dd MMMM")
            }
        }

        val formatedDate = dateFormatter.format(SimpleDateFormat("yyyy-MM-dd").parse(date))

        /*LocalDate.parse(date,formatter)*/
        return formatedDate

    }

    inner class ViewHolder(private val listItemDayBinding: ListItemDayBinding): RecyclerView.ViewHolder(listItemDayBinding.root){
        init {
            itemView.setOnClickListener{
                onItemClick?.invoke(items[absoluteAdapterPosition], absoluteAdapterPosition)
            }
        }

        fun bind(item: ListItem.DayItem){
            listItemDayBinding.dayDate.text = formatDate(item.forecast.date, week = false, short = false )
            listItemDayBinding.dayWeek.text = formatDate(item.forecast.date, week = true, short = false)
            listItemDayBinding.dayImage.load("https://${item.forecast.day.condition.icon}")
            listItemDayBinding.tempOfDay.text = itemView.context.getString(R.string.day_temperature, item.forecast.day.maxtemp_c.roundToInt())
            listItemDayBinding.tempOfNight.text = itemView.context.getString(R.string.night_temperature, item.forecast.day.mintemp_c.roundToInt())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItemDayBinding: ListItemDayBinding = ListItemDayBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(listItemDayBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size


}