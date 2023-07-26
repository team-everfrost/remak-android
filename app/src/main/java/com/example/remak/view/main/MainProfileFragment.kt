package com.example.remak.view.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import com.example.remak.App
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.MainProfileFragmentBinding
import com.example.remak.view.account.AccountActivity

class MainProfileFragment : BaseFragment() {
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
            hideKeyboard()
        }
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)

        //뒤로가기 시 홈 프래그먼트로 이동
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
            isEnabled = false

        }

        binding.logoutButton.setOnClickListener {
            showWarnDialog("로그아웃 하시겠습니까?")
        }

        return binding.root
    }




    //ondestroy
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("MainProfileFragment", "onDestroy")

        (view as ViewGroup).removeAllViews()

    }


    private fun showWarnDialog(getContent : String) {
        val dialog = Dialog(requireContext())
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_warning)

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val x = (size.x * 0.85).toInt()


            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        } else {
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (rect.width() * 0.85).toInt()
            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        }
        val confirmBtn = dialog.findViewById<View>(R.id.confirmBtn)
        val cancelBtn = dialog.findViewById<View>(R.id.cancelBtn)
        val content = dialog.findViewById<TextView>(R.id.msgTextView)
        content.text = getContent

        confirmBtn.setOnClickListener {
            viewModel.deleteToken()
            dialog.dismiss()
            val intent = Intent(requireContext(), AccountActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()


    }
}