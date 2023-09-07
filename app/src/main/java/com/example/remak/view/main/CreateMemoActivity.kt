package com.example.remak.view.main

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.remak.App
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.EditPageMemoBinding

class CreateMemoActivity : AppCompatActivity() {
    private lateinit var binding: EditPageMemoBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository: TokenRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = EditPageMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.contentEditText.text = null

        binding.contentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    binding.completeBtn.isEnabled = true
                    binding.completeBtn.background = AppCompatResources.getDrawable(
                        this@CreateMemoActivity,
                        R.drawable.custom_ripple_effect_blue_rec
                    )
                    binding.completeBtn.setTextColor(getColor(R.color.white))
                } else {
                    binding.completeBtn.isEnabled = false
                    binding.completeBtn.background = AppCompatResources.getDrawable(
                        this@CreateMemoActivity,
                        R.drawable.custom_ripple_effect
                    )
                    binding.completeBtn.setTextColor(getColor(R.color.whiteGray))
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.completeBtn.setOnClickListener {
            viewModel.createMemo(binding.contentEditText.text.toString())
        }

        viewModel.isMemoCreateSuccess.observe(this) {
            showDialog(it)
        }
    }

    private fun showDialog(getContent: String) {
        val dialog = Dialog(this)
        val windowManager =
            this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_information)

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)

            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val x = (size.x * 0.85).toInt()
            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (rect.width() * 0.85).toInt()
            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        }
        val confirmBtn = dialog.findViewById<View>(R.id.confirmBtn)
        val content = dialog.findViewById<TextView>(R.id.msgTextView)
        content.text = getContent

        confirmBtn.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }
}