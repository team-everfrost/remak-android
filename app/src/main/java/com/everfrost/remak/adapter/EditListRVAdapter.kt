package com.everfrost.remak.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.everfrost.remak.R
import com.everfrost.remak.UtilityRV
import com.everfrost.remak.network.model.MainListData

class EditListRVAdapter(
    var dataSet: List<MainListData.Data>,
    private val checkCallback: (Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedItemsCount = 0

    companion object {
        private const val MEMO = "MEMO"
        private const val FILE = "FILE"
        private const val WEBPAGE = "WEBPAGE"
        private const val IMAGE = "IMAGE"
        private const val DATE = "DATE"
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
        selectedItemsCount =
            if (checkbox.isChecked) selectedItemsCount + 1 else selectedItemsCount - 1
    }

    inner class MemoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val subject: TextView = view.findViewById(R.id.subject)
        val date: TextView = view.findViewById(R.id.dateText)
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
        val date: TextView = view.findViewById(R.id.dateText)
        val thumbnail: View = view.findViewById(R.id.thumbnail)
        val checkbox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)

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
        val title: TextView = view.findViewById<TextView>(R.id.title)
        val description: TextView = view.findViewById(R.id.link)
        val date: TextView = view.findViewById(R.id.dateText)
        val checkbox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)

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
        val checkbox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        val thumbnail: ImageFilterView = view.findViewById(R.id.thumbnail)
        val title: TextView = view.findViewById(R.id.title)
        val date: TextView = view.findViewById(R.id.dateText)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position, checkbox)
                }
            }
        }
    }

    inner class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date = view.findViewById<TextView>(R.id.date)
    }

    override fun getItemViewType(position: Int): Int = when (dataSet[position].type) {
        MEMO -> MEMO_VIEW_TYPE
        FILE -> FILE_VIEW_TYPE
        WEBPAGE -> WEBPAGE_VIEW_TYPE
        IMAGE -> IMAGE_VIEW_TYPE
        DATE -> DATE_VIEW_TYPE
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

            DATE_VIEW_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
                DateViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid type of data " + viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MemoViewHolder -> { // 메모
                holder.checkbox.visibility = View.VISIBLE //선택모드일때만 보이게
                holder.checkbox.isChecked = dataSet[position].isSelected
                UtilityRV.setMemo(
                    holder.title,
                    holder.date,
                    holder.subject,
                    position,
                    dataSet,
                )
            }

            is FileViewHolder -> {
                holder.checkbox.isChecked = dataSet[position].isSelected
                holder.checkbox.visibility = View.VISIBLE
                UtilityRV.setFile(
                    holder.title,
                    holder.date,
                    holder.subject,
                    dataSet,
                    position,
                    holder.itemView
                )
            }

            is WebpageViewHolder -> {
                holder.checkbox.isChecked = dataSet[position].isSelected
                holder.checkbox.visibility = View.VISIBLE
                UtilityRV.setWebpage(
                    holder.title,
                    holder.date,
                    holder.description,
                    dataSet,
                    position,
                    holder.itemView
                )
            }

            is ImageViewHolder -> {
                holder.checkbox.visibility = View.VISIBLE
                holder.checkbox.isChecked = dataSet[position].isSelected
                UtilityRV.setImage(
                    holder.title,
                    holder.date,
                    dataSet,
                    position,
                    holder.itemView
                )
            }

            is DateViewHolder -> {
                holder.date.text = dataSet[position].header
            }
        }
    }

    fun getSelectedItemsCount(): Int {
        return selectedItemsCount
    }

    fun getSelectedItems(): ArrayList<String> {
        // 체크된 아이템들의 docId를 반환
        val selectedItems = dataSet.filter { it.isSelected }.mapNotNull { it.docId }
        return ArrayList(selectedItems)
    }

    fun getItem(position: Int): MainListData.Data {
        return dataSet[position]
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}

class EditListItemOffsetDecoration(
    private val mItemOffset: Int,
    private val adapter: EditListRVAdapter
) :
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
            outRect.top = 100
        }
    }
}