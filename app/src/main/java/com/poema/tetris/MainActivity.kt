package com.poema.tetris


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.widget.Button
import com.poema.tetris.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent

import android.media.MediaPlayer
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.content.ComponentName

import android.content.pm.PackageManager





const val PERCENTAGE_OF_BOARD_HEIGHT = 0.2
const val PERCENTAGE_OF_BOARD_WIDTH = 0.05
const val BLOCK_CODES = "TJLOSZI"
const val INTERVAL = 300L


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }
}

