package com.example.remak.view.detail

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.App
import com.example.remak.R
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
                showWarnDialog(
            "수정을 취소하시겠습니까?",
                        confirmClick = {
                            isEditMode = false
                            binding.completeBtn.visibility = View.GONE
                            binding.moreIcon.visibility = View.VISIBLE
                            binding.shareIcon.visibility = View.VISIBLE
                            binding.memoContent.clearFocus()
                        },
                        cancelClick = {
                            //do nothing
                        }
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
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
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
                    isEditMode = true
                    binding.completeBtn.visibility = View.VISIBLE
                    binding.moreIcon.visibility = View.GONE
                    binding.shareIcon.visibility = View.GONE
             }
         }

        binding.completeBtn.setOnClickListener {
            viewModel.updateMemo(memoId, binding.memoContent.text.toString())
            isEditMode = false
            binding.completeBtn.visibility = View.GONE
            binding.moreIcon.visibility = View.VISIBLE
            binding.shareIcon.visibility = View.VISIBLE
            binding.memoContent.clearFocus()
        }

        binding.backBtn.setOnClickListener {
            if (isEditMode) {
                isEditMode = false
                binding.completeBtn.visibility = View.GONE
                binding.moreIcon.visibility = View.VISIBLE
                binding.shareIcon.visibility = View.VISIBLE
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
                        showWarnDialog(
                            "삭제하시겠습니까?",
                            confirmClick = {
                                viewModel.deleteDocument(memoId)
                                finish()
                            },

                            cancelClick = {
                                //do nothing
                            }
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
        }
    }



    private fun showWarnDialog(getContent : String, confirmClick: () -> Unit, cancelClick: () -> Unit) {
        val dialog = Dialog(this)
        val windowManager =
            this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_warning)

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (size.x * 0.85).toInt()
            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)
        } else {
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (rect.width() * 0.85).toInt()
            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        val confirmBtn = dialog.findViewById<View>(R.id.confirmBtn)
        val cancelBtn = dialog.findViewById<View>(R.id.cancelBtn)
        val content = dialog.findViewById<TextView>(R.id.msgTextView)
        content.text = getContent

        confirmBtn.setOnClickListener {
            dialog.dismiss()
            confirmClick()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
            cancelClick()
        }
        dialog.show()
    }
}