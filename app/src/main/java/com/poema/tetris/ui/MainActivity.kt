package com.poema.tetris.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.poema.tetris.databinding.ActivityMainBinding

const val INCREASE_SPEED_INTERVAL = 15

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

