package com.example.weatherappxml.ui

import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappxml.R
import com.example.weatherappxml.ui.SearchAdapter.SearchHolder

class DatabaseAdapter(private val items: List<String>): RecyclerView.Adapter<DatabaseAdapter.DatabaseHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    inner class DatabaseHolder(view: View): RecyclerView.ViewHolder(view) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatabaseHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_search, parent, false)
        return DatabaseHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DatabaseHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

}