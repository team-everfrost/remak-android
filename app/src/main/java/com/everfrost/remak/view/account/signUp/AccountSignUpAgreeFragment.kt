package com.everfrost.remak.view.account.signUp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.everfrost.remak.R
import com.everfrost.remak.databinding.AccountSignupPrivacyFragmentBinding

class AccountSignUpAgreeFragment : Fragment() {
    private lateinit var binding: AccountSignupPrivacyFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountSignupPrivacyFragmentBinding.inflate(inflater, container, false)
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

        binding.agreeAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.agreeAllCheckBox.isChecked = true
                binding.privacyCheckBox.isChecked = true
                binding.termsOfServiceCheckBox.isChecked = true
                updateButtonState()
            } else {
                binding.agreeAllCheckBox.isChecked = false
                binding.privacyCheckBox.isChecked = false
                binding.termsOfServiceCheckBox.isChecked = false
                updateButtonState()
            }
        }

        binding.agreeAllLayout.setOnClickListener {
            if (binding.agreeAllCheckBox.isChecked) {
                binding.agreeAllCheckBox.isChecked = false
                binding.privacyCheckBox.isChecked = false
                binding.termsOfServiceCheckBox.isChecked = false
                updateButtonState()
            } else {
                binding.agreeAllCheckBox.isChecked = true
                binding.privacyCheckBox.isChecked = true
                binding.termsOfServiceCheckBox.isChecked = true
                updateButtonState()
            }
        }

        binding.privacyCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.privacyCheckBox.isChecked = true
                updateButtonState()
            } else {
                binding.privacyCheckBox.isChecked = false
                updateButtonState()
            }
        }

        binding.privacyLayout.setOnClickListener {
            if (binding.privacyCheckBox.isChecked) {
                binding.privacyCheckBox.isChecked = false
                updateButtonState()
            } else {
                binding.privacyCheckBox.isChecked = true
                updateButtonState()
            }
        }

        binding.termsOfServiceCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.termsOfServiceCheckBox.isChecked = true
                updateButtonState()
            } else {
                binding.termsOfServiceCheckBox.isChecked = false
                updateButtonState()
            }
        }

        binding.termsOfServiceLayout.setOnClickListener {
            if (binding.termsOfServiceCheckBox.isChecked) {
                binding.termsOfServiceCheckBox.isChecked = false
                updateButtonState()
            } else {
                binding.termsOfServiceCheckBox.isChecked = true
                updateButtonState()
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_accountSignUpAgreeFragment_to_accountEmailSignInFragment)
        }

        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_accountSignUpAgreeFragment_to_accountSignUp1Fragment2)
        }

        binding.privacyArrow.setOnClickListener {
            val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.black))
                .build()
            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorSchemeParams)
                .build()
            customTabsIntent.launchUrl(
                requireContext(),
                Uri.parse("https://remak.io/terms-of-service")
            )
        }

        binding.termsOfServiceArrow.setOnClickListener {
            val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.black))
                .build()
            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorSchemeParams)
                .build()
            customTabsIntent.launchUrl(
                requireContext(),
                Uri.parse("https://remak.io/privacy-policy")
            )
        }
    }

    private fun updateButtonState() {
        binding.nextBtn.isEnabled =
            binding.privacyCheckBox.isChecked && binding.termsOfServiceCheckBox.isChecked
        if (binding.nextBtn.isEnabled) {
            binding.nextBtn.setTextColor(
                resources.getColor(
                    R.color.white,
                    requireContext().theme
                )
            )
            binding.nextBtn.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.custom_ripple_effect_blue_rec
            )
        } else {
            binding.nextBtn.setTextColor(
                resources.getColor(
                    R.color.whiteGray,
                    requireContext().theme
                )
            )
            binding.nextBtn.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
        }
    }

}