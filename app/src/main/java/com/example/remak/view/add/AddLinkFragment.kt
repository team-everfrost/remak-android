package com.example.remak.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.remak.App
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AddLinkFragmentBinding

class AddLinkFragment : Fragment() {
    private lateinit var binding : AddLinkFragmentBinding
    private val viewModel : AddViewModel by activityViewModels { AddViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = AddLinkFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


}