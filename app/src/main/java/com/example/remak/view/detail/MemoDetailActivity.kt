package com.example.remak.view.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.databinding.DetailPageMemoActivityBinding

class MemoDetailActivity : AppCompatActivity() {
    private lateinit var binding : DetailPageMemoActivityBinding

    private val viewModel : DetailViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DetailPageMemoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val memoId = intent.getStringExtra("docId")

        Log.d("memoId", memoId.toString())
        viewModel.getDetailData(memoId!!)

        viewModel.detailData.observe(this, {
            binding.memoContent.setText(it.content)
        })






    }
}