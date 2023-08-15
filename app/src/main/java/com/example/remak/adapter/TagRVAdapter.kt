package com.example.remak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R
import com.example.remak.network.model.TagListData

class TagRVAdapter (var tagData : List<TagListData.Data>,private val itemClickListener :  OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        private const val COLLECTION = "COLLECTION"
        private const val TAG = "TAG"
        private const val TYPE = "TYPE"
        private const val COLLECTION_VIEW_TYPE = 0
        private const val TAG_VIEW_TYPE = 1
        private const val TYPE_VIEW_TYPE = 2
    }

    inner class CollectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

    inner class TagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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


    inner class TypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val type : TextView = view.findViewById<TextView>(R.id.tagType)
    }

    fun getTagName(adapterPosition : Int) : String {
        return tagData[adapterPosition].name
    }

    override fun getItemViewType(position: Int): Int = when (tagData[position].type) {
        COLLECTION -> COLLECTION_VIEW_TYPE
        TAG -> TAG_VIEW_TYPE
        TYPE -> TYPE_VIEW_TYPE
        else -> throw IllegalArgumentException("Invalid type of data " + position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COLLECTION_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
                CollectionViewHolder(view)
            }
            TAG_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
                TagViewHolder(view)
            }
            TYPE_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag_type, parent, false)
                TypeViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid type of data " + viewType)
        }
    }

    override fun getItemCount(): Int {
        return tagData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CollectionViewHolder -> {
                holder.title.text = tagData[position].name
                holder.count.text = tagData[position].count.toString()
            }
            is TagViewHolder -> {
                holder.title.text = "#${tagData[position].name}"
                holder.count.text = tagData[position].count.toString()
            }
            is TypeViewHolder -> {
                holder.type.text = tagData[position].name
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}




