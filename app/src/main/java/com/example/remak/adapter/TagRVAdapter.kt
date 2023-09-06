package com.example.remak.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R
import com.example.remak.network.model.TagListData

class TagRVAdapter (private val context : Context, var tagData : List<TagListData.Data>,private val itemClickListener :  OnItemClickListener) : RecyclerView.Adapter<TagRVAdapter.TagRVViewHolder>(){



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

    fun getTagCount(adapterPosition: Int) : Int {
        return tagData[adapterPosition].count
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagRVViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
        return TagRVViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tagData.size
    }

    override fun onBindViewHolder(holder: TagRVViewHolder, position: Int) {
        holder.title.text = tagData[position].name
        holder.count.text = HtmlCompat.fromHtml("<font color=\"#1F8CE6\">${tagData[position].count}개</font><font color=\"#646F7C\">가 있어요</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)

    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}



class TagItemOffsetDecoration(private val mItemOffset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = mItemOffset

        val position = parent.getChildAdapterPosition(view)

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mItemOffset * 2
        }

    }

}




