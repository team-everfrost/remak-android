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
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.R
import com.example.remak.databinding.DetailPageMemoActivityBinding

class MemoDetailActivity : AppCompatActivity() {
    private lateinit var binding : DetailPageMemoActivityBinding

    private val viewModel : DetailViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DetailPageMemoActivityBinding.inflate(layoutInflater)
        binding.root.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

        }
        setContentView(binding.root)

        val memoId = intent.getStringExtra("docId")

        Log.d("memoId", memoId.toString())
        viewModel.getDetailData(memoId!!)

        viewModel.detailData.observe(this, {
            binding.memoContent.setText(it.content)
        })

        binding.editBtn.setOnClickListener {
            binding.memoContent.isEnabled = true
            binding.memoContent.isFocusableInTouchMode = true
            binding.memoContent.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.memoContent, InputMethodManager.SHOW_IMPLICIT)
            binding.editBtn.visibility = View.GONE
            binding.completeBtn.visibility = View.VISIBLE

        }

        binding.completeBtn.setOnClickListener {
            showDialog("수정하시겠습니까?", memoId)

        }






    }



    private fun showDialog(getContent : String, memoId : String) {
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
            viewModel.updateMemo(memoId, binding.memoContent.text.toString())
            binding.memoContent.isEnabled = false
            binding.completeBtn.visibility = View.GONE
            binding.editBtn.visibility = View.VISIBLE
            dialog.dismiss()

        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()


    }
}