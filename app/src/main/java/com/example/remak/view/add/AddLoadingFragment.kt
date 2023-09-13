package com.example.remak.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.remak.App
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.LoadingUploadFragmentBinding

class AddLoadingFragment : Fragment() {
    private lateinit var binding: LoadingUploadFragmentBinding
    private val viewModel: AddViewModel by activityViewModels { AddViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = LoadingUploadFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .asGif()
            .load(R.drawable.loading)
            .into(binding.loadingImage)

        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UploadState.SUCCESS -> {
                    findNavController().navigate(R.id.action_addLoadingFragment_to_addFragment)
                }

                else -> {}
            }
        }
    }
}