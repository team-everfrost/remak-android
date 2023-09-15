package com.example.remak.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R

class LinkTagRVAdapter(
    var tags: List<String?>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<LinkTagRVAdapter.LinkTagRVViewHolder>() {
    inner class LinkTagRVViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tagName: TextView = view.findViewById(R.id.tagName)

        init {
            view.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LinkTagRVAdapter.LinkTagRVViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_link_tag, parent, false)

        return LinkTagRVViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun onBindViewHolder(holder: LinkTagRVViewHolder, position: Int) {
        holder.tagName.text = "#${tags[position]}"
    }
}

class SpacingItemDecoration(private val horizontalSpacing: Int, private val verticalSpacing: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        outRect.right = horizontalSpacing
        outRect.left = horizontalSpacing
        outRect.top = verticalSpacing
        outRect.bottom = verticalSpacing
    }
}