package com.example.remak.view.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remak.App
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.SearchResultActivityBinding
import com.example.remak.view.detail.FileDetailActivity
import com.example.remak.view.detail.ImageDetailActivity
import com.example.remak.view.detail.LinkDetailActivity
import com.example.remak.view.detail.MemoDetailActivity
import com.example.remak.view.main.HomeRVAdapter

class SearchResultActivity : AppCompatActivity(), SearchRVAdapter.OnItemClickListener {

    private lateinit var binding : SearchResultActivityBinding

    private val viewModel : SearchViewModel by viewModels { SearchViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository : TokenRepository
    private lateinit var adapter : SearchRVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val query = intent.getStringExtra("query") ?: ""

        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = SearchResultActivityBinding.inflate(layoutInflater)

        adapter = SearchRVAdapter(mutableListOf(), this)
        val recyclerView = binding.searchResultRV
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val itemDecoration = ItemOffsetDecoration(10, adapter)
        recyclerView.addItemDecoration(itemDecoration)

        viewModel.searchResult.observe(this) { data ->
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            binding.searchResultRV.visibility = View.VISIBLE
            adapter.dataSet = data
            Log.d("searchresultdata", data.toString())
            adapter.notifyDataSetChanged()
        }

        viewModel.getSearchResult(query)

        setContentView(binding.root)




    }

    override fun onItemClick(view: View, position: Int) {
        when (viewModel.searchResult.value!![position].type) {
            "MEMO" -> {
                val intent = Intent(this, MemoDetailActivity::class.java)
                intent.putExtra("docId", viewModel.searchResult.value!![position].docId)
                startActivity(intent)
            }

            "FILE" -> {
                val intent = Intent(this, FileDetailActivity::class.java)
                intent.putExtra("docId", viewModel.searchResult.value!![position].docId)
                startActivity(intent)
            }

            "WEBPAGE" -> {
                val intent = Intent(this, LinkDetailActivity::class.java)
                intent.putExtra("docId", viewModel.searchResult.value!![position].docId)
                startActivity(intent)

            }

            "IMAGE" -> {
                val intent = Intent(this, ImageDetailActivity::class.java)
                intent.putExtra("docId", viewModel.searchResult.value!![position].docId)
                startActivity(intent)
            }


        }
    }
}