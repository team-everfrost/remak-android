package com.example.remak.view.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.adapter.TagDetailItemOffsetDecoration
import com.example.remak.adapter.TagDetailRVAdapter
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageCollectionActivityBinding

class CollectionDetailActivity : AppCompatActivity(), TagDetailRVAdapter.OnItemClickListener {
    private lateinit var binding: DetailPageCollectionActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    private lateinit var adapter: TagDetailRVAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageCollectionActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val collectionName = intent.getStringExtra("collectionName")
        val collectionCount = intent.getIntExtra("collectionCount", 0)
        adapter = TagDetailRVAdapter(listOf(), this)
        recyclerView = binding.collectionDetailRecyclerView
        recyclerView.adapter = adapter
        val itemDecorator = TagDetailItemOffsetDecoration(10)
        recyclerView.addItemDecoration(itemDecorator)
        recyclerView.layoutManager = LinearLayoutManager(this)

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

}