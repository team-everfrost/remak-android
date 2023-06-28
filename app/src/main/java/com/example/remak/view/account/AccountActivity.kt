package com.example.remak.view.account

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.remak.databinding.AccountActivityBinding
import com.example.remak.R
import com.kakao.sdk.common.util.Utility

class AccountActivity : AppCompatActivity() {
    private lateinit var binding : AccountActivityBinding
    private lateinit var navController : NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()

        var keyHash = Utility.getKeyHash(this)


        Log.d("keyHash", "onCreate: $keyHash")

    }




}