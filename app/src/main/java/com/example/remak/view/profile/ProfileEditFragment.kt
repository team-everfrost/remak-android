package com.example.remak.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.EditProfileFragmentBinding
import com.example.remak.view.account.AccountActivity
import com.example.remak.view.main.MainViewModel
import com.example.remak.view.main.MainViewModelFactory

class ProfileEditFragment : Fragment() {
    private lateinit var binding: EditProfileFragmentBinding
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
        binding = EditProfileFragmentBinding.inflate(inflater, container, false)
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        viewModel.getUserData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userData.observe(viewLifecycleOwner) {
            binding.emailText.text = it.email
        }

        binding.logoutButton.setOnClickListener {
            UtilityDialog.showWarnDialog(
                requireContext(), "로그아웃",
                "정말 로그아웃 하시겠습니까?",
                "네",
                "아니오",
                confirmClick = {
                    viewModel.deleteToken()
                    val intent = Intent(requireContext(), AccountActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }, cancelClick = {
                    //do nothing
                })
        }

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.passwordLayout.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.from_right,
                R.anim.to_left,
                R.anim.from_left,
                R.anim.to_right
            )
            transaction.replace(R.id.mainFragmentContainerView, ProfileResetPassword1Fragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

}
