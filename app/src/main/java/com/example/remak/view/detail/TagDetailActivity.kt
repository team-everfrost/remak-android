package com.example.remak.view.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.adapter.TagDetailRVAdapter
import com.example.remak.adapter.TagItemOffsetDecoration
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.TagDetailActivityBinding

class TagDetailActivity : AppCompatActivity(), TagDetailRVAdapter.OnItemClickListener {
    private lateinit var binding: TagDetailActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    private lateinit var adapter : TagDetailRVAdapter
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = TagDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tagName = intent.getStringExtra("tagName")
        adapter = TagDetailRVAdapter(listOf(), this)
        recyclerView = binding.tagDetailRV
        recyclerView.adapter = adapter
        val itemDecoration = TagItemOffsetDecoration(10)
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.getTagDetailData(tagName!!)
        binding.tagName.text = "#$tagName"

        viewModel.tagDetailData.observe(this) {
            Log.d("tagDetailData", it.toString())
            adapter.dataSet = it
            adapter.notifyDataSetChanged()
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