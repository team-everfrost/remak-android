package com.everfrost.remak.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.DetailPageMemoActivityBinding
import com.everfrost.remak.view.collection.EditCollectionBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint
class MemoDetailActivity : AppCompatActivity() {
    private lateinit var binding: DetailPageMemoActivityBinding
    private val viewModel: DetailViewModel by viewModels()
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
            val popupView = layoutInflater.inflate(R.layout.custom_popup_menu_image_and_file, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true
            popupWindow.isOutsideTouchable = true
            val addBtn: TextView = popupView.findViewById(R.id.addBtn)
            val deleteBtn: TextView = popupView.findViewById(R.id.deleteBtn)
            addBtn.setOnClickListener {
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
                popupWindow.dismiss()
            }
            deleteBtn.setOnClickListener {
                UtilityDialog.showWarnDialog(
                    this,
                    "파일을 삭제하시겠습니까?",
                    "삭제시 복구가 불가능해요",
                    "삭제하기",
                    "취소하기",
                    confirmClick = {
                        viewModel.deleteDocument(memoId)
                        finish()
                    },
                    cancelClick = {}
                )
                popupWindow.dismiss()
            }
            popupWindow.showAsDropDown(it)
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