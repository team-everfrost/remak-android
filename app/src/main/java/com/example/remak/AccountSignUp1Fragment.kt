package com.example.remak

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.remak.databinding.AccountSignup1FragmentBinding

class AccountSignUp1Fragment : Fragment() {
    private lateinit var binding : AccountSignup1FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountSignup1FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
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
    }


    //emailText변경감지
    fun emailTextChange(){
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(isEmailValid(s.toString())){
                    binding.nextBtn.setBackgroundColor(Color.parseColor("#FF0000"))
                    binding.nextBtn.isEnabled = true
                    val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
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