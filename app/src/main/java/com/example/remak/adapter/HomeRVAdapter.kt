package com.example.remak.adapter

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.remak.R
import com.example.remak.network.model.MainListData
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class HomeRVAdapter(var dataSet : List<MainListData.Data>, private val itemClickListener : OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isInSelectionMode = false // 선택모드 유무
    var selectedItemsCount = 0 // 선택된 아이템 개수
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

    fun getSelectedItems(): List<String> { // 선택된 아이템들의 docId를 반환
        return dataSet.filter { it.isSelected }.map { it.docId!! }
    }

    fun isSelectionMode() : Boolean {
        return isInSelectionMode
    }

    fun isSelectionModeEnd() {
        isInSelectionMode = false
        selectedItemsCount = 0
        notifyDataSetChanged()
    }

    //클릭 이벤트 처리
    private fun handleItemClick(clickedView : View, position: Int, checkbox: CheckBox) {
        if (isInSelectionMode) {
            toggleSelection(position, checkbox)
            if (selectedItemsCount == 0) {
                isInSelectionMode = false
                itemClickListener.onSelectionEnded()
                notifyDataSetChanged()
            }
        } else {
            itemClickListener.onItemClick(clickedView, position)
        }
    }

    // 롱클릭 이벤트 처리
    private fun handleItemLongClick(position: Int, checkbox: CheckBox) {
        if (!isInSelectionMode) {
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
        selectedItemsCount = if (checkbox.isChecked) selectedItemsCount + 1 else selectedItemsCount - 1
    }

    fun toggleCheckbox(position: Int) {
        val checkBox = dataSet[position].isSelected
        dataSet[position].isSelected = !dataSet[position].isSelected
        notifyDataSetChanged()
    }

    inner class MemoViewHolder(view : View) : RecyclerView.ViewHolder(view) { // 메모 아이템 뷰홀더
        val title : TextView = view.findViewById<TextView>(R.id.title)
        val checkbox : CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // 선택한 아이템이 실제로 존재하는지 -> 예외처리
                    handleItemClick(it, position, checkbox)
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

    inner class FileViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById<TextView>(R.id.title)
        val subject : TextView = view.findViewById(R.id.subject)
        val checkbox : CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // 선택한 아이템이 실제로 존재하는지 -> 예외처리
                    handleItemClick(it, position, checkbox)
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

    inner class WebpageViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val checkbox : CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        val title : TextView = view.findViewById(R.id.title)
        val link : TextView = view.findViewById(R.id.link)
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // 선택한 아이템이 실제로 존재하는지 -> 예외처리
                    handleItemClick(it, position, checkbox)
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

    inner class ImageViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val checkbox : CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // 선택한 아이템이 실제로 존재하는지 -> 예외처리
                    handleItemClick(it, position, checkbox)
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

    inner class DateViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val date : TextView = view.findViewById(R.id.date)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onSelectionStarted() // 선택모드 시작
        fun onSelectionEnded() // 선택모드 종료
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
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
                MemoViewHolder(view)
            }
            FILE_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
                FileViewHolder(view)
            }
            WEBPAGE_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_link, parent, false)
                WebpageViewHolder(view)
            }
            IMAGE_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
                ImageViewHolder(view)
            }
            DATE_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
                DateViewHolder(view)
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
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE //선택모드일때만 보이게
                holder.checkbox.isChecked = dataSet[position].isSelected //선택된 아이템이면 체크박스 체크
            }
            is FileViewHolder -> {
                //날짜 포맷 변경
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

                Log.d("errordate", dataSet[position].updatedAt!!)

                val dateTime = ZonedDateTime.parse(dataSet[position].updatedAt, inputFormatter)
                val outputDateStr = dateTime.format(outputFormatter)

                val text = holder.itemView.context.getString(R.string.filetype_date, "PDF", outputDateStr)

                // .앞에 있는 파일 이름만 가져오기
                val title = dataSet[position].title!!.substringBefore(".")

                holder.title.text = title//제목
                holder.subject.text = text //파일타입, 날짜
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected
            }

            is WebpageViewHolder -> {
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected
                val title = dataSet[position].title!!.replace(" ", "")
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
                        .into(holder.itemView.findViewById(R.id.likeBtn))
                }

            }

            is ImageViewHolder -> {
                Glide.with(holder.itemView.context)
                    .load(dataSet[position].url)
                    .into(holder.itemView.findViewById(R.id.imageView))
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected
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

class HomeItemOffsetDecoration(private val mItemOffset: Int, private val adapter: HomeRVAdapter) : RecyclerView.ItemDecoration() {
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