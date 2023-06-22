package com.example.remak.signUp

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.databinding.AccountSignup1FragmentBinding

class AccountSignUp1Fragment : BaseFragment() {
    private lateinit var binding : AccountSignup1FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountSignup1FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountMainFragment)
        }
        binding.nextBtn.setOnClickListener{
            findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountSignUp2Fragment2)
        }
        emailTextChange()

        //기기의 뒤로가기 버튼 재정의
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountMainFragment)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("destroy", "onDestroy:")
    }

    //emailText변경감지
    fun emailTextChange(){
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(isEmailValid(s.toString())){
                    binding.nextBtn.setBackgroundColor(Color.parseColor("#FF0000"))
                    binding.nextBtn.isEnabled = true
                    val drawable = ContextCompat.getDrawable(requireContext(),
                        R.drawable.custom_ripple_effect
                    )
                    binding.nextBtn.background = drawable
                }else{

                    binding.nextBtn.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //do nothing
            }
        })
    }

    //이메일 정규표현식 체크
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }





}