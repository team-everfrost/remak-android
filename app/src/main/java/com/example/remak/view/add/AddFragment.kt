package com.example.remak.view.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AddFragmentBinding
import com.example.remak.view.main.CreateMemoActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream

class AddFragment : Fragment() {

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
                    for (uri in uriList) {
                        val mimeType = requireActivity().contentResolver.getType(uri)
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        val file = inputStreamToFile(inputStream!!, uri)
                        val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
                        fileList.add(
                            MultipartBody.Part.createFormData(
                                "files",
                                file.name,
                                requestFile
                            )
                        )
                    }
                    viewModel.uploadFile(fileList)
                }
            }
        }

    private val viewModel: AddViewModel by activityViewModels { AddViewModelFactory(tokenRepository) }
    private lateinit var binding: AddFragmentBinding
    lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = AddFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uploadFileSuccess.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                UtilityDialog.showInformDialog("파일 업로드에 성공했습니다.", requireContext())
                viewModel.resetUploadFileSuccess()
            }
        }

        binding.linkAddBtn.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_addLinkFragment)
        }

        binding.fileAddBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            getContent.launch(intent)
        }

        //사진 생성 이벤트 추가
        binding.photoAddBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            getContent.launch(intent)
        }

        binding.memoAddBtn.setOnClickListener {
            val intent = Intent(activity, CreateMemoActivity::class.java)
            startActivity(intent)
        }

    }

    //uri에서 파일 가져오기
    private fun inputStreamToFile(inputStream: InputStream, uri: Uri): File {
        val fileName = getFileNameFromUri(uri)
        val file = File(requireContext().cacheDir, fileName)
        file.outputStream().use { fileOutputStream ->
            inputStream.copyTo(fileOutputStream)
        }
        return file
    }

    //uri에서 파일명 가져오기
    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        requireActivity().contentResolver.query(uri, null, null, null, null)?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            fileName = it.getString(nameIndex)
        }
        return fileName
    }

}