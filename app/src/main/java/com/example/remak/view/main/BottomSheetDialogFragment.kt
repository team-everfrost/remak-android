package com.example.remak.view.main

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.databinding.BottomSheetDialogBinding
import com.example.remak.network.model.UploadFileData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import java.net.URI

class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            val mimeType = requireActivity().contentResolver.getType(uri!!)
            val inputStream = requireActivity().contentResolver.openInputStream(uri!!)
            val file = inputStreamToFile(inputStream!!,uri, requireActivity().contentResolver.getType(uri))
            val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
            val fileList = List<MultipartBody.Part>(1) {
                MultipartBody.Part.createFormData("files", file.name, requestFile)
            }
            viewModel.uploadFile(fileList)
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
            }
            getContent.launch(intent)
        }

        binding.linearImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
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
            if (linkEditText.text.toString().isEmpty()) {
                showInformDialog("링크를 입력해주세요.")
            } else {
                val tempUrl = linkEditText.text.toString()
                val splitText = tempUrl.split("\\n|,".toRegex())

                val urlList = ArrayList<String>()
                for (item in splitText) {
                    urlList.add(item.trim())
                }

                Log.d("urlList", urlList.toString())

                for (i in urlList) {
                    var url = i
                    if (!i.startsWith("http://") && !i.startsWith("https://")) {
                        url = "https://$i"
                        viewModel.createWebPage(url)
                    } else {
                        viewModel.createWebPage(url)
                    }
                }

//                if (!tempUrl.startsWith("http://") && !tempUrl.startsWith("https://")) {
//                    tempUrl = "https://$tempUrl"
//                    viewModel.createWebPage(tempUrl)
//                } else {
//                    viewModel.createWebPage(linkEditText.text.toString())
//                }
                dialog.dismiss()
                (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
                this.dismiss()
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
    private fun inputStreamToFile(inputStream: InputStream, uri: Uri, mimeType : String?) : File {
        val fileName = getFileNameFromUri(uri)
        val file = File(requireContext().cacheDir, fileName)
        file.outputStream().use { fileOutputStream ->
            inputStream.copyTo(fileOutputStream)
        }
        return file
    }


    fun showInformDialog(msg: String) {
        val dialog = Dialog(requireContext())
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_information)

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (size.x * 0.7).toInt()
            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)
        } else {
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (rect.width() * 0.7).toInt()
            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        val confirmBtn = dialog.findViewById<View>(R.id.confirmBtn)
        confirmBtn.setOnClickListener {
            dialog.dismiss()
        }
        val msgText = dialog.findViewById<TextView>(R.id.msgTextView)
        msgText.text = msg
        dialog.show()
    }
}