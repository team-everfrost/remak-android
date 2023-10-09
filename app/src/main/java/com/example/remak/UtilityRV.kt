package com.example.remak

import android.graphics.Rect
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.remak.adapter.HomeRVAdapter
import com.example.remak.network.model.MainListData

object UtilityRV {
    class HomeItemOffsetDecoration( //날짜가 있는 홈의 아이템 간격 조정
        private val mItemOffset: Int,
        private val adapter: HomeRVAdapter
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

    class CardItemOffsetDecoration(private val mItemOffset: Int) : //날짜가 없는 카드 아이템 간격 조정
        RecyclerView.ItemDecoration() {
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

    fun setWebpage(
        title: TextView,
        date: TextView,
        description: TextView,
        dataSet: List<MainListData.Data>,
        position: Int,
        itemView: View
    ) {
        val newTitle = dataSet[position].title!!.replace(" ", "")
        val summary = dataSet[position].summary
        date.text = "링크 | ${extractDomain(position, dataSet)}"
        when (dataSet[position].status!!) {
            "SCRAPE_PENDING" -> {
                title.text = dataSet[position].url
                description.text = "스크랩 대기중이에요."
            }

            "SCRAPE_PROCESSING" -> {
                title.text = dataSet[position].url
                description.text = "스크랩이 진행중이에요!"
            }

            "SCRAPE_REJECTED" -> {
                title.text = dataSet[position].url
                description.text = "스크랩에 실패했어요."
            }

            "EMBED_PENDING" -> {
                title.text = newTitle
                description.text = "AI가 곧 자료를 요약할거에요."
            }

            "EMBED_PROCESSING" -> {
                title.text = newTitle
                description.text = "AI가 자료를 요약중이에요!"
            }

            "EMBED_REJECTED" -> {
                title.text = dataSet[position].title
                description.text = "AI가 자료를 요약하지 못했어요."
            }

            "COMPLETED" -> {
                title.text = newTitle
                if (summary != null) {
                    if (summary.contains("\n")) {
                        val index = summary.indexOf("\n") // 첫줄만 보여주기
                        description.text = summary.substring(0, index) // 첫줄만 보여주기
                    } else {
                        description.text = dataSet[position].summary
                    }
                } else {
                    description.text = ""
                }
            }
        }
        if (newTitle.isEmpty()) {
            title.text = dataSet[position].url
        } else {
            title.text = dataSet[position].title
        }

        if (!dataSet[position].thumbnailUrl.isNullOrEmpty()) {
            Glide.with(itemView.context)
                .load(dataSet[position].thumbnailUrl)
                .transform(CenterCrop(), RoundedCorners(47))
                .into(itemView.findViewById(R.id.thumbnail))
            itemView.findViewById<ImageFilterView>(R.id.thumbnail).background =
                AppCompatResources.getDrawable(
                    itemView.context,
                    R.drawable.item_link_radius_whitegray
                )
//                itemView.context.getDrawable(R.drawable.item_link_radius_whitegray)

        } else {
            Glide.with(itemView.context)
                .load(R.drawable.no_thumbnail_image)
                .transform(CenterCrop(), RoundedCorners(47))
                .into(itemView.findViewById(R.id.thumbnail))
            itemView.findViewById<ImageFilterView>(R.id.thumbnail).background = null
        }
    }

    fun setFile(
        title: TextView,
        date: TextView,
        subject: TextView,
        dataSet: List<MainListData.Data>,
        position: Int,
        itemView: View
    ) {
        val newTitle = dataSet[position].title!!
        val summary = dataSet[position].summary
        title.text = newTitle//제목
        date.text = "파일 | ${dateSetting(position, dataSet)}"//날짜
        when (dataSet[position].status!!) {
            "EMBED_PENDING" -> {
                subject.text = "AI가 곧 자료를 요약할거에요."
            }

            "EMBED_PROCESSING" -> {
                subject.text = "AI가 자료를 요약중이에요!"
            }

            "EMBED_REJECTED" -> {
                subject.text = "AI가 자료를 요약하지 못했어요."
            }

            "COMPLETED" -> {
                if (summary != null) {
                    if (summary.contains("\n")) {
                        val index = summary.indexOf("\n")
                        subject.text = summary.substring(0, index)
                    } else {
                        subject.text = dataSet[position].summary
                    }
                } else {
                    subject.text = ""
                }
            }
        }
        if (!dataSet[position].thumbnailUrl.isNullOrEmpty()) {
            Glide.with(itemView.context)
                .load(dataSet[position].thumbnailUrl)
                .transform(CenterCrop(), RoundedCorners(47))
                .into(itemView.findViewById(R.id.thumbnail))
            itemView.findViewById<ImageFilterView>(R.id.thumbnail).background =
                AppCompatResources.getDrawable(
                    itemView.context,
                    R.drawable.item_link_radius_whitegray
                )
        } else {
            Glide.with(itemView.context)
                .load(R.drawable.no_thumbnail_image)
                .transform(CenterCrop(), RoundedCorners(47))
                .into(itemView.findViewById(R.id.thumbnail))
            itemView.findViewById<ImageFilterView>(R.id.thumbnail).background = null
        }
    }

    fun setImage(
        title: TextView,
        date: TextView,
        dataSet: List<MainListData.Data>,
        position: Int,
        itemView: View
    ) {
        title.text = dataSet[position].title
        date.text = "이미지 | ${dateSetting(position, dataSet)}"
        if (!dataSet[position].thumbnailUrl.isNullOrEmpty()) {
            Glide.with(itemView.context)
                .load(dataSet[position].thumbnailUrl)
                .transform(CenterCrop(), RoundedCorners(47))
                .into(itemView.findViewById(R.id.thumbnail))
        } else {
            Glide.with(itemView.context)
                .load(R.drawable.no_thumbnail_image)
                .transform(CenterCrop(), RoundedCorners(47))
                .into(itemView.findViewById(R.id.thumbnail))
            itemView.findViewById<ImageFilterView>(R.id.thumbnail).background = null
        }
    }

    fun setMemo(
        title: TextView,
        date: TextView,
        subject: TextView,
        position: Int,
        dataSet: List<MainListData.Data>
    ) {
        val lines = dataSet[position].content!!.split("\n")
        val firstPart = lines.firstOrNull() ?: ""
        val secondPart = if (lines.size > 1) {
            lines.subList(1, lines.size).joinToString("\n")
        } else {
            ""
        }
        title.text = firstPart
        subject.text = secondPart
        date.text = "메모 | ${dateSetting(position, dataSet)}"
    }

    private fun extractDomain(position: Int, dataSet: List<MainListData.Data>): String? {
        val url = dataSet[position].url
        val regex = """https?://([\w\-\.]+)""".toRegex()
        val result = regex.find(url!!)
        return result?.groups?.get(1)?.value
    }

    private fun dateSetting(position: Int, dataSet: List<MainListData.Data>): String {
        val rawDate = dataSet[position].updatedAt
        return rawDate?.split("T")?.get(0) ?: ""
    }

}