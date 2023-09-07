package com.example.remak.view.account.findPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.remak.R
import com.example.remak.UtilityLogin
import com.example.remak.UtilitySystem
import com.example.remak.databinding.FindPassword1FragmentBinding

class AccountFindPassword1Fragment : Fragment() {
    private lateinit var binding: FindPassword1FragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FindPassword1FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_accountFindPassword1Fragment_to_accountFindPassword2Fragment)
        }

        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }

        UtilityLogin.emailCheck(
            requireContext(),
            binding.emailEditText,
            binding.nextBtn,
            binding.emailErrorMessage
        )
    }
}