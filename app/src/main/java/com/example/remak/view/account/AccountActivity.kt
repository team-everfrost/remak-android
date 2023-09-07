package com.example.remak.view.account

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.remak.R
import com.example.remak.databinding.AccountActivityBinding
import com.kakao.sdk.common.util.Utility

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: AccountActivityBinding
    private lateinit var navController: NavController


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //화면을 상태바까지 확장
        window.insetsController?.let { controller ->
            val appearance = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            controller.setSystemBarsAppearance(appearance, appearance)
        }
        window.setDecorFitsSystemWindows(false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()

        var keyHash = Utility.getKeyHash(this)
        Log.d("keyHash", "onCreate: $keyHash")

    }


}