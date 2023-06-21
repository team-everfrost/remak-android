package com.example.remak

import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    fun hideKeyboard(){
        if (activity != null && requireActivity().currentFocus != null){
            val inputManager : InputMethodManager = requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
        }
    }


}