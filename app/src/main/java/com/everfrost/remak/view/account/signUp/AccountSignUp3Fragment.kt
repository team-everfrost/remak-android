package com.everfrost.remak.view.account.signUp

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
import com.everfrost.remak.UtilityLogin
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.AccountSignup3FragmentBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AccountSignUp3Fragment : Fragment() {
    private lateinit var binding: AccountSignup3FragmentBinding
    lateinit var signInRepository: TokenRepository
    private val viewModel: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = AccountSignup3FragmentBinding.inflate(inflater, container, false)
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

        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                UtilityLogin.passwordEnglishCheck(
                    requireContext(),
                    binding.passwordEditText.text.toString(),
                    binding.englishCheck
                )
                UtilityLogin.passwordNumberCheck(
                    requireContext(),
                    binding.passwordEditText.text.toString(),
                    binding.numberCheck
                )
                UtilityLogin.passwordLengthCheck(
                    requireContext(),
                    binding.passwordEditText.text.toString(),
                    binding.nineCheck
                )
                if (UtilityLogin.isPasswordValid(binding.passwordEditText.text.toString())) {
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
                } else {
                    binding.completeBtn.isEnabled = false
                    binding.completeBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.disableTextColor
                        )
                    )
                    binding.completeBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })



        binding.completeBtn.setOnClickListener {
            viewModel.setUserPassword(binding.passwordEditText.text.toString())
            findNavController().navigate(R.id.action_accountSignUp3Fragment_to_accountSignUp4Fragment)

        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_accountSignUp3Fragment_to_accountSignUp2Fragment2)
        }
    }

}