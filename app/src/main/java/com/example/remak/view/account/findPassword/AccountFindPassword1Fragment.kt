package com.example.remak.view.account.findPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.databinding.FindPassword1FragmentBinding

class AccountFindPassword1Fragment : BaseFragment() {
    private lateinit var binding : FindPassword1FragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FindPassword1FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_accountFindPassword1Fragment_to_accountFindPassword2Fragment)
        }

        binding.root.setOnClickListener {
            hideKeyboard()
        }



        emailCheck(binding.emailEditText, binding.nextBtn, binding.emailErrorMessage)



    }


    //이메일 정규표현식 체크

}