package com.everfrost.remak.view.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.LoadingUploadFragmentBinding

class AddLoadingFragment : Fragment() {
    private lateinit var binding: LoadingUploadFragmentBinding
    private val viewModel: AddViewModel by activityViewModels { AddViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository =
            TokenRepository((requireActivity().application as com.everfrost.remak.App).dataStore)
        binding = LoadingUploadFragmentBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .asGif()
            .load(R.drawable.loading)
            .into(binding.loadingImage)

        binding.completeBtn.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("isDelete", true)
            requireActivity().setResult(Activity.RESULT_OK, resultIntent)
            requireActivity().finish()
        }

        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UploadState.SUCCESS -> {
//                    findNavController().navigate(R.id.action_addLoadingFragment_to_addFragment)
                    binding.loadingImage.visibility = View.GONE
                    binding.loadingLottie.visibility = View.VISIBLE
                    binding.loadingLottie.setAnimation(R.raw.animation)
                    binding.loadingLottie.playAnimation()
                    binding.loadingText.visibility = View.GONE
                    binding.completeBtn.visibility = View.VISIBLE
                }

                UploadState.FAIL -> {

                }

                else -> {}
            }
        }

        viewModel.isFileTooLarge.observe(viewLifecycleOwner) {
            if (it) {
                UtilityDialog.showInformDialog("파일이 너무 큽니다.",
                    "최대 10mb까지 업로드 가능합니다.",
                    requireContext(),
                    confirmClick = {
                        requireActivity().finish()
                    })
            }
        }

    }
}