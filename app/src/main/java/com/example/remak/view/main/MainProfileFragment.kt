package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.MainProfileFragmentBinding
import com.example.remak.view.account.AccountActivity

class MainProfileFragment : Fragment() {
    private lateinit var binding : MainProfileFragmentBinding
    private val viewModel : MainViewModel by activityViewModels { MainViewModelFactory(tokenRepository)}
    lateinit var tokenRepository: TokenRepository
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainProfileFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)

        //뒤로가기 시 홈 프래그먼트로 이동
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
            isEnabled = false
        }

        binding.logoutButton.setOnClickListener {
            UtilityDialog.showWarnDialog(requireContext(), "로그아웃 하시겠습니까?", confirmClick = {
                viewModel.deleteToken()
                val intent = Intent(requireContext(), AccountActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }, cancelClick = {
                //do nothing
            })
        }

        return binding.root
    }

    //ondestroy

}