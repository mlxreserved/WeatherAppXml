package com.example.weatherappxml.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappxml.R
import com.example.weatherappxml.databinding.ListItemSearchBinding

class SearchAdapter(val items: List<String>): RecyclerView.Adapter<SearchAdapter.SearchHolder>() {


    var onItemClick: ((String) -> Unit)? = null


    inner class SearchHolder(private val binding: ListItemSearchBinding): RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener{
                onItemClick?.invoke(items[absoluteAdapterPosition])
            }
        }

        fun bind(item: String) {
            binding.searchCity.text = item
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemSearchBinding.inflate(layoutInflater, parent, false)
        return SearchHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}