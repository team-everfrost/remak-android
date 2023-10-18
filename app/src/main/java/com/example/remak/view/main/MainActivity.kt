package com.example.remak.view.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.remak.App
import com.example.remak.BaseActivity
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.MainActivityBinding
import com.example.remak.view.collection.MainCollectionFragment
import com.example.remak.view.profile.MainProfileFragment
import com.example.remak.view.search.MainSearchFragment
import com.example.remak.view.tag.MainTagFragment

class MainActivity : BaseActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    private var _binding: MainActivityBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((application as App).dataStore)
        _binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.loginCheck()

        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView)
                    if (currentFragment is MainHomeFragment) {
                        if (currentFragment.isRecyclerViewInitialized()) {
                            currentFragment.scrollToTop()
                        }
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.mainFragmentContainerView, MainHomeFragment()).commit()
                    }
                    true
                }

                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFragmentContainerView, MainSearchFragment()).commit()
                    true
                }

                R.id.tagFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFragmentContainerView, MainTagFragment()).commit()
                    true
                }

                R.id.collectionFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFragmentContainerView, MainCollectionFragment()).commit()
                    true
                }

                R.id.profileFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFragmentContainerView, MainProfileFragment()).commit()
                    true
                }

                else -> false
            }
        }

        // 시작 시 홈 프래그먼트로 이동
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.homeFragment
        }

    }

    fun hideBottomNavi() {
        binding.bottomNavigation.visibility = View.GONE
    }

    fun showBottomNavi() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
