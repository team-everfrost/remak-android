package com.everfrost.remak.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.everfrost.remak.R
import com.everfrost.remak.UtilityLogin
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.ProfileResetPassword1FragmentBinding
import com.everfrost.remak.view.account.signIn.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileResetPassword1Fragment : Fragment() {
    private lateinit var binding: ProfileResetPassword1FragmentBinding
    private val viewModel: SignInViewModel by activityViewModels()
    lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ProfileResetPassword1FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            com.everfrost.remak.UtilitySystem.hideKeyboard(requireActivity())
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
        }
    }

}