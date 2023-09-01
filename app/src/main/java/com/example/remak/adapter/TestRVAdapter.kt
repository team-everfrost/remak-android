package com.example.remak.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R

class TestRVAdapter (var testList : List<String>) : RecyclerView.Adapter<TestRVAdapter.TestRVViewHolder>() {

    inner class TestRVViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val titleText = view.findViewById(R.id.titleTextView) as TextView
        val iconImageView = view.findViewById(R.id.iconImageView) as ImageView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestRVViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sign_in_big_icon, parent, false)
        return TestRVViewHolder(view)
    }

    override fun getItemCount(): Int {
        return testList.size
    }

    override fun onBindViewHolder(holder: TestRVViewHolder, position: Int) {
        holder.titleText.text = testList[position]
        if (position % 2 == 0) {
            holder.iconImageView.setImageResource(R.drawable.image_icon)
        } else {
            holder.iconImageView.setImageResource(R.drawable.document_icon)
        }

    }
}

class TestItemOffsetDecoration(private val mItemOffset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = mItemOffset
        // Add top margin only for the first item to avoid double space between items

    }
}

