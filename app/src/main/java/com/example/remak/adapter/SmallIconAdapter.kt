package com.example.remak.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R

class SmallIconAdapter(var dataSet : List<List<String>>) : RecyclerView.Adapter<SmallIconAdapter.SmallIconViewHolder>() {

    inner class SmallIconViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val titleText = view.findViewById(R.id.titleTextView) as TextView
        val contentText = view.findViewById(R.id.contentTextView) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallIconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sign_in_small_icon, parent, false)
        return SmallIconViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: SmallIconViewHolder, position: Int) {
        holder.titleText.text = dataSet[position][0]
        holder.contentText.text = dataSet[position][1]

    }
}

class SmallIconItemOffsetDecoration(private val mItemOffset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = mItemOffset
        // Add top margin only for the first item to avoid double space between items
    }

}