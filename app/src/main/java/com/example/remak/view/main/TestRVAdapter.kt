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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)

        return TestRVViewHolder(view)
    }

    override fun getItemCount(): Int {
        return testData.size
    }

    override fun onBindViewHolder(holder: TestRVViewHolder, position: Int) {
    }

}


