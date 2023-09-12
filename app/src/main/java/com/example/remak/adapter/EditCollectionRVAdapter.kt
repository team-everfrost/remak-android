package com.example.remak.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.remak.R
import com.example.remak.network.model.TagDetailData
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class EditCollectionRVAdapter(
    var dataSet: List<TagDetailData.Data>,
    private val checkCallback: (Boolean) -> Unit,
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
        private const val DATE_VIEW_TYPE = 4
    }

    private fun toggleSelection(position: Int, checkbox: CheckBox) {
        dataSet[position].isSelected = !dataSet[position].isSelected
        checkbox.isChecked = dataSet[position].isSelected
        checkCallback(dataSet[position].isSelected)
    }

    inner class MemoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.title)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position, checkbox)
                }
            }
        }
    }

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.title)
        val subject: TextView = view.findViewById(R.id.subject)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position, checkbox)
                }
            }
        }
    }

    inner class WebpageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val link: TextView = view.findViewById(R.id.link)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position, checkbox)
                }
            }
        }
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position, checkbox)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
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
                WebpageViewHolder(view)
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
                holder.title.text = dataSet[position].content
                holder.checkbox.visibility = View.VISIBLE //선택모드일때만 보이게
                holder.checkbox.isChecked = dataSet[position].isSelected
            }

            is FileViewHolder -> {
                holder.checkbox.visibility = View.VISIBLE //선택모드일때만 보이게
                holder.checkbox.isChecked = dataSet[position].isSelected
                //날짜 포맷 변경
                val inputFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

                Log.d("errordate", dataSet[position].updatedAt!!)

                val dateTime = ZonedDateTime.parse(dataSet[position].updatedAt, inputFormatter)
                val outputDateStr = dateTime.format(outputFormatter)

                val text =
                    holder.itemView.context.getString(R.string.filetype_date, "PDF", outputDateStr)

                // .앞에 있는 파일 이름만 가져오기
                val title = dataSet[position].title!!.substringBefore(".")

                holder.title.text = title//제목
                holder.subject.text = text //파일타입, 날짜
            }

            is WebpageViewHolder -> {
                val title = dataSet[position].title!!.replace(" ", "")
                holder.checkbox.visibility = View.VISIBLE //선택모드일때만 보이게
                holder.checkbox.isChecked = dataSet[position].isSelected
                if (title.isNullOrEmpty()) {
                    holder.title.text = dataSet[position].url
                } else {
                    Log.d("title", dataSet[position].title!!.toString())
                    holder.title.text = dataSet[position].title

                }
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

            is ImageViewHolder -> {
                holder.checkbox.visibility = View.VISIBLE //선택모드일때만 보이게
                holder.checkbox.isChecked = dataSet[position].isSelected
                Glide.with(holder.itemView.context)
                    .load(dataSet[position].url)
                    .into(holder.itemView.findViewById(R.id.imageView))
            }
        }
    }

    fun getSelectedItems(): ArrayList<String> {
        // 체크된 아이템들의 docId를 반환
        val selectedItems = dataSet.filter { it.isSelected }.mapNotNull { it.docId }
        return ArrayList(selectedItems)
    }

}
