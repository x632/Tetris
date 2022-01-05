package com.poema.tetris


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main


const val PERCENTAGE_OF_BOARD_HEIGHT = 0
const val PERCENTAGE_OF_BOARD_WIDTH = 0
const val BLOCK_CODES = "TJLOSZI"

class MainActivity : AppCompatActivity() {

    private lateinit var gameView: DynamicView
    private var oldBlock: Array<Array<Int>> = arrayOf<Array<Int>>()
    private var position = Position(5, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        val w =
            displayMetrics.widthPixels - (displayMetrics.widthPixels * (PERCENTAGE_OF_BOARD_WIDTH))
        val h =
            displayMetrics.heightPixels - (displayMetrics.heightPixels * (PERCENTAGE_OF_BOARD_HEIGHT))
        gameView = DynamicView(this, w.toInt(), h.toInt())
        setContentView(gameView)

        pickBlock()
    }

    private fun pickBlock() {
        val code = BLOCK_CODES.random()
        val block = createBlock(code)
        oldBlock = block
        insertBlock(position, block)

    }

    private fun insertBlock(position: Position, block: Array<Array<Int>>) {
        block.forEachIndexed { rowIndex, _ ->
            block[rowIndex].forEachIndexed { columnIndex, value ->
                if (value != 0) {
                    GameBoard.arr[rowIndex + position.y][columnIndex + position.x] = value
                } else {
                    GameBoard.arr[rowIndex + position.y][columnIndex + position.x] = 0
                }
            }
        }
        gameView.invalidate()

        CoroutineScope(Main).launch {
            delay(150)
            removeBlock(position, block)
            println("!!! Position = ${position.y}")
            if (position.y == 16) {
                position.y = 0
            } else {
                position.y++
            }
            pickBlock()
        }

    }

    private fun removeBlock(position: Position, block: Array<Array<Int>>) {
        block.forEachIndexed { rowIndex, _ ->
            block[rowIndex].forEachIndexed { columnIndex, _ ->
                GameBoard.arr[rowIndex + position.y][columnIndex + position.x] = 0
            }
        }
    }

    fun createBlock(type: Char): Array<Array<Int>> {
        return when (type) {
            'I' -> {
                return arrayOf(
                    arrayOf(0, 1, 0, 0),
                    arrayOf(0, 1, 0, 0),
                    arrayOf(0, 1, 0, 0),
                    arrayOf(0, 1, 0, 0),
                )
            }
            'L' -> {
                return arrayOf(
                    arrayOf(0, 2, 0),
                    arrayOf(0, 2, 0),
                    arrayOf(0, 2, 2),
                )
            }
            'J' -> {
                return arrayOf(
                    arrayOf(0, 3, 0),
                    arrayOf(0, 3, 0),
                    arrayOf(3, 3, 0),
                )
            }
            'O' -> {
                return arrayOf(
                    arrayOf(4, 4),
                    arrayOf(4, 4)
                )
            }
            'Z' -> {
                return arrayOf(
                    arrayOf(5, 5, 0),
                    arrayOf(0, 5, 5),
                    arrayOf(0, 0, 0),
                )
            }
            'S' -> {
                return arrayOf(
                    arrayOf(0, 6, 6),
                    arrayOf(6, 6, 0),
                    arrayOf(0, 0, 0),
                )
            }
            'T' -> {
                return arrayOf(
                    arrayOf(0, 7, 0),
                    arrayOf(7, 7, 7),
                    arrayOf(0, 0, 0),
                )
            }
            else -> {
                emptyArray<Array<Int>>()
            }
        }
    }
}

