package com.example.remak.view.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.EditPageMemoBinding

class AddMemoFragment : Fragment() {
    private lateinit var binding: EditPageMemoBinding
    private val viewModel: AddViewModel by viewModels { AddViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository = TokenRepository((this.activity?.application as App).dataStore)
        binding = EditPageMemoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.contentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    binding.completeBtn.isEnabled = true
                    binding.completeBtn.background = AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect_blue_rec
                    )
                    binding.completeBtn.setTextColor(requireContext().getColor(R.color.white))
                } else {
                    binding.completeBtn.isEnabled = false
                    binding.completeBtn.background = AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect
                    )
                    binding.completeBtn.setTextColor(requireContext().getColor(R.color.whiteGray))
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        viewModel.isMemoComplete.observe(viewLifecycleOwner) {
            if (it) {
                UtilityDialog.showInformDialog(
                    "메모가 생성되었습니다.",
                    "",
                    requireContext(),
                    confirmClick = {
                        val resultIntent = Intent()
                        resultIntent.putExtra("isDelete", true)
                        requireActivity().setResult(Activity.RESULT_OK, resultIntent)
                        requireActivity().finish()
                    }
                )
            }
        }
        binding.completeBtn.setOnClickListener {
            viewModel.addMemo(binding.contentEditText.text.toString())
        }
    }
}