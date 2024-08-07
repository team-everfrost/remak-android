package com.everfrost.remak.view.add

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.everfrost.remak.R
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.AddActivityBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddActivity : AppCompatActivity() {
    private val viewModel: AddViewModel by viewModels()
    lateinit var tokenRepository: TokenRepository
    private var _binding: AddActivityBinding? = null
    private lateinit var navController: NavController

    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = AddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.findNavController()

    }

}