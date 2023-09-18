package com.example.remak.view.account.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.UtilityLogin
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountSignup1FragmentBinding

class AccountSignUp1Fragment : Fragment() {
    private lateinit var binding: AccountSignup1FragmentBinding

    private val viewModel: SignUpViewModel by activityViewModels {
        SignUpViewModelFactory(
            signInRepository
        )
    }

    lateinit var signInRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signInRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = AccountSignup1FragmentBinding.inflate(inflater, container, false)

        //상태바 높이만큼 margin적용
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight: Int = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            // 기본값 또는 예상되는 높이
            24 * resources.displayMetrics.density.toInt()
        }
        val layoutParams = binding.rootLayout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, statusBarHeight, 0, 0)
        binding.rootLayout.layoutParams = layoutParams

        viewModel.isSignInSuccess.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountSignUp2Fragment2)
                viewModel.doneVerifyCodeResult()
                viewModel.setUserEmail(binding.emailEditText.text.toString())
            }
        }

        viewModel.isEmailExist.observe(viewLifecycleOwner) { isEmailExist ->
            if (isEmailExist) {
                //키보드 내리기
                UtilitySystem.hideKeyboard(requireActivity())
                UtilityDialog.showInformDialog(
                    "이미 존재하는 이메일입니다.",
                    "중복 된 이메일은 사용할수 없어요",
                    requireContext(),
                    confirmClick = { viewModel.resetIsEmailExit() }
                )
            }
        }



        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountMainFragment)
        }
        binding.nextBtn.setOnClickListener {
            viewModel.getVerifyCode(binding.emailEditText.text.toString())

        }
        UtilityLogin.emailCheck(
            requireContext(),
            binding.emailEditText,
            binding.nextBtn,
            binding.emailErrorMessage
        )

    }

}