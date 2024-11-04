package com.example.weatherappxml.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappxml.R

class SearchAdapter(val items: List<String>): RecyclerView.Adapter<SearchAdapter.SearchHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    inner class SearchHolder(view: View): RecyclerView.ViewHolder(view){

        private val city: TextView = itemView.findViewById(R.id.search_city)

        init {
            itemView.setOnClickListener{
                onItemClick?.invoke(items[absoluteAdapterPosition])
            }
        }

        fun bind(item: String) {
            city.text = item
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_search, parent, false)
        return SearchHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}