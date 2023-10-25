package com.everfrost.remak.view.account.signIn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.everfrost.remak.R
import com.everfrost.remak.UtilityLogin
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.AccountResetPassword1FragmentBinding

class AccountResetPassword1Fragment : Fragment() {
    private lateinit var binding: AccountResetPassword1FragmentBinding
    private val viewModel: SignInViewModel by activityViewModels {
        SignInViewModelFactory(
            tokenRepository
        )
    }
    lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository =
            TokenRepository((requireActivity().application as com.everfrost.remak.App).dataStore)
        binding = AccountResetPassword1FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            com.everfrost.remak.UtilitySystem.hideKeyboard(requireActivity())
        }

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UtilityLogin.emailCheck(
            requireContext(),
            binding.emailEditText,
            binding.nextBtn,
            binding.emailErrorMessage
        )

        viewModel.isResetEmailValid.observe(viewLifecycleOwner) {
            if (it) {
                binding.emailErrorMessage.visibility = View.INVISIBLE
                viewModel.resetEmailValid()
                findNavController().navigate(R.id.action_accountResetPassword1Fragment_to_accountResetPassword2Fragment)
            } else {
                binding.emailErrorMessage.visibility = View.VISIBLE
            }
        }

        binding.nextBtn.setOnClickListener {
            viewModel.getResetPasswordCode(binding.emailEditText.text.toString())
            Log.d("test", "onViewCreated: ${binding.emailEditText.text.toString()}")
        }
    }

}