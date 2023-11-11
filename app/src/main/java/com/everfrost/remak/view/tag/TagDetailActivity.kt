package com.everfrost.remak.view.tag

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everfrost.remak.UtilityRV
import com.everfrost.remak.adapter.SearchRVAdapter
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.TagDetailActivityBinding
import com.everfrost.remak.view.detail.FileDetailActivity
import com.everfrost.remak.view.detail.ImageDetailActivity
import com.everfrost.remak.view.detail.LinkDetailActivity
import com.everfrost.remak.view.detail.MemoDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TagDetailActivity : AppCompatActivity(), SearchRVAdapter.OnItemClickListener {
    private lateinit var binding: TagDetailActivityBinding
    private val viewModel: TagViewModel by viewModels()
    lateinit var tokenRepository: TokenRepository
    private lateinit var adapter: SearchRVAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TagDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tagName = intent.getStringExtra("tagName")
//        val tagCount = intent.getIntExtra("tagCount", 0)
        adapter = SearchRVAdapter(listOf(), this)
        recyclerView = binding.tagDetailRV
        recyclerView.adapter = adapter
        val itemDecoration = UtilityRV.CardItemOffsetDecoration(10)
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.getTagDetailData(tagName!!)
        viewModel.tagDetailData.observe(this) {
            binding.tagName.text = "${tagName} (${it.size})"
            adapter.dataSet = it
            adapter.notifyDataSetChanged()
        }

        //리사이클러 뷰 무한스크롤 기능
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = recyclerView.layoutManager?.itemCount
                val lastVisibleItemCount =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (totalItemCount!! <= (lastVisibleItemCount + 5)) {
                    viewModel.getNewTagDetailData(tagName)
                }
            }
        })

        binding.backButton.setOnClickListener {
            finish()
        }

    }

    override fun onItemClick(position: Int) {
        when (viewModel.tagDetailData.value!![position].type) {
            "MEMO" -> {
                val intent = Intent(this, MemoDetailActivity::class.java)
                intent.putExtra("docId", viewModel.tagDetailData.value!![position].docId)
                startActivity(intent)
            }

            "FILE" -> {
                val intent = Intent(this, FileDetailActivity::class.java)
                intent.putExtra("docId", viewModel.tagDetailData.value!![position].docId)
                startActivity(intent)
            }

            "WEBPAGE" -> {
                val intent = Intent(this, LinkDetailActivity::class.java)
                intent.putExtra("docId", viewModel.tagDetailData.value!![position].docId)
                startActivity(intent)
            }

            "IMAGE" -> {
                val intent = Intent(this, ImageDetailActivity::class.java)
                intent.putExtra("docId", viewModel.tagDetailData.value!![position].docId)
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetTagDetailData()
    }
}