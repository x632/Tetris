package com.poema.tetris.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.poema.tetris.databinding.ActivityMainBinding

const val INCREASE_SPEED_INTERVAL = 6
const val PERCENTAGE_OF_HEIGHT_TO_LEAVE_OUT = 0.0
const val PERCENTAGE_OF_WIDTH_TO_LEAVE_OUT = 0.00
const val BLOCK_CODES = "TJLOSZI"



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

