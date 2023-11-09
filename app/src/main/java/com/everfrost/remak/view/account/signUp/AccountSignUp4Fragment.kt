package com.everfrost.remak.view.account.signUp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.everfrost.remak.R
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.AccountSignup4FragmentBinding
import com.everfrost.remak.view.main.MainActivity

class AccountSignUp4Fragment : Fragment() {
    private lateinit var binding: AccountSignup4FragmentBinding
    lateinit var signInRepository: TokenRepository
    private val viewModel: SignUpViewModel by activityViewModels {
        SignUpViewModelFactory(
            signInRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signInRepository =
            TokenRepository((requireActivity().application as com.everfrost.remak.App).dataStore)
        binding = AccountSignup4FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
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

        viewModel.isSignInSuccess.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful == true) {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                viewModel.doneVerifyCodeResult()
                requireActivity().finish()
            }
        }

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.passwordEditText.text.toString() == viewModel.userPassword.value) {
                    binding.passwordEditText.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round)
                    binding.completeBtn.isEnabled = true
                    binding.completeBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.completeBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect_blue_rec
                    )
                    binding.passwordErrorText.visibility = View.INVISIBLE

                } else {
                    binding.passwordEditText.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round_red)
                    binding.completeBtn.isEnabled = false
                    binding.completeBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.disableTextColor
                        )
                    )
                    binding.completeBtn.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
                    binding.passwordErrorText.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.completeBtn.setOnClickListener {
            viewModel.signup(viewModel.userEmail.value!!, viewModel.userPassword.value!!)

        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_accountSignUp4Fragment_to_accountSignUp3Fragment)
        }
    }

}