package com.example.remak

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

object UtilitySystem {
    fun hideKeyboard(activity: Activity) {
        val inputManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = activity.currentFocus
        if (currentFocusView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    fun showKeyboard(activity: Activity) {
        val inputManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = activity.currentFocus
        if (currentFocusView != null) {
            inputManager.showSoftInput(currentFocusView, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}