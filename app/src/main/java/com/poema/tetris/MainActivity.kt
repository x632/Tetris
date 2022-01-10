package com.poema.tetris


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.widget.Button
import com.poema.tetris.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main


const val PERCENTAGE_OF_BOARD_HEIGHT = 0.2
const val PERCENTAGE_OF_BOARD_WIDTH = 0.05
const val BLOCK_CODES = "TJLOSZI"
const val INTERVAL = 300L

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gameView: DynamicView
    private var currentBlock: Array<Array<Int>> = arrayOf<Array<Int>>()
    private var position = Position(5, 0)
    private var newRound = true
    private var offset = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        val w =
            displayMetrics.widthPixels - (displayMetrics.widthPixels * (PERCENTAGE_OF_BOARD_WIDTH))
        val h =
            displayMetrics.heightPixels - (displayMetrics.heightPixels * (PERCENTAGE_OF_BOARD_HEIGHT))
        gameView = DynamicView(this, w.toInt(), h.toInt())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}

