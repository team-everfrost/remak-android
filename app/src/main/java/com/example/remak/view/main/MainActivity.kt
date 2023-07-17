package com.example.remak.view.main

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.remak.R
import com.example.remak.databinding.MainActivityBinding
import com.example.remak.databinding.MainHomeFragmentBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private val homeFragment = MainHomeFragment()
    private val searchFragment = MainSearchFragment()
    private val tagFragment = MainTagFragment()
    private val profileFragment = MainProfileFragment()
    private val blankFragment = blank()

    private var _binding : MainActivityBinding? = null
     val binding get() = _binding!!

    // Active Fragment Tracker
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(R.id.mainFragmentContainerView, homeFragment)
            .add(R.id.mainFragmentContainerView, searchFragment).hide(searchFragment)
            .add(R.id.mainFragmentContainerView, tagFragment).hide(tagFragment)
            .add(R.id.mainFragmentContainerView, profileFragment).hide(profileFragment)
            .add(R.id.mainFragmentContainerView, blankFragment).hide(blankFragment)
            .commit()

        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, homeFragment).commit()
                    activeFragment = homeFragment
                    true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, searchFragment).commit()
                    activeFragment = searchFragment
                    true
                }
                R.id.blankFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, blankFragment).commit()
                    activeFragment = blankFragment
                    true
                }
                R.id.tagFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, tagFragment).commit()
                    activeFragment = tagFragment
                    true
                }
                R.id.profileFragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, profileFragment).commit()
                    activeFragment = profileFragment
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.selectedItemId = R.id.homeFragment

        binding.addBtn.setOnClickListener {
            val myBottomSheet = BottomSheetDialogFragment()
            myBottomSheet.show(supportFragmentManager, myBottomSheet.tag)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
