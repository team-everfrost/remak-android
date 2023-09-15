package com.example.remak.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.remak.App
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.PrivacyPolicyFragmentBinding
import com.example.remak.view.main.MainViewModel
import com.example.remak.view.main.MainViewModelFactory

class PrivacyPoliceFragment : Fragment() {
    private lateinit var binding: PrivacyPolicyFragmentBinding
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            tokenRepository
        )
    }
    lateinit var tokenRepository: TokenRepository
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PrivacyPolicyFragmentBinding.inflate(inflater, container, false)
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputStream = resources.openRawResource(R.raw.policy)
        val longString = inputStream.bufferedReader().use { it.readText() }
        binding.privacyPolicyText.text = longString
    }

}