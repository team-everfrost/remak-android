package com.example.remak.view.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.remak.R
import com.example.remak.UtilityLogin
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountResetPassword1FragmentBinding
import com.example.remak.view.account.signIn.SignInViewModel
import com.example.remak.view.account.signIn.SignInViewModelFactory

class ProfileResetPassword1Fragment : Fragment() {
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
            TokenRepository((requireActivity().application as com.example.remak.App).dataStore)
        binding = AccountResetPassword1FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            com.example.remak.UtilitySystem.hideKeyboard(requireActivity())
        }
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
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.from_right,
                    R.anim.to_left,
                    R.anim.from_left,
                    R.anim.to_right
                )
                transaction.replace(R.id.mainFragmentContainerView, ProfileResetPassword2Fragment())
                transaction.addToBackStack(null)
                transaction.commit()
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