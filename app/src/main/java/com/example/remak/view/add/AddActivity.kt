package com.example.remak.view.add

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AddActivityBinding

class AddActivity : AppCompatActivity() {
    private val viewModel: AddViewModel by viewModels { AddViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    private var _binding: AddActivityBinding? = null
    private lateinit var navController: NavController

    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((application as App).dataStore)
        _binding = AddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

}