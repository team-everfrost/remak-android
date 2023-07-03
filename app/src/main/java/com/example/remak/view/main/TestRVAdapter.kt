package com.example.remak.view.main

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R

class TestRVAdapter (private val testData : Array<String>) : RecyclerView.Adapter<TestRVAdapter.TestRVViewHolder>(){

    inner class TestRVViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestRVViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false)

        return TestRVViewHolder(view)
    }

    override fun getItemCount(): Int {
        return testData.size
    }

    override fun onBindViewHolder(holder: TestRVViewHolder, position: Int) {
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

