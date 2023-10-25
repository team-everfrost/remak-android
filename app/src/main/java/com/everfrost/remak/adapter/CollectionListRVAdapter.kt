package com.everfrost.remak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.everfrost.remak.R
import com.everfrost.remak.UtilityRV
import com.everfrost.remak.network.model.MainListData

class CollectionListRVAdapter(
    var dataSet: List<MainListData.Data>,
    private val itemClickListener: OnItemClickListener,
    private val checkCallback: (Boolean) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isInSelectionMode = false

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

    fun getSelectedItems(): List<String> { // 선택된 아이템들의 docId를 반환
        return dataSet.filter { it.isSelected }.map { it.docId!! }
    }

    fun isSelectionMode(): Boolean {
        return isInSelectionMode
    }

    fun toggleSelectionMode() {
        dataSet.forEach { it.isSelected = false } //모든 체크박스의 체크를 해제
        isInSelectionMode = !isInSelectionMode
        notifyDataSetChanged()
    }

    private fun handleItemClick(position: Int, checkbox: CheckBox) {
        if (isInSelectionMode) {
            toggleSelection(position, checkbox)
        } else {
            itemClickListener.onItemClick(position)
        }
    }

    // 선택된 아이템의 isSelected 값을 반전시키고, 체크박스도 반전시킴
    private fun toggleSelection(position: Int, checkbox: CheckBox) {
        dataSet[position].isSelected = !dataSet[position].isSelected
        checkbox.isChecked = dataSet[position].isSelected
        checkCallback(dataSet[position].isSelected)
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
                    handleItemClick(position, checkbox)
                }
            }

            view.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemLongClick(position)
                    toggleSelection(position, checkbox)
                }
                true
            }
        }
    }

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.title)
        val subject: TextView = view.findViewById(R.id.subject)
        val date: TextView = view.findViewById(R.id.dateText)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    handleItemClick(position, checkbox)
                }
            }
            view.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemLongClick(position)
                    toggleSelection(position, checkbox)
                }
                true
            }
        }
    }

    inner class WebPageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val link: TextView = view.findViewById(R.id.link)
        val date: TextView = view.findViewById(R.id.dateText)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    handleItemClick(position, checkbox)
                }
            }
            view.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemLongClick(position)
                    toggleSelection(position, checkbox)
                }
                true
            }

        }
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.dateText)
        val title: TextView = view.findViewById(R.id.title)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    handleItemClick(position, checkbox)
                }
            }
            view.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemLongClick(position)
                    toggleSelection(position, checkbox)
                }
                true
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
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
//                if (isInSelectionMode) { //애니메이션 실험 코드
//                    holder.title.animation = AnimationUtils.loadAnimation(
//                        holder.itemView.context,
//                        R.anim.recycler_to_right
//                    )
//                    holder.subject.animation = AnimationUtils.loadAnimation(
//                        holder.itemView.context,
//                        R.anim.recycler_to_right
//                    )
//                    holder.date.animation = AnimationUtils.loadAnimation(
//                        holder.itemView.context,
//                        R.anim.recycler_to_right
//                    )
//                }

                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected
                UtilityRV.setMemo(
                    holder.title,
                    holder.date,
                    holder.subject,
                    position,
                    dataSet
                )
            }

            is FileViewHolder -> {
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected

//                if (isInSelectionMode) { //애니메이션 실험 코드
//                    holder.title.animation = AnimationUtils.loadAnimation(
//                        holder.itemView.context,
//                        R.anim.recycler_to_right
//                    )
//                    holder.subject.animation = AnimationUtils.loadAnimation(
//                        holder.itemView.context,
//                        R.anim.recycler_to_right
//                    )
//                    holder.date.animation = AnimationUtils.loadAnimation(
//                        holder.itemView.context,
//                        R.anim.recycler_to_right
//                    )
//                }

                UtilityRV.setFile(
                    holder.title,
                    holder.date,
                    holder.subject,
                    dataSet,
                    position,
                    holder.itemView
                )
            }

            is WebPageViewHolder -> {
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected
                UtilityRV.setWebpage(
                    holder.title,
                    holder.date,
                    holder.link,
                    dataSet,
                    position,
                    holder.itemView
                )
            }

            is ImageViewHolder -> {
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected
                UtilityRV.setImage(
                    holder.title,
                    holder.date,
                    dataSet,
                    position,
                    holder.itemView
                )
            }
        }
    }

    fun getItem(position: Int): MainListData.Data {
        return dataSet[position]
    }
}

