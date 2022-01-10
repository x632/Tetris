package com.poema.tetris.ui.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import com.poema.tetris.*


import kotlinx.coroutines.*

class GameFragment : Fragment() {

    private lateinit var gameView: DynamicView
    private var currentBlock: Array<Array<Int>> = arrayOf<Array<Int>>()
    private var position = Position(5, 0)
    private var newRound = true
    private var job: Job? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        val w =
            displayMetrics.widthPixels - (displayMetrics.widthPixels * (PERCENTAGE_OF_BOARD_WIDTH))
        val h =
            displayMetrics.heightPixels - (displayMetrics.heightPixels * (PERCENTAGE_OF_BOARD_HEIGHT))
        gameView = DynamicView(activity, w.toInt(), h.toInt())

        val goRight: View = requireActivity().findViewById(R.id.goRight)
        val goLeft: View = requireActivity().findViewById(R.id.goLeft)
        val rotateLeft: View = requireActivity().findViewById(R.id.rotateLeft)
        val rotateRight: View = requireActivity().findViewById(R.id.rotateRight)

        goLeft.setOnClickListener {
            movePlayerToTheSides(-1)
        }
        goRight.setOnClickListener{
            movePlayerToTheSides(1)
        }
        rotateRight.setOnClickListener {
            removeBlock()
            rotateBlock()
        }

        pickBlock()
        return gameView
    }


    private fun pickBlock() {
        if (newRound) {
            checkForAndRemoveFullRows()
            val code = BLOCK_CODES.random()
            currentBlock = createBlock(code)

        }
        newRound = false
        mainFunction()
        pause()
    }

    private fun mainFunction() {
        removeBlock()
        position.y++
        if (isCollision()) {
            position.y--
            insertBlock()
            position.y = 0
            position.x = 5
            newRound = true

            pickBlock()
        } else {
                insertBlock()
        }
    }

    private fun checkForAndRemoveFullRows() {
        outer@ while (true) {
            for (y in GameBoard.arr.lastIndex downTo 0) {
                var sum = 0
                for (value in GameBoard.arr[y]) {
                    if (value != 0) sum++
                }
                if (sum == 12) {
                    val new2dArray = Array(20) { Array<Int>(12) { 0 } }
                    for (ind in 1..y) {
                        new2dArray[ind] = GameBoard.arr[ind - 1]
                    }
                    val newFirstLine = Array<Int>(12) { 0 }
                    new2dArray[0] = newFirstLine
                    for (ind in y until new2dArray.lastIndex) {
                        new2dArray[ind + 1] = GameBoard.arr[ind + 1]
                    }
                    GameBoard.arr = new2dArray
                    continue@outer
                }
            }
            break
        }
    }

    private fun pause() {
        job?.let { job!!.cancel() }
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(INTERVAL)
            pickBlock()
        }
    }

    private fun insertBlock() {
        currentBlock.forEachIndexed { rowIndex, _ ->
            currentBlock[rowIndex].forEachIndexed { columnIndex, value ->
                if (value != 0) {
                    GameBoard.arr[rowIndex + position.y][columnIndex + position.x] = value
                }
            }
        }
        gameView.invalidate()
    }

    private fun removeBlock() {
        for( rowIndex in 0..currentBlock.lastIndex){
            for (columnIndex in 0..currentBlock.lastIndex){
                if (currentBlock[rowIndex][columnIndex] != 0) {
                    GameBoard.arr[rowIndex + position.y][columnIndex + position.x] = 0
                }
            }
        }
        gameView.invalidate()
    }

    private fun rotateBlock() {
        val n = currentBlock.size
        val turnedBlock = Array(n) { Array<Int>(n) { 0 } }
        for (i in 0 until n) {
            for (j in 0 until n) {
                turnedBlock[i][j] = currentBlock[n - 1 - j][i]
            }
        }
        currentBlock = turnedBlock
    }

    private fun isCollision(): Boolean {
        for (y in currentBlock.indices) {
            for (x in currentBlock[y].indices) {
                if (currentBlock[y][x] != 0) {
                    if ((position.y + y) in 0..19) {
                        if ((position.x + x) in 0..11) {
                            if (GameBoard.arr[y + position.y][x + position.x] != 0) {
                                return true
                            }
                        }
                        else{
                            return true
                        }
                    }
                    else{
                        return true
                    }
                }
            }
        }
        return false
    }



    private fun createBlock(type: Char): Array<Array<Int>> {
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

    private fun movePlayerToTheSides(dir: Int) {
        removeBlock()
        position.x += dir
        if (!isCollision()) {
            insertBlock()
        } else {
            position.x -= dir
            insertBlock()
        }
    }
}