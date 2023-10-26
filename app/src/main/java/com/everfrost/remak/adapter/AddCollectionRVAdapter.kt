package com.everfrost.remak.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.everfrost.remak.R
import com.everfrost.remak.network.model.CollectionListData

class AddCollectionRVAdapter(
    var collectionData: List<CollectionListData.Data>,
) : RecyclerView.Adapter<AddCollectionRVAdapter.AddCollectionRVViewHolder>() {

    private fun toggleSelection(position: Int, checkBox: CheckBox) {
        collectionData[position].isSelected = !collectionData[position].isSelected
        checkBox.isChecked = collectionData[position].isSelected
    }

    inner class AddCollectionRVViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val collectionName: TextView = view.findViewById<TextView>(R.id.collectionName)
        val checkBox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position, checkBox)
                }
            }
            checkBox.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position, checkBox)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return collectionData.size
    }

    override fun onBindViewHolder(holder: AddCollectionRVViewHolder, position: Int) {
        holder.collectionName.text = collectionData[position].name
        holder.checkBox.isChecked = collectionData[position].isSelected

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCollectionRVViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_collection, parent, false)
        return AddCollectionRVViewHolder(view)
    }

    fun getSelectedItem(): List<String> {
        val selectedItems = collectionData.filter { it.isSelected }.mapNotNull { it.name }
        Log.d("selectedItemsprevious", selectedItems.toString())
        return selectedItems
    }
}