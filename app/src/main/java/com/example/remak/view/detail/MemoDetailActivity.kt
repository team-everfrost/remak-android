package com.example.remak.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageMemoActivityBinding

class MemoDetailActivity : AppCompatActivity() {
    private lateinit var binding : DetailPageMemoActivityBinding
    private val viewModel : DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository)}
    lateinit var tokenRepository: TokenRepository
    var isEditMode = false

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isEditMode) {
                UtilityDialog.showWarnDialog(
                    this@MemoDetailActivity,
            "수정을 취소하시겠습니까?",
                        confirmClick = {
                            endEditMode()
                            binding.memoContent.clearFocus()
                        },
                        cancelClick = {}
                    )
            } else {
                finish()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageMemoActivityBinding.inflate(layoutInflater)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(this)
        }
        setContentView(binding.root)
        val memoId = intent.getStringExtra("docId")

        this.onBackPressedDispatcher.addCallback(this, callback)
        viewModel.getDetailData(memoId!!)
        viewModel.detailData.observe(this) {
            binding.memoContent.setText(it.content)
        }

         binding.memoContent.setOnFocusChangeListener { _, hasFocus ->
             if (hasFocus) {
                 startEditMode()
             }
         }

        binding.completeBtn.setOnClickListener {
            viewModel.updateMemo(memoId, binding.memoContent.text.toString())
            endEditMode()
            binding.memoContent.clearFocus()
            //키보드 내려오기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

        binding.backBtn.setOnClickListener {
            if (isEditMode) {
                endEditMode()
                binding.memoContent.clearFocus()
                //키보드 내리기
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
            } else {
                finish()
            }
        }

        binding.moreIcon.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.detail_more_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {menuItem ->
                when(menuItem.itemId) {
                    R.id.deleteBtn -> {
                        com.example.remak.UtilityDialog.showWarnDialog(
                            this,
                            "삭제하시겠습니까?",
                            confirmClick = {
                                viewModel.deleteDocument(memoId)
                                val resultIntent = Intent()
                                resultIntent.putExtra("isDelete", true)
                                setResult(RESULT_OK, resultIntent)
                                finish()
                            },
                            cancelClick = {}
                        )
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        binding.frameLayout.setOnClickListener {
            binding.memoContent.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.memoContent, InputMethodManager.SHOW_IMPLICIT)
            binding.memoContent.setSelection(binding.memoContent.text.length)
        }

        binding.shareIcon.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, binding.memoContent.text.toString())
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun startEditMode() {
        isEditMode = true
        binding.completeBtn.visibility = View.VISIBLE
        binding.moreIcon.visibility = View.GONE
        binding.shareIcon.visibility = View.GONE
    }

    private fun endEditMode() {
        isEditMode = false
        binding.completeBtn.visibility = View.GONE
        binding.moreIcon.visibility = View.VISIBLE
        binding.shareIcon.visibility = View.VISIBLE
    }

}