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
        updateGame()
    }

    private fun updateGame() {

        pickBlock()
        CoroutineScope(Main).launch {
            delay(300)
        }



    }

    private fun pickBlock() {
        if (newRound) {
            val code = BLOCK_CODES.random()
            currentBlock = createBlock(code)
        }

        newRound = false
        checkCollisionBelow()
        //insertBlock(position, currentBlock)

    }

    private fun insertBlock(position: Position, block: Array<Array<Int>>) {
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
        block.forEachIndexed { rowIndex, _ ->
            block[rowIndex].forEachIndexed { columnIndex, value ->
                if (value != 0) {
                    GameBoard.arr[rowIndex + position.y][columnIndex + position.x] = 0
                }
            }
        }
    }

    private fun checkCollisionBelow() {
        GameBoard.printArray()
        GameBoard.printBlock(currentBlock)

        val xW = currentBlock[0].lastIndex
        val yH = currentBlock.lastIndex
        println("!!! blockbredd! (X) = $xW")
        println("!!! blockhöjd! (Y) = $yH")

        for (x in 0..xW) {
            for (y in currentBlock.lastIndex downTo 0) {
                if (currentBlock[y][x] != 0 && GameBoard.arr[y + position.y + 1][x + position.x] != 0) {
                    newRound = true
                    insertBlock(position, currentBlock)
                    position.y = 0
                    return
                }
            }
        }
        newRound = true
        insertBlock(position, currentBlock)
        position.y++

    }
    /*    for (y in currentBlock.lastIndex downTo 0) {

            for ((x,value) in currentBlock[y].withIndex()) {

                if (currentBlock[y][x] != 0) {  //varje gång det finns något i blocket så går den in för att kolla nedanför
                    println("!!! INTE LIKA MED 0 I BLOCKET! X = ${x} Y = ${y}")
                    if (GameBoard.arr[y + position.y + 1][x+position.x] != 0){ //|| position.y + (y+1) == 19) {
                        println("!!! BRYTVILLKORET! X = ${x+position.x} Y = ${y + position.y + 1}")
                        position.y = 0
                        newRound = true
                        return
                    }
                }
            }
        }
        removeBlock(position, currentBlock)
        position.y++*/

/*  for(value in currentBlock[lastRowIndex]){
      //GameBoard.printArray()
      counterColIndex++
      if (value != 0){
          println("!!! lastrowIndex = $lastRowIndex")
          if (GameBoard.arr[lastRowIndex + position.y+offset+1][counterColIndex + position.x] != 0 ){
              position.y = 0
              newRound = true
              return
          }
      }
  }*/

/* currentBlock.forEachIndexed { rowIndex, _ ->
     currentBlock[rowIndex].forEachIndexed { columnIndex, value ->
         if (GameBoard.arr[rowIndex + position.y+offset][columnIndex + position.x] != 0){
             position.y = 0
             newRound = true
             return
         }
     }
 }*/
//THIS BELOW WORKS!
/*if (position.y + (currentBlock.size - 1) + offset == 19) {
    position.y = 0
    newRound = true
} else {
    removeBlock(position, currentBlock)
    position.y++
}*/


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
}

