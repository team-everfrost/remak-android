package com.example.remak.view.main

import android.content.Context
import android.graphics.Rect
import android.icu.text.Transliterator.Position
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.R
import com.example.remak.network.model.MainListData
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HomeRVAdapter(var dataSet : List<MainListData.Data>, private val itemClickListener : OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        private const val MEMO = "MEMO"
        private const val MEMO_VIEW_TYPE = 1
        private const val FILE_VIEW_TYPE = 0
    }
    inner class MemoViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val title : TextView = view.findViewById<TextView>(R.id.title)
        init {
            view.setOnClickListener {
            itemClickListener.onItemClick(it, adapterPosition)
            }
        }

    }

    inner class FileViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById<TextView>(R.id.title)
        val subject : TextView = view.findViewById(R.id.subject)
        init {
            view.setOnClickListener {
                itemClickListener.onItemClick(it, adapterPosition)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }


    override fun getItemViewType(position: Int): Int = when (dataSet[position].type) {
        MEMO -> MEMO_VIEW_TYPE
        else -> FILE_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MEMO_VIEW_TYPE -> {
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
                //날짜 포맷 변경
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" , Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dataSet[position].updatedAt)
                val outputDateStr = outputFormat.format(date)
                val text = holder.itemView.context.getString(R.string.filetype_date, "PDF", outputDateStr)

                holder.title.text = dataSet[position].title //제목
                holder.subject.text = text //파일타입, 날짜


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