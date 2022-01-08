package com.poema.tetris.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import com.poema.tetris.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.Toast

import android.view.MotionEvent

import android.view.View.OnTouchListener





class GameFragment : Fragment() {


    private lateinit var gameView: DynamicView
    private var currentBlock: Array<Array<Int>> = arrayOf<Array<Int>>()
    private var position = Position(5, 0)
    private var newRound = true
    private var offset = 0



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        val w =
            displayMetrics.widthPixels - (displayMetrics.widthPixels * (PERCENTAGE_OF_BOARD_WIDTH))
        val h =
            displayMetrics.heightPixels - (displayMetrics.heightPixels * (PERCENTAGE_OF_BOARD_HEIGHT))
        gameView = DynamicView(activity, w.toInt(), h.toInt())

        gameView.setOnTouchListener { _, event ->

            val touchX = event.x
            val touchY = event.y

            if (event.action == MotionEvent.ACTION_DOWN) {
                touch(touchX, touchY)
            }
            true
        }
        return gameView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickBlock()
    }

    private fun pickBlock() {

        if (newRound) {
            checkForAndRemoveFullRows()
            val code = BLOCK_CODES.random()
            currentBlock = createBlock(code)
        }
        newRound = false
        checkCollisionBelow()
        pause()
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
        CoroutineScope(Dispatchers.Main).launch {
            delay(INTERVAL)
            removeBlock(position, currentBlock)
            pickBlock()
        }
    }

    private fun insertBlock(block: Array<Array<Int>>, position: Position) {
        block.forEachIndexed { rowIndex, _ ->
            block[rowIndex].forEachIndexed { columnIndex, value ->
                if (value != 0) {
                    GameBoard.arr[rowIndex + position.y][columnIndex + position.x] = value
                }
            }
        }
        gameView.invalidate()
        //checkForFullRows()
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
        val turnedBlock = Array(n) { Array<Int>(n) { 0 } }
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
                //kolla om den är på nederrsta raden
                if (currentBlock[y][x] != 0 && position.y + y > 18) {
                    newRound = true
                    insertBlock(currentBlock, position)
                    position.y = 0
                    return
                }
                //kolla om positionen under har innehåll(andra byggstenar)
                if (currentBlock[y][x] != 0) {
                    if (position.x + x > 11) {
                        position.x--
                    }
                    if (position.x + x < 0) {
                        position.x++
                    }
                    if (GameBoard.arr[y + (position.y + 1)][position.x + x] != 0) {
                        newRound = true
                        insertBlock(currentBlock, position)
                        position.y = 0
                        return
                    }
                }
            }
        }
        insertBlock(currentBlock, position)
        position.y++
    }

    private fun checkCollisionToTheSides(): Boolean {
        val xW = currentBlock[0].lastIndex
        val yH = currentBlock.lastIndex

        for (x in 0..xW) {
            for (y in 0..yH) {
                if (currentBlock[y][x] != 0) {
                    if ( position.x + x < 1 || position.x + x > 10){
                        return true
                    }
                    /*  if (position.x + x < 1 || position.x + x > 10) {
                          return true
                      }*/
                }
            }
        }
        return false
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

/*   override fun onTouchEvent(event: MotionEvent): Boolean {
       val touchX = event.x
       val touchY = event.y

       when (event.action) {
           MotionEvent.ACTION_DOWN -> touch(touchX, touchY)
           // MotionEvent.ACTION_MOVE -> touchMove()
           //MotionEvent.ACTION_UP -> touchUp()
       }
       return true
   }
*/
   private fun touch(touchX: Float, touchY: Float) {


       if (touchX < 524 && touchY > 1235) {
           if (position.x > 5 || !checkCollisionToTheSides()) {
               removeBlock(position, currentBlock)
               position.x--


           }
       }

       if (touchX > 524 && touchY > 1235) {
           if (position.x < 4 || !checkCollisionToTheSides()) {
               removeBlock(position, currentBlock)
               position.x++


           }

       }
       if (touchY < 1235) {
           removeBlock(position, currentBlock)
           rotateBlock()
       }

   }

}