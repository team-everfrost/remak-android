package com.example.remak.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.remak.R
import com.example.remak.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : MainActivityBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()




    }
}