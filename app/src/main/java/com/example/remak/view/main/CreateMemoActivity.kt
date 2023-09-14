package com.example.remak.view.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
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
            UtilityDialog.showInformDialog(
                "메모가 생성되었습니다.",
                "",
                this,
                confirmClick = {
                    finish()
                }
            )
        }
    }
}