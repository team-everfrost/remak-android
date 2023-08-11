package com.example.remak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R
import com.example.remak.network.model.TagListData

class TagRVAdapter (var tagData : List<TagListData.Data>,private val itemClickListener :  OnItemClickListener) : RecyclerView.Adapter<TagRVAdapter.TagRVViewHolder>(){

    inner class TagRVViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.tagName)
        val count: TextView = view.findViewById<TextView>(R.id.tagCount)
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }

    }

    fun getTagName(adapterPosition : Int) : String {
        return tagData[adapterPosition].name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagRVViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)

        return TagRVViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tagData.size
    }

    override fun onBindViewHolder(holder: TagRVViewHolder, position: Int) {
        holder.title.text = "#${tagData[position].name}"
        holder.count.text = tagData[position].count.toString()
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}




