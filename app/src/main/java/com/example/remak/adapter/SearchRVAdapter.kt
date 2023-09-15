package com.example.remak.adapter

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.remak.R
import com.example.remak.network.model.MainListData
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SearchRVAdapter(
    var dataSet: List<MainListData.Data>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val MEMO = "MEMO"
        private const val FILE = "FILE"
        private const val WEBPAGE = "WEBPAGE"
        private const val IMAGE = "IMAGE"
        private const val MEMO_VIEW_TYPE = 1
        private const val FILE_VIEW_TYPE = 0
        private const val WEBPAGE_VIEW_TYPE = 2
        private const val IMAGE_VIEW_TYPE = 3
    }

    fun resetData() {
        dataSet = listOf()
        notifyDataSetChanged()
    }

    inner class MemoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val subject: TextView = view.findViewById(R.id.subject)
        val date: TextView = view.findViewById(R.id.dateText)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(it, position)
                }
            }
        }
    }

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.title)
        val subject: TextView = view.findViewById(R.id.subject)
        val date: TextView = view.findViewById(R.id.dateText)
        val thumbnail: View = view.findViewById(R.id.thumbnail)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(it, position)
                }
            }
        }
    }

    inner class WebPageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val link: TextView = view.findViewById(R.id.link)
        val date: TextView = view.findViewById(R.id.dateText)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(it, position)
                }
            }

        }
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: View = view.findViewById(R.id.thumbnail)
        val date: TextView = view.findViewById(R.id.dateText)
        val title: TextView = view.findViewById(R.id.title)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(it, position)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)

    }

    override fun getItemViewType(position: Int): Int = when (dataSet[position].type) {
        MEMO -> MEMO_VIEW_TYPE
        FILE -> FILE_VIEW_TYPE
        WEBPAGE -> WEBPAGE_VIEW_TYPE
        IMAGE -> IMAGE_VIEW_TYPE
        else -> throw IllegalArgumentException("Invalid type of data " + position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MEMO_VIEW_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
                MemoViewHolder(view)
            }

            FILE_VIEW_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
                FileViewHolder(view)
            }

            WEBPAGE_VIEW_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_link, parent, false)
                WebPageViewHolder(view)
            }

            IMAGE_VIEW_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
                ImageViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid type of data " + viewType)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MemoViewHolder -> { // 메모
                val lines = dataSet[position].content!!.split("\n")
                val firstPart = lines.firstOrNull() ?: ""
                val secondPart = if (lines.size > 1) {
                    lines.subList(1, lines.size).joinToString("\n")
                } else {
                    ""
                }
                holder.date.text = "메모 | ${dateSetting(position)}"
                holder.title.text = firstPart
                holder.subject.text = secondPart
            }

            is FileViewHolder -> {
                val title = dataSet[position].title
                val summary = dataSet[position].summary
                holder.title.text = title//제목
                holder.date.text = "파일 | ${dateSetting(position)}"//날짜

                when (dataSet[position].status) {
                    "EMBED_PENDING" -> {
                        holder.subject.text = "AI가 곧 자료를 요약할거에요."
                    }

                    "EMBED_PROCESSING" -> {
                        holder.subject.text = "AI가 자료를 요약중이에요!"
                    }

                    "EMBED_REJECTED" -> {
                        holder.subject.text = "AI가 자료를 요약하지 못했어요."
                    }

                    "COMPLETED" -> {
                        if (summary != null) {
                            if (summary.contains("\n")) {
                                val index = summary.indexOf("\n")
                                holder.subject.text = summary.substring(0, index)
                            } else {
                                holder.subject.text = dataSet[position].summary
                            }
                        } else {
                            holder.subject.text = ""
                        }
                    }
                }
                if (!dataSet[position].thumbnailUrl.isNullOrEmpty()) {
                    Glide.with(holder.itemView.context)
                        .load(dataSet[position].thumbnailUrl)
                        .transform(CenterCrop(), RoundedCorners(47))
                        .into(holder.itemView.findViewById(R.id.thumbnail))
                } else {
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.sample_image)
                        .transform(CenterCrop(), RoundedCorners(47))
                        .into(holder.itemView.findViewById(R.id.thumbnail))
                }
            }

            is WebPageViewHolder -> {
                val title = dataSet[position].title!!.replace(" ", "")
                if (title.isNullOrEmpty()) {
                    holder.title.text = dataSet[position].url
                } else {
                    Log.d("title", dataSet[position].title.toString())
                    holder.title.text = dataSet[position].title

                }
                holder.date.text = "링크 | ${extractDomain(position)}"
                if (dataSet[position].summary.isNullOrEmpty()) {
                    holder.link.text = "Ai가 문서를 요약중이에요!"
                } else {
                    val summary = dataSet[position].summary
                    //summary의 엔터 다음 문자는 제거
                    if (summary!!.contains("\n")) {
                        val index = summary.indexOf("\n")
                        holder.link.text = summary.substring(0, index)
                    } else {
                        holder.link.text = dataSet[position].summary
                    }
                }
                if (!dataSet[position].thumbnailUrl.isNullOrEmpty()) {
                    Glide.with(holder.itemView.context)
                        .load(dataSet[position].thumbnailUrl)
                        .transform(CenterCrop(), RoundedCorners(10))
                        .into(holder.itemView.findViewById(R.id.thumbnail))
                } else {
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.sample_image)
                        .transform(CenterCrop(), RoundedCorners(10))
                        .into(holder.itemView.findViewById(R.id.thumbnail))
                }
            }

            is ImageViewHolder -> {
                holder.title.text = dataSet[position].title
                holder.date.text = "이미지 | ${dateSetting(position)}"
                if (!dataSet[position].thumbnailUrl.isNullOrEmpty()) {
                    Glide.with(holder.itemView.context)
                        .load(dataSet[position].thumbnailUrl)
                        .transform(CenterCrop(), RoundedCorners(47))
                        .into(holder.itemView.findViewById(R.id.thumbnail))
                } else {
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.no_thumbnail_image)
                        .transform(CenterCrop(), RoundedCorners(47))
                        .into(holder.itemView.findViewById(R.id.thumbnail))
                    holder.itemView.findViewById<ImageFilterView>(R.id.thumbnail).background = null
                }
            }
        }
    }

    private fun extractDomain(position: Int): String? {
        val url = dataSet[position].url
        val regex = """https?://([\w\-\.]+)""".toRegex()
        val result = regex.find(url!!)
        return result?.groups?.get(1)?.value
    }

    private fun dateSetting(position: Int): String {
        val inputFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val dateTime = ZonedDateTime.parse(dataSet[position].updatedAt, inputFormatter)
        return dateTime.format(outputFormatter)
    }

    fun getItem(position: Int): MainListData.Data {
        return dataSet[position]
    }
}

class ItemOffsetDecoration(private val mItemOffset: Int, private val adapter: SearchRVAdapter) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = mItemOffset

        val position = parent.getChildAdapterPosition(view)
        val item = adapter.getItem(position)

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mItemOffset
        } else if (item.type == "DATE") {
            outRect.top = mItemOffset
        }

    }
}