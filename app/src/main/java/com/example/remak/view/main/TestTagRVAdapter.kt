package com.example.remak.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R

class TestTagRVAdapter (private val testData : Array<String>) : RecyclerView.Adapter<TestTagRVAdapter.TestTagRVViewHolder>(){

    inner class TestTagRVViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestTagRVViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)

        return TestTagRVViewHolder(view)
    }

    override fun getItemCount(): Int {
        return testData.size
    }

    override fun onBindViewHolder(holder: TestTagRVViewHolder, position: Int) {
    }

}



