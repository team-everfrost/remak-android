package com.example.remak.view.main.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.databinding.SettingPageActivityBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding : SettingPageActivityBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SettingPageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}