package com.example.weatherappxml.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherappxml.R
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

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val dayDateTextView: TextView = itemView.findViewById(R.id.day_date)
        private val dayWeekTextDate: TextView = itemView.findViewById(R.id.day_week)
        private val dayImage: ImageView = itemView.findViewById(R.id.day_image)
        private val dayTempOfDayTextView: TextView = itemView.findViewById(R.id.temp_of_day)
        private val dayTempOfNightTextView: TextView = itemView.findViewById(R.id.temp_of_night)

        init {
            itemView.setOnClickListener{
                onItemClick?.invoke(items[absoluteAdapterPosition], absoluteAdapterPosition)
            }
        }

        fun bind(item: ListItem.DayItem){
            dayDateTextView.text = formatDate(item.forecast.date, week = false, short = false )
            dayWeekTextDate.text = formatDate(item.forecast.date, week = true, short = false)
            dayImage.load("https://${item.forecast.day.condition.icon}")
            dayTempOfDayTextView.text = itemView.context.getString(R.string.day_temperature, item.forecast.day.maxtemp_c.roundToInt())
            dayTempOfNightTextView.text = itemView.context.getString(R.string.night_temperature, item.forecast.day.mintemp_c.roundToInt())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_day, parent, false))
    }

    override fun onBindViewHolder(holder: DayAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size


}