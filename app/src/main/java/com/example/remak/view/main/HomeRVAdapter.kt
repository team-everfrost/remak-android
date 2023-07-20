package com.example.remak.view.main

import android.content.Context
import android.graphics.Rect
import android.icu.text.Transliterator.Position
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.remak.R
import com.example.remak.network.model.MainListData
import org.w3c.dom.Text
import java.text.SimpleDateFormat
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
        private const val MEMO_VIEW_TYPE = 1
        private const val FILE_VIEW_TYPE = 0
        private const val WEBPAGE_VIEW_TYPE = 2
        private const val IMAGE_VIEW_TYPE = 3
    }

    fun getSelectedItems(): List<String> { // 선택된 아이템들의 docId를 반환
        return dataSet.filter { it.isSelected }.map { it.docId }
    }
    inner class MemoViewHolder(view : View) : RecyclerView.ViewHolder(view) { // 메모 아이템 뷰홀더

        val title : TextView = view.findViewById<TextView>(R.id.title)
        val checkbox : CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // 아이템이 선택되었을 때
                    if (isInSelectionMode) { // 선택모드일 때
                        dataSet[position].isSelected = !dataSet[position].isSelected
                        checkbox.isChecked = dataSet[position].isSelected
                        selectedItemsCount = if (checkbox.isChecked) selectedItemsCount + 1 else selectedItemsCount - 1 //체크박스가 체크되어있으면 선택된 아이템 개수 +1, 체크되어있지 않으면 -1
                        if (selectedItemsCount == 0) { // 선택된 아이템이 없을 때
                            isInSelectionMode = false // 선택모드 해제
                            itemClickListener.onSelectionEnded() // 선택모드 해제 리스너 호출
                            notifyDataSetChanged()
                        }
                    } else {
                        itemClickListener.onItemClick(it, position)
                    }
                }
            }

            view.setOnLongClickListener { // 아이템을 길게 눌렀을 때
                if (!isInSelectionMode) { // 선택 모드가 아닐 때만 이벤트 처리
                    val position = adapterPosition // 아이템의 위치를 가져옴
                    if (position != RecyclerView.NO_POSITION) {
                        isInSelectionMode = true // 선택모드 활성화
                        notifyDataSetChanged()
                        itemClickListener.onSelectionStarted() // 선택모드 시작 리스너 호출
                        dataSet[position].isSelected = !dataSet[position].isSelected // 선택된 아이템의 isSelected를 true로 변경
                        checkbox.isChecked = dataSet[position].isSelected // 체크박스 체크
                        if (checkbox.isChecked) { // 체크박스가 체크되었을 때
                            selectedItemsCount++  // 선택된 아이템 개수 증가
                        }
                    }
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
                if (position != RecyclerView.NO_POSITION) {
                    if (isInSelectionMode) {
                        dataSet[position].isSelected = !dataSet[position].isSelected
                        checkbox.isChecked = dataSet[position].isSelected
                        selectedItemsCount = if (checkbox.isChecked) selectedItemsCount + 1 else selectedItemsCount - 1
                        if (selectedItemsCount == 0) {
                            isInSelectionMode = false
                            itemClickListener.onSelectionEnded()
                            notifyDataSetChanged() // This will refresh the RecyclerView and hide checkboxes
                        }
                    } else {
                        itemClickListener.onItemClick(it, position)
                    }
                }
            }

            view.setOnLongClickListener { // 아이템을 길게 눌렀을 때
                if (!isInSelectionMode) { // 선택 모드가 아닐 때만 이벤트 처리
                    val position = adapterPosition // 아이템의 위치를 가져옴
                    if (position != RecyclerView.NO_POSITION) {
                        isInSelectionMode = true // 선택모드 활성화
                        notifyDataSetChanged()
                        itemClickListener.onSelectionStarted() // 선택모드 시작 리스너 호출
                        dataSet[position].isSelected = !dataSet[position].isSelected // 선택된 아이템의 isSelected를 true로 변경
                        checkbox.isChecked = dataSet[position].isSelected // 체크박스 체크
                        if (checkbox.isChecked) { // 체크박스가 체크되었을 때
                            selectedItemsCount++  // 선택된 아이템 개수 증가
                        }
                    }
                }
                true
            }
        }

    }

    inner class WebpageViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val checkbox : CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (isInSelectionMode) {
                        dataSet[position].isSelected = !dataSet[position].isSelected
                        checkbox.isChecked = dataSet[position].isSelected
                        selectedItemsCount = if (checkbox.isChecked) selectedItemsCount + 1 else selectedItemsCount - 1
                        if (selectedItemsCount == 0) {
                            isInSelectionMode = false
                            itemClickListener.onSelectionEnded()
                            notifyDataSetChanged() // This will refresh the RecyclerView and hide checkboxes
                        }
                    } else {
                        itemClickListener.onItemClick(it, position)
                    }
                }
            }

            view.setOnLongClickListener { // 아이템을 길게 눌렀을 때
                if (!isInSelectionMode) { // 선택 모드가 아닐 때만 이벤트 처리
                    val position = adapterPosition // 아이템의 위치를 가져옴
                    if (position != RecyclerView.NO_POSITION) {
                        isInSelectionMode = true // 선택모드 활성화
                        notifyDataSetChanged()
                        itemClickListener.onSelectionStarted() // 선택모드 시작 리스너 호출
                        dataSet[position].isSelected = !dataSet[position].isSelected // 선택된 아이템의 isSelected를 true로 변경
                        checkbox.isChecked = dataSet[position].isSelected // 체크박스 체크
                        if (checkbox.isChecked) { // 체크박스가 체크되었을 때
                            selectedItemsCount++  // 선택된 아이템 개수 증가
                        }
                    }
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
                if (position != RecyclerView.NO_POSITION) {
                    if (isInSelectionMode) {
                        dataSet[position].isSelected = !dataSet[position].isSelected
                        checkbox.isChecked = dataSet[position].isSelected
                        selectedItemsCount = if (checkbox.isChecked) selectedItemsCount + 1 else selectedItemsCount - 1
                        if (selectedItemsCount == 0) {
                            isInSelectionMode = false
                            itemClickListener.onSelectionEnded()
                            notifyDataSetChanged() // This will refresh the RecyclerView and hide checkboxes
                        }
                    } else {
                        itemClickListener.onItemClick(it, position)
                    }
                }
            }

            view.setOnLongClickListener { // 아이템을 길게 눌렀을 때
                if (!isInSelectionMode) { // 선택 모드가 아닐 때만 이벤트 처리
                    val position = adapterPosition // 아이템의 위치를 가져옴
                    if (position != RecyclerView.NO_POSITION) {
                        isInSelectionMode = true // 선택모드 활성화
                        notifyDataSetChanged()
                        itemClickListener.onSelectionStarted() // 선택모드 시작 리스너 호출
                        dataSet[position].isSelected = !dataSet[position].isSelected // 선택된 아이템의 isSelected를 true로 변경
                        checkbox.isChecked = dataSet[position].isSelected // 체크박스 체크
                        if (checkbox.isChecked) { // 체크박스가 체크되었을 때
                            selectedItemsCount++  // 선택된 아이템 개수 증가
                        }
                    }
                }
                true
            }
        }

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
            else -> throw IllegalArgumentException("Invalid type of data " + viewType)
        }



    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]

        when (holder) {
            is MemoViewHolder -> { // 메모
                holder.title.text = dataSet[position].content
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE //선택모드일때만 보이게
                holder.checkbox.isChecked = dataSet[position].isSelected //선택된 아이템이면 체크박스 체크
            }
            is FileViewHolder -> {
                //날짜 포맷 변경
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" , Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dataSet[position].updatedAt)
                val outputDateStr = outputFormat.format(date)
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



            }

            is ImageViewHolder -> {
                Glide.with(holder.itemView.context)
                    .load(dataSet[position].url)
                    .into(holder.itemView.findViewById(R.id.imageView))
                holder.checkbox.visibility = if (isInSelectionMode) View.VISIBLE else View.GONE
                holder.checkbox.isChecked = dataSet[position].isSelected



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