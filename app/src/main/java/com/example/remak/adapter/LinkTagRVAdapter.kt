package com.example.remak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R

class LinkTagRVAdapter (var tags : List<String>) : RecyclerView.Adapter<LinkTagRVAdapter.LinkTagRVViewHolder>() {
    inner class LinkTagRVViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tagName = view.findViewById<TextView>(R.id.tagName)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkTagRVAdapter.LinkTagRVViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_link_tag, parent, false)

        return LinkTagRVViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun onBindViewHolder(holder: LinkTagRVViewHolder, position: Int) {
        holder.tagName.text = "#${tags[position]}"
    }
}