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

class HomeRVAdapter(
    var dataSet: List<MainListData.Data>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isInSelectionMode = false // 선택모드 유무
    private var selectedItemsCount = 0 // 선택된 아이템 개수

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

    fun getSelectedItems(): ArrayList<String> {
        // 체크된 아이템들의 docId를 반환
        val selectedItems = dataSet.filter { it.isSelected }.mapNotNull { it.docId }
        return ArrayList(selectedItems)
    }

    fun isSelectionMode(): Boolean {
        return isInSelectionMode
    }

    fun isSelectionModeEnd() {
        isInSelectionMode = false
        selectedItemsCount = 0
        notifyDataSetChanged()
    }

    fun toggleMod() {
        isInSelectionMode = !isInSelectionMode
        notifyDataSetChanged()
    }

    //클릭 이벤트 처리
    private fun handleItemClick(position: Int, checkbox: CheckBox) {
        if (isInSelectionMode) {
            toggleSelection(position, checkbox)
            if (selectedItemsCount == 0) {
                isInSelectionMode = false
                itemClickListener.onSelectionEnded()
                notifyDataSetChanged()
            }
        } else {
            itemClickListener.onItemClick(position)
        }
    }

    // 롱클릭 이벤트 처리
    private fun handleItemLongClick(position: Int, checkbox: CheckBox) {
        if (!isInSelectionMode) {
            dataSet.forEach { it.isSelected = false } //모든 체크박스의 체크를 해제

            isInSelectionMode = true
            notifyDataSetChanged()
            itemClickListener.onSelectionStarted()
            toggleSelection(position, checkbox)
        }
    }

    // 선택된 아이템의 isSelected 값을 반전시키고, 체크박스도 반전시킴
    private fun toggleSelection(position: Int, checkbox: CheckBox) {
        dataSet[position].isSelected = !dataSet[position].isSelected
        checkbox.isChecked = dataSet[position].isSelected
        selectedItemsCount =
            if (checkbox.isChecked) selectedItemsCount + 1 else selectedItemsCount - 1
    }

    inner class MemoViewHolder(view: View) : RecyclerView.ViewHolder(view) { // 메모 아이템 뷰홀더
        val title: TextView = view.findViewById<TextView>(R.id.title)
        val subject: TextView = view.findViewById(R.id.subject)
        val date: TextView = view.findViewById(R.id.dateText)
        val checkbox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // 선택한 아이템이 실제로 존재하는지 -> 예외처리
                    handleItemClick(position, checkbox)
                }
            }

            view.setOnLongClickListener { // 아이템을 길게 눌렀을 때
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    handleItemLongClick(position, checkbox)
                }
                true
            }
        }
    }

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.title)
        val subject: TextView = view.findViewById(R.id.subject)
        val date: TextView = view.findViewById(R.id.dateText)
        val checkbox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // 선택한 아이템이 실제로 존재하는지 -> 예외처리
                    handleItemClick(position, checkbox)
                }
            }

            view.setOnLongClickListener { // 아이템을 길게 눌렀을 때
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    handleItemLongClick(position, checkbox)
                }
                true
            }
        }

    }

    inner class WebpageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val date: TextView = view.findViewById(R.id.dateText)
        val checkbox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        val title: TextView = view.findViewById(R.id.title)
        val description: TextView = view.findViewById(R.id.link)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // 선택한 아이템이 실제로 존재하는지 -> 예외처리
                    handleItemClick(position, checkbox)
                }
            }

            view.setOnLongClickListener { // 아이템을 길게 눌렀을 때
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    handleItemLongClick(position, checkbox)
                }
                true
            }
        }
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.title)
        val date: TextView = view.findViewById<TextView>(R.id.dateText)
        val checkbox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // 선택한 아이템이 실제로 존재하는지 -> 예외처리
                    handleItemClick(position, checkbox)
                }
            }

            view.setOnLongClickListener { // 아이템을 길게 눌렀을 때
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    handleItemLongClick(position, checkbox)
                }
                true
            }
        }
    }

    inner class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.date)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onSelectionStarted() // 선택모드 시작
        fun onSelectionEnded() // 선택모드 종료
    }

    override fun getItemViewType(position: Int): Int = when (dataSet[position].type) {
        MEMO -> MEMO_VIEW_TYPE
        FILE -> FILE_VIEW_TYPE
        WEBPAGE -> WEBPAGE_VIEW_TYPE
        IMAGE -> IMAGE_VIEW_TYPE
        DATE -> DATE_VIEW_TYPE
        else -> throw IllegalArgumentException("Invalid type of data $position")
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

            else -> throw IllegalArgumentException("Invalid type of data $viewType")
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MemoViewHolder -> { // 메모
                holder.checkbox.visibility =
                    if (isInSelectionMode) View.VISIBLE else View.GONE //선택모드일때만 보이게
                holder.checkbox.isChecked = dataSet[position].isSelected //선택된 아이템이면 체크박스 체크
                UtilityRV.setMemo(holder.title, holder.date, holder.subject, position, dataSet)

            }

            is FileViewHolder -> {
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected
                // .앞에 있는 파일 이름만 가져오기
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
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected
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
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected
                UtilityRV.setImage(holder.title, holder.date, dataSet, position, holder.itemView)
            }

            is DateViewHolder -> {
                holder.date.text = dataSet[position].header
            }
        }
    }

    fun getItem(position: Int): MainListData.Data {
        return dataSet[position]
    }

}

