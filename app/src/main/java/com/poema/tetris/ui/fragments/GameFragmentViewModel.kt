package com.poema.tetris.ui.fragments


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poema.tetris.GameScreen
import com.poema.tetris.Position
import com.poema.tetris.ui.INCREASE_SPEED_INTERVAL
import kotlinx.coroutines.*



const val MAKE_TETRIS_SOUND = 1
const val MAKE_ROW_SOUND = 2
const val RESTART = 3
const val REFRESH_SCREEN = 4

class GameFragmentViewModel : ViewModel() {

    private var introJob: Job? = null
    private var position = Position(5, 0)
    private var newRound = true
    private var job: Job? = null
    private var currentBlock: Array<Array<Int>> = arrayOf()
    var score = 0
    var gameOn = false
    private var interval = 1000L
    private var time = 0L
    private var lapTime = 0L


    private val _event: MutableLiveData<Int> = MutableLiveData<Int>()
    val event: LiveData<Int> = _event

    private val _scoringRows: MutableLiveData<Int> = MutableLiveData<Int>()
    val scoringRows: LiveData<Int> = _scoringRows

    fun onStart(){
        introJob?.let { introJob!!.cancel() }
        gameOn = true
        time = System.currentTimeMillis()
        lapTime = System.currentTimeMillis() + INCREASE_SPEED_INTERVAL*1000
        GameScreen.emptyGameBoard()
        pickBlock()
    }

    private fun pickBlock() {
        if (newRound) {
            removeFullRowsAndDoScoreCount()
            val code = "ILJOZST".random()
            currentBlock = GameScreen.createBlock(code)
        }

        newRound = false
        mainFunction()
        pause()
    }

    private fun increaseSpeed() {
        println("!!! INTERVAL HAS SHORTENED TO $interval!")
        if (interval > 25) interval -= 25L
    }

    private fun mainFunction() {
        if (System.currentTimeMillis() > lapTime) {
            increaseSpeed()
            lapTime = System.currentTimeMillis() + (INCREASE_SPEED_INTERVAL * 1000)
        }
        if (position.y == 0 && isCollision()) {
            job?.cancel()
            onEnd()
        }
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

    private fun removeFullRowsAndDoScoreCount() {
        var amountOfRows = 0
        outer@ while (true) {
            for (y in GameScreen.arr.lastIndex downTo 0) {
                var sum = 0
                for (value in GameScreen.arr[y]) {
                    if (value != 0) sum++
                }
                if (sum == 12) {
                    _event.value = MAKE_ROW_SOUND
                    amountOfRows++
                    val new2dArray = Array(20) { Array<Int>(12) { 0 } }
                    for (ind in 1..y) {
                        new2dArray[ind] = GameScreen.arr[ind - 1]
                    }
                    val newFirstLine = Array<Int>(12) { 0 }
                    new2dArray[0] = newFirstLine
                    for (ind in y until new2dArray.lastIndex) {
                        new2dArray[ind + 1] = GameScreen.arr[ind + 1]
                    }
                    GameScreen.arr = new2dArray
                    continue@outer
                }
            }
            break
        }
        if (amountOfRows == 4) {
            _event.value = MAKE_TETRIS_SOUND
        }

        _scoringRows.value = amountOfRows//updateScore(amountOfRows)
    }

    private fun pause() {
        job?.let { job!!.cancel() }
        job = viewModelScope.launch {
            delay(interval)
            pickBlock()
        }
    }

    fun insertBlock() {
        currentBlock.forEachIndexed { rowIndex, _ ->
            currentBlock[rowIndex].forEachIndexed { columnIndex, value ->
                if (value != 0) {
                    GameScreen.arr[rowIndex + position.y][columnIndex + position.x] = value
                }
            }
        }
        _event.value = REFRESH_SCREEN
    }

    fun removeBlock() {
        for (rowIndex in 0..currentBlock.lastIndex) {
            for (columnIndex in 0..currentBlock.lastIndex) {
                if (currentBlock[rowIndex][columnIndex] != 0) {
                    GameScreen.arr[rowIndex + position.y][columnIndex + position.x] = 0
                }
            }
        }
        _event.value = REFRESH_SCREEN
    }

    fun performRotation(dir: Int) {
        val pos = position.x
        var offset = 1
        rotateBlock(dir)
        while (isCollision()) {
            position.x += offset
            offset = -(offset + if (offset > 0) 1 else -1)
            if (offset > currentBlock[0].size) {
                rotateBlock(-dir)
                position.x = pos
                return
            }
        }
        insertBlock()
    }

    private fun rotateBlock(dir: Int) {
        val n = currentBlock.size
        val turnedBlock = Array(n) { Array<Int>(n) { 0 } }
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (dir < 0) {
                    turnedBlock[i][j] = currentBlock[n - 1 - j][i]
                } else {
                    turnedBlock[i][j] = currentBlock[j][n - i - 1]
                }
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
                            if (GameScreen.arr[y + position.y][x + position.x] != 0) {
                                return true
                            }
                        } else {
                            return true
                        }
                    } else {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun movePlayerToTheSides(dir: Int) {
        removeBlock()
        position.x += dir
        if (!isCollision()) {
            insertBlock()
        } else {
            position.x -= dir
            insertBlock()
        }
    }

    fun movePlayerDown() {
        removeBlock()
        position.y++
        if (!isCollision()) {
            insertBlock()
        } else {
            position.y--
            insertBlock()
        }
    }

    fun introBoard() {
        var pos = 0
        introJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                for (indexY in 0..4) {
                    for (indexX in (0 + pos)..(11 + pos)) {
                        GameScreen.arr[indexY + 7][indexX - pos] =
                            GameScreen.introBlock[indexY][indexX]
                    }
                    if (pos > 33) {
                        pos = 0
                    }
                }
                delay(100)
                _event.value = REFRESH_SCREEN
                pos++
            }
        }
    }

    private fun onEnd() {
        GameScreen.emptyGameBoard()
        job?.cancel()
        _event.value= RESTART
    }

}