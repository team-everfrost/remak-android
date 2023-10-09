package com.example.remak.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
import com.example.remak.view.collection.EditCollectionBottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Locale

class MemoDetailActivity : AppCompatActivity() {
    private lateinit var binding: DetailPageMemoActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    var isEditMode = false
    private var initMemo: String = ""

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isEditMode) {
                UtilityDialog.showWarnDialog(
                    this@MemoDetailActivity,
                    "수정을 취소하시겠습니까?",
                    "작성한 내용이 지워져요",
                    "네",
                    "아니오",
                    confirmClick = {
                        endEditMode()
                        binding.memoContent.clearFocus()
                        binding.memoContent.setText(initMemo)
                    },
                    cancelClick = {}
                )
            } else {
                if (initMemo != binding.memoContent.text.toString()) {
                    val resultIntent = Intent()
                    resultIntent.putExtra("isDelete", true)
                    setResult(RESULT_OK, resultIntent)
                }
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
            initMemo = it.content!!
            binding.memoContent.setText(it.content)
            binding.dateText.text = setDate(it.createdAt!!)
        }

        binding.memoContent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startEditMode()
            }
        }

        binding.completeBtn.setOnClickListener {
            viewModel.updateMemo(memoId, binding.memoContent.text.toString())
            initMemo = binding.memoContent.text.toString()
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
                if (initMemo != binding.memoContent.text.toString()) {
                    val resultIntent = Intent()
                    resultIntent.putExtra("isDelete", true)
                    setResult(RESULT_OK, resultIntent)
                }
                finish()
            }
        }

        binding.moreIcon.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.detail_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.addCollection -> {
                        val bundle = Bundle()
                        val selectedItems = ArrayList<String>()
                        selectedItems.add(memoId)
                        if (selectedItems.isNotEmpty()) {
                            bundle.putStringArrayList("selected", selectedItems)
                            bundle.putString("type", "detail")
                            val bottomSheet = EditCollectionBottomSheetDialog()
                            bottomSheet.arguments = bundle
                            bottomSheet.show(
                                supportFragmentManager,
                                "EditCollectionBottomSheetDialog"
                            )
                        }
                        true
                    }

                    R.id.removeBtn -> {
                        UtilityDialog.showWarnDialog(
                            this,
                            "삭제하시겠습니까?",
                            "삭제시 복구가 불가능해요",
                            "삭제하기",
                            "취소하기",
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

        binding.memoContent.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.memoContent, InputMethodManager.SHOW_IMPLICIT)
            if (!isEditMode) {
                binding.memoContent.setSelection(binding.memoContent.text.length)
            }
        }
    }

    private fun setDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(date)
        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val outputDateStr = outputFormat.format(date)
        return outputDateStr
    }

    private fun startEditMode() {
        isEditMode = true
        binding.completeBtn.visibility = View.VISIBLE
        binding.moreIcon.visibility = View.GONE
    }

    private fun endEditMode() {
        isEditMode = false
        binding.completeBtn.visibility = View.GONE
        binding.moreIcon.visibility = View.VISIBLE
    }

    private fun setFirstLineBold(editText: EditText) {
        val content = editText.text.toString()
        val spannable = SpannableStringBuilder(content)
        val spans: Array<Any> = editText.text.getSpans(0, content.length, Any::class.java)
        var isComposing = false
        for (span in spans) {
            if (editText.text.getSpanFlags(span) and Spanned.SPAN_COMPOSING == Spanned.SPAN_COMPOSING) {
                isComposing = true
                break
            }
        }

        if (!isComposing) {
            editText.removeTextChangedListener(textWatcher)
            val firstNewLineIndex = content.indexOf('\n')
            if (firstNewLineIndex > 0) {
                spannable.setSpan(
                    android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    0, firstNewLineIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else if (content.isNotEmpty()) {
                spannable.setSpan(
                    android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    0, content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            editText.text = spannable
            editText.setSelection(content.length) // 커서를 텍스트 끝으로 이동
            editText.addTextChangedListener(textWatcher)  // 다시 TextWatcher 추가
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            setFirstLineBold(binding.memoContent)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

}