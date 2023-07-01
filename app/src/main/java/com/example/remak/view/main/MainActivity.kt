package com.example.remak.view.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.remak.R
import com.example.remak.databinding.MainActivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding : MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigation
        val navController = supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView)?.findNavController() as NavHostController

        bottomNavigationView.setupWithNavController(navController)

        binding.addBtn.setOnClickListener {
            Log.d("test", "test")


            val myBottomSheet = BottomSheetDialogFragment()
            myBottomSheet.show(supportFragmentManager, myBottomSheet.tag)
        }

    }



}