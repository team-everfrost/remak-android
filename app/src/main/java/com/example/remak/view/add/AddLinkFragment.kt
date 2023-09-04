package com.example.remak.view.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AddLinkFragmentBinding

class AddLinkFragment : Fragment() {
    private lateinit var binding : AddLinkFragmentBinding
    private val viewModel : AddViewModel by viewModels { AddViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = AddLinkFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addLinkEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    binding.addBtn.isEnabled = true
                    binding.addBtn.background = ContextCompat.getDrawable(requireContext(), com.example.remak.R.drawable.custom_ripple_effect_blue_rec)
                    binding.addBtn.setTextColor(ContextCompat.getColor(requireContext(), com.example.remak.R.color.white))
                } else {
                    binding.addBtn.isEnabled = false
                    binding.addBtn.background = ContextCompat.getDrawable(requireContext(), com.example.remak.R.drawable.custom_ripple_effect)
                    binding.addBtn.setTextColor(ContextCompat.getColor(requireContext(), com.example.remak.R.color.whiteGray))
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.addBtn.setOnClickListener {
            val url = binding.addLinkEditText.text.toString().trim()
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
            requireActivity().finish()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_addLinkFragment_to_addFragment)
            TODO("현재 프래그먼트 지워지게 처리")
        }
    }


}