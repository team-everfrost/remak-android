package com.example.remak.view.main

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.remak.R
import com.example.remak.databinding.BottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import java.net.URI

class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var tempFile: File
    val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            val mimeType = requireActivity().contentResolver.getType(uri!!)
            val inputStream = requireActivity().contentResolver.openInputStream(uri!!)
            val file = inputStreamToFile(inputStream!!, requireActivity().contentResolver.getType(uri))
            val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())


            val fileList = List<MultipartBody.Part>(1) {
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            }
            viewModel.uploadFile(fileList)



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

        binding.linearNote.setOnClickListener {
            val intent = Intent(activity, CreateMemoActivity::class.java)
            startActivity(intent)
            this.dismiss()

        }

        //파일클릭 이벤트 추가
        binding.linearFile.setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
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

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        confirmBtn.setOnClickListener {

            dialog.dismiss()

        }
        dialog.show()


    }

    private fun inputStreamToFile(inputStream: InputStream, mimeType : String?) : File {
        val file = File(requireContext().cacheDir, "temp")
        file.outputStream().use { fileOutputStream ->
            inputStream.copyTo(fileOutputStream)
        }
        return file
    }
}