package com.example.remak.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R
import com.example.remak.network.model.CollectionListData

class CollectionRVAdapter(
    var collectionData: List<CollectionListData.Data>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CollectionRVAdapter.CollectionRVViewHolder>() {
    inner class CollectionRVViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.collectionNameText)
        val description: TextView = view.findViewById(R.id.collectionDescriptionText)
        val count: TextView = view.findViewById<TextView>(R.id.collectionCount)

        init {
            view.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return collectionData.size
    }

    override fun onBindViewHolder(holder: CollectionRVViewHolder, position: Int) {
        val count = collectionData[position].count
        holder.title.text = collectionData[position].name
        holder.description.text = collectionData[position].description
        if (count == 0) {
            holder.count.text = "${collectionData[position].count}개"
        } else {
            holder.count.text = HtmlCompat.fromHtml(
                "<font color=\"#1F8CE6\">${collectionData[position].count}개",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionRVViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_collection, parent, false)
        return CollectionRVViewHolder(view)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}

class SpacingItemDecorator(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % 2 // 2는 열의 수입니다.
        if (column == 0) {
            outRect.left = spacing
            outRect.right = spacing / 2
        } else {
            outRect.left = spacing / 2
            outRect.right = spacing
        }

        outRect.top = spacing
    }
}
