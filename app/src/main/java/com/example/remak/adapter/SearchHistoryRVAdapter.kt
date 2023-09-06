package com.example.remak.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R

class SearchHistoryRVAdapter (var history : List<String>, private val itemClickListener : OnItemClickListener) : RecyclerView.Adapter<SearchHistoryRVAdapter.SearchHistoryRVViewHolder>() {

    inner class SearchHistoryRVViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val deleteBtn : ImageButton = view.findViewById(R.id.deleteBtn)
        val searchHistoryText : TextView = view.findViewById(R.id.searchHistoryText)
        init {
            deleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onDeleteBtnClick(position)
                }

            }
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemViewClick(position)
                }
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
        holder.searchHistoryText.text = history[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryRVViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_history, parent, false)
        return SearchHistoryRVViewHolder(view)
    }

}


class SearchHistoryItemOffsetDecoration(private val mItemOffset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = mItemOffset
        // Add top margin only for the first item to avoid double space between items

    }
}