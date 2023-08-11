package com.example.remak.view.main

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.databinding.BottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream

class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val clipData = result.data?.clipData
            val uriList = mutableListOf<Uri>()
            var isUploadAble = true
            if (clipData != null && clipData.itemCount <= 10) {
                for (i in 0 until clipData.itemCount) {
                    uriList.add(clipData.getItemAt(i).uri)
                }
            } else if (clipData == null) {  // 단일 파일이 선택된 경우
                result.data?.data?.let {
                    uriList.add(it)
                }
            } else {
                UtilityDialog.showInformDialog("파일은 최대 10개까지 선택 가능합니다.", requireContext())
                isUploadAble = false
            }

            if (isUploadAble) {
                val fileList = mutableListOf<MultipartBody.Part>()
                for (uri in uriList){
                    val mimeType = requireActivity().contentResolver.getType(uri)
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val file = inputStreamToFile(inputStream!!,uri)
                    val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
                    fileList.add(MultipartBody.Part.createFormData("files", file.name, requestFile))
                }
                viewModel.uploadFile(fileList)
            }
            this.dismiss()
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
        }
    }

    private lateinit var binding: BottomSheetDialogBinding
    private val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as View).background = ColorDrawable(Color.TRANSPARENT)
        binding.linearLink.setOnClickListener {
            showDialog()
        }

        //메모생성 이벤트 추가
        binding.linearNote.setOnClickListener {
            val intent = Intent(activity, CreateMemoActivity::class.java)
            startActivity(intent)
            this.dismiss()
        }

        //파일생성 이벤트 추가
        binding.linearFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            getContent.launch(intent)
        }

        binding.linearImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            }
            getContent.launch(intent)
        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_add_link)
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
                true
            } else {
                false
            }
        }

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
        val cancelBtn = dialog.findViewById<View>(R.id.cancelBtn)
        val confirmBtn = dialog.findViewById<View>(R.id.confirmBtn)
        val linkEditText = dialog.findViewById<AppCompatEditText>(R.id.linkEditText)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        confirmBtn.setOnClickListener {
            val url = linkEditText.text.toString().trim()
            if (url.isEmpty()) {
                UtilityDialog.showInformDialog("링크를 입력해주세요.", requireContext())
            } else {
                val splitText = url.split("\\n|,".toRegex()) //줄바꿈, 콤마로 구분
                val urlList = ArrayList<String>()
                for (item in splitText) {
                    urlList.add(item.trim())
                }

                for (i in urlList) {
                    var url = i
                    if (!i.startsWith("http://") && !i.startsWith("https://")) {
                        url = "https://$i"
                        viewModel.createWebPage(url)
                    } else {
                        viewModel.createWebPage(url)
                    }
                }
                dialog.dismiss()
                (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
                this.dismiss()
                viewModel.resetMainData()
                viewModel.getAllMainList()
            }
        }
        dialog.show()
    }

    //uri에서 파일명 가져오기
    private fun getFileNameFromUri(uri: Uri) : String? {
        var fileName: String? = null
        requireActivity().contentResolver.query(uri, null, null, null, null)?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            fileName = it.getString(nameIndex)
        }
        return fileName
    }

    //uri에서 파일 가져오기
    private fun inputStreamToFile(inputStream: InputStream, uri: Uri) : File {
        val fileName = getFileNameFromUri(uri)
        val file = File(requireContext().cacheDir, fileName)
        file.outputStream().use { fileOutputStream ->
            inputStream.copyTo(fileOutputStream)
        }
        return file
    }

}