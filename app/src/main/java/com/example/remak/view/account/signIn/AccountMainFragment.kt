package com.example.remak.view.account.signIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.UtilityLogin
import com.example.remak.UtilitySystem
import com.example.remak.adapter.TagItemOffsetDecoration
import com.example.remak.adapter.TestItemOffsetDecoration
import com.example.remak.adapter.TestRVAdapter
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountMainFragmentBinding
import com.example.remak.view.main.MainActivity
import java.sql.Time
import java.util.Timer
import java.util.TimerTask


class AccountMainFragment : Fragment() {
    private lateinit var binding : AccountMainFragmentBinding
    private var timer = Timer()

    private val viewModel: SignInViewModel by activityViewModels { SignInViewModelFactory(signInRepository) }
    lateinit var signInRepository : TokenRepository
    val iconData = listOf<String>(
        "통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx","통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx",
        "통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx",
        "통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx",
        "통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = AccountMainFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener{
            UtilitySystem.hideKeyboard(requireActivity())
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                viewModel.doneLogin()
                requireActivity().finish()
            }
        }

        UtilityLogin.signInCheck(requireContext(), binding.idEditText, binding.pwEditText, binding.signInBtn)

        viewModel.showDialog.observe(viewLifecycleOwner) { showDialog ->
            if (showDialog) {
                UtilityDialog.showInformDialog( "아이디 또는 비밀번호를 확인해주세요", requireContext())
                viewModel.doneDialog()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       val adapter = TestRVAdapter(iconData)
        binding.recyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.setOnTouchListener{ _, _ ->
            true
        }

        val itemDecoration = TestItemOffsetDecoration(30)
        binding.recyclerView.addItemDecoration(itemDecoration)

        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 2) {
                        // 마지막 아이템에 도달하면 첫 번째 아이템으로 스크롤
                        binding.recyclerView.scrollToPosition(1)
                    } else {
                        binding.recyclerView.scrollBy(1, 0) // 50 픽셀만큼 오른쪽으로 스크롤
                    }
                }
            }
        }, 0, 10) // 100ms 마다 스크롤 동작 실행



        binding.signUpBtn.setOnClickListener{
            findNavController().navigate(R.id.action_accountMainFragment_to_accountSignUp1Fragment2)
        }
        binding.findAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.action_accountMainFragment_to_accountFindPassword1Fragment)
        }

        binding.kakaoBtn.setOnClickListener {
            viewModel.kakaoLogin(requireActivity())

        }
        binding.signInBtn.setOnClickListener{
            viewModel.emailLogin(binding.idEditText.text.toString(), binding.pwEditText.text.toString())
        }

        binding.test.setOnClickListener {
            findNavController().navigate(R.id.action_accountMainFragment_to_accountEmailSignInFragment)
        }




        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
        Log.d("timer", "cancel")
    }





}