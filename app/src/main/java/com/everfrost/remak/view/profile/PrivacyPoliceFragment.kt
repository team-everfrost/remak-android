package com.everfrost.remak.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.everfrost.remak.R
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.PrivacyPolicyFragmentBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PrivacyPoliceFragment : Fragment() {
    private lateinit var binding: PrivacyPolicyFragmentBinding

    lateinit var tokenRepository: TokenRepository
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PrivacyPolicyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputStream = resources.openRawResource(R.raw.policy)
        val longString = inputStream.bufferedReader().use { it.readText() }
        binding.privacyPolicyText.text = longString
    }

}