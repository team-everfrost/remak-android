package com.example.remak.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R

class SearchHistoryRVAdapter (val history : List<String>, private val itemClickListener : OnItemClickListener) : RecyclerView.Adapter<SearchHistoryRVAdapter.SearchHistoryRVViewHolder>() {

    inner class SearchHistoryRVViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val deleteBtn : TextView = view.findViewById(R.id.deleteBtn)
        init {
            deleteBtn.setOnClickListener {

            }

            view.setOnClickListener {
            }
        }
    }

    interface OnItemClickListener {
        fun onItemViewClick(position : Int)
        fun onDeleteBtnClick(position : Int)
    }

    override fun getItemCount(): Int {
        return history.size
    }

    override fun onBindViewHolder(holder: SearchHistoryRVViewHolder, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryRVViewHolder {
        TODO("Not yet implemented")
    }

}