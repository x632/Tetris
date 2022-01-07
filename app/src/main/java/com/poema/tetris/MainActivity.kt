package com.poema.tetris



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main


const val PERCENTAGE_OF_BOARD_HEIGHT = 0
const val PERCENTAGE_OF_BOARD_WIDTH = 0
const val BLOCK_CODES = "TJLOSZI"
const val INTERVAL = 300L

class MainActivity : AppCompatActivity() {

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
        setContentView(gameView)
        pickBlock()
    }


    private fun pickBlock() {

        if (newRound) {
            val code = BLOCK_CODES.random()
            currentBlock = createBlock(code)
        }
        newRound = false
        checkCollisionBelow()
        pause()

    }

    fun setOffsets() {
        //GameBoard.printBlock(currentBlock)
        var sum = 0
        var rowEmpty = 0
        var yRow = 0
        for ((indexY, valueY) in currentBlock.withIndex()) {
            for (value in valueY) {
                sum += value
                println("!!! $value")

            }
            if (sum == 0) {
               // println("!!! row that's empty: $indexY")
                rowEmpty++
                sum = 0
               // println("!!! Rows empty to the left in this block: $rowEmpty")
            }
            sum=0
        }
    }

    private fun pause() {
        CoroutineScope(Main).launch {
            delay(INTERVAL)
            removeBlock(position,currentBlock)
            pickBlock()
        }

    }

    private fun insertBlock(block:Array<Array<Int>>,position:Position) {
        block.forEachIndexed { rowIndex, _ ->
            block[rowIndex].forEachIndexed { columnIndex, value ->
                if (value != 0) {
                    GameBoard.arr[rowIndex + position.y][columnIndex + position.x] = value
                }
            }
        }
        gameView.invalidate()

    }

    private fun removeBlock(position: Position, block: Array<Array<Int>>) {
        currentBlock.forEachIndexed { rowIndex, _ ->
            currentBlock[rowIndex].forEachIndexed { columnIndex, value ->
                if (value != 0) {
                    if (position.y > 0) {
                        GameBoard.arr[rowIndex + position.y - 1][columnIndex + position.x] = 0
                    }

                }
            }
        }
    }

    private fun rotateBlock() {
        val n = currentBlock.size
        val turnedBlock = Array(n) { Array<Int>(n){0} }
        for (i in 0 until n) {
            for (j in 0 until n) {
                turnedBlock[i][j] = currentBlock[n - 1 - j][i]
            }
        }
        currentBlock = turnedBlock
    }

    private fun checkCollisionBelow() {
        //setOffsets()
        val xW = currentBlock[0].lastIndex
        val yH = currentBlock.lastIndex

        for (x in 0..xW) {
            for (y in currentBlock.lastIndex downTo 0) {

                if (position.y + y - offset == 19) {
                    newRound = true
                    insertBlock(currentBlock,position)
                    position.y = 0
                    return
                }
                if (currentBlock[y][x] != 0 && GameBoard.arr[y + (position.y + 1)][x + position.x] != 0) {
                    newRound = true
                    insertBlock(currentBlock,position)
                    position.y = 0
                    return
                }
            }
        }
        insertBlock(currentBlock,position)
        position.y++
    }


    private fun createBlock(type: Char): Array<Array<Int>> {
        return when (type) {
            'I' -> {
                offset = 0
                return arrayOf(
                    arrayOf(0, 1, 0, 0),
                    arrayOf(0, 1, 0, 0),
                    arrayOf(0, 1, 0, 0),
                    arrayOf(0, 1, 0, 0),
                )
            }
            'L' -> {
                offset = 0
                return arrayOf(
                    arrayOf(0, 2, 0),
                    arrayOf(0, 2, 0),
                    arrayOf(0, 2, 2),
                )
            }
            'J' -> {
                offset = 0
                return arrayOf(
                    arrayOf(0, 3, 0),
                    arrayOf(0, 3, 0),
                    arrayOf(3, 3, 0),
                )
            }
            'O' -> {
                offset = 0
                return arrayOf(
                    arrayOf(4, 4),
                    arrayOf(4, 4)
                )
            }
            'Z' -> {
                offset = 1
                return arrayOf(
                    arrayOf(5, 5, 0),
                    arrayOf(0, 5, 5),
                    arrayOf(0, 0, 0),
                )
            }
            'S' -> {
                offset = 1
                return arrayOf(
                    arrayOf(0, 6, 6),
                    arrayOf(6, 6, 0),
                    arrayOf(0, 0, 0),
                )
            }
            'T' -> {
                offset = 1
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touch(touchX, touchY)
            // MotionEvent.ACTION_MOVE -> touchMove()
            //MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    fun touch(touchX: Float, touchY: Float) {

        if (touchX < 524 && position.x >= 1 && touchY > 1235) {
            removeBlock(position,currentBlock)
            position.x--
        }
        else if (touchX > 524 && position.x <= 8 && touchY > 1235) {
            removeBlock(position,currentBlock)
            position.x++
        }
        else if (touchY < 1235){
            removeBlock(position,currentBlock)
            rotateBlock()
        }
    }
}

