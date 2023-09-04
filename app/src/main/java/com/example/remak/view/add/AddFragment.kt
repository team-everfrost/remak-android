package com.example.remak.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AddFragmentBinding

class AddFragment : Fragment() {
    private val viewModel : AddViewModel by activityViewModels { AddViewModelFactory(tokenRepository)  }
    private lateinit var binding : AddFragmentBinding
    lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = AddFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.linkAddBtn.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_addLinkFragment)
        }
    }


}