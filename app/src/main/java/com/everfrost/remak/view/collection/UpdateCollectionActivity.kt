package com.everfrost.remak.view.collection

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.CollectionUpdateActivityBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UpdateCollectionActivity : AppCompatActivity() {
    private lateinit var binding: CollectionUpdateActivityBinding
    private val viewModel: CollectionViewModel by viewModels()
    private lateinit var tokenRepository: TokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CollectionUpdateActivityBinding.inflate(layoutInflater)
        val collectionName = intent.getStringExtra("collectionName")
        val collectionDescription = intent.getStringExtra("collectionDescription")
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(this)
        }
        setContentView(binding.root)
        binding.collectionNameEditText.setText(collectionName)
        binding.collectionDescriptionEditText.setText(collectionDescription)

        binding.collectionNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (isEditTextEmpty()) {
                    binding.completeBtn.isEnabled = false
                    binding.completeBtn.setTextColor(
                        ContextCompat.getColor(
                            this@UpdateCollectionActivity,
                            com.everfrost.remak.R.color.unselectedColor
                        )
                    )
                    binding.completeBtn.background =
                        ContextCompat.getDrawable(
                            this@UpdateCollectionActivity,
                            com.everfrost.remak.R.drawable.custom_ripple_effect
                        )
                } else {
                    binding.completeBtn.isEnabled = true
                    binding.completeBtn.setTextColor(
                        ContextCompat.getColor(
                            this@UpdateCollectionActivity,
                            com.everfrost.remak.R.color.white
                        )
                    )
                    binding.completeBtn.background =
                        ContextCompat.getDrawable(
                            this@UpdateCollectionActivity,
                            com.everfrost.remak.R.drawable.custom_ripple_effect_blue_rec
                        )
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewModel.isDuplicateName.observe(this) {
            if (it == true) {
                UtilityDialog.showInformDialog(
                    "이미 존재하는 이름입니다.",
                    "",
                    this,
                    confirmClick = { viewModel.resetDuplicateName() }
                )
            } else if (it == false) {
                UtilityDialog.showInformDialog(
                    "컬렉션이 수정되었습니다.",
                    "",
                    this,
                    confirmClick = {
                        val resultIntent = Intent()
                        resultIntent.putExtra("isChange", true)
                        resultIntent.putExtra(
                            "newName",
                            binding.collectionNameEditText.text.toString()
                        )
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                )

            }
        }

        binding.completeBtn.setOnClickListener {
            if (binding.collectionNameEditText.text.isNotBlank()) {
                viewModel.updateCollection(
                    collectionName!!,
                    binding.collectionNameEditText.text.toString(),
                    binding.collectionDescriptionEditText.text.toString()
                )
            } else {
                UtilityDialog.showInformDialog(
                    "컬렉션 이름을 입력해주세요.",
                    "빈 이름은 사용할 수 없어요",
                    this,
                    confirmClick = {}
                )
            }
        }
    }

    private fun isEditTextEmpty(): Boolean {
        return binding.collectionNameEditText.text.toString().isEmpty()
    }

}