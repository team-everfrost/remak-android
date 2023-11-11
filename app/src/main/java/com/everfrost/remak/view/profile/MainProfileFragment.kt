package com.everfrost.remak.view.profile

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.everfrost.remak.R
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.MainProfileFragmentBinding
import com.everfrost.remak.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainProfileFragment : Fragment() {
    private lateinit var binding: MainProfileFragmentBinding
    private val viewModel: ProfileViewModel by activityViewModels()
    lateinit var tokenRepository: TokenRepository
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainProfileFragmentBinding.inflate(inflater, container, false)


        //뒤로가기 시 홈 프래그먼트로 이동
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
            isEnabled = false
        }
        viewModel.getStorageSize()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }

        viewModel.usagePercent.observe(viewLifecycleOwner) {
            binding.usageText.text =
                "${viewModel.usageSize.value}/${viewModel.storageSize.value}GB (${it}%) 사용중"

        }



        binding.editBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.from_right,
                R.anim.to_left,
                R.anim.from_left,
                R.anim.to_right
            )
            transaction.replace(R.id.mainFragmentContainerView, ProfileEditFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.privacyLayout.setOnClickListener {
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

        binding.termsOfServiceLayout.setOnClickListener {
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

        binding.versionText.text = getString(R.string.version_name)


    }

    //ondestroy

}