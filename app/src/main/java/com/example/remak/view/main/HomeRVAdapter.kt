package com.example.remak.view.main

import android.graphics.Rect
import android.icu.text.Transliterator.Position
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R
import com.example.remak.network.model.MainListData

class HomeRVAdapter(var dataSet : List<MainListData.Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        private const val MEMO = "MEMO"
        private const val MEMO_VIEW_TYPE = 1
        private const val FILE_VIEW_TYPE = 0
    }
    inner class MemoViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.title)

    }

    class FileViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.title)

    }

    override fun getItemViewType(position: Int): Int = when (dataSet[position].type) {
        MEMO -> MEMO_VIEW_TYPE
        else -> FILE_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MEMO_VIEW_TYPE -> {
                Log.d("HomeRVAdapter", "onCreateViewHolder: MEMO_VIEW_TYPE")
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
                MemoViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
                FileViewHolder(view)
            }
        }



    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is MemoViewHolder -> {
                holder.title.text = dataSet[position].content
            }
            is FileViewHolder -> {
                holder.title.text = dataSet[position].content
            }
        }
    }

}

class ItemOffsetDecoration(private val mItemOffset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = mItemOffset

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mItemOffset
        }
    }
}