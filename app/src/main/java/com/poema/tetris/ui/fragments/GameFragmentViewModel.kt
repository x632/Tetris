package com.poema.tetris.ui.fragments


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poema.tetris.Game
import com.poema.tetris.GameScreen
import com.poema.tetris.Player
import kotlinx.coroutines.*


class GameFragmentViewModel : ViewModel() {

    private var introJob: Job? = null
    private var job: Job? = null
    private val player = Player()


    sealed class UiInstruction {
        object RefreshScreen : UiInstruction()
        object MakeRowSound : UiInstruction()
        data class MakeTetrisSound(val score: Int) : UiInstruction()
        data class Restart(var score: Int) : UiInstruction()
        data class ShowScore(val score: Int) : UiInstruction()
    }

    private val _uiInstruction: MutableLiveData<UiInstruction> = MutableLiveData<UiInstruction>()
    val uiInstruction: MutableLiveData<UiInstruction> = _uiInstruction


    fun onStart() {
        introJob?.let { introJob!!.cancel() }
        Game.gameOn = true
        Game.time = System.currentTimeMillis()
        Game.lapTime = System.currentTimeMillis() + INCREASE_SPEED_INTERVAL * 1000
        GameScreen.emptyGameScreen()
        pickBlock()
    }

    private fun pickBlock() {  //main loop
        if (Game.newRound) {
            val rows = removeFullRowsAndRearrangeScreen()
            doScoring(rows)
            checkAndMoveLevitatingBlocks()
            val code = "ILJOZST".random()
            player.currentBlock = GameScreen.createBlock(code)
        }

        Game.newRound = false
        mainFunction()
        pause()
    }

    fun checkAndMoveLevitatingBlocks() {
        viewModelScope.launch {
            while (checkIfLevitatingBlock()) {
                uiInstruction.value = UiInstruction.RefreshScreen
                delay(200)
                checkIfLevitatingBlock()
            }
        }

    }


    private fun mainFunction() {   //main loop
        if (System.currentTimeMillis() > Game.lapTime) {
            increaseSpeed()
            Game.lapTime = System.currentTimeMillis() + (INCREASE_SPEED_INTERVAL * 1000)
        }
        if (player.position.y == 0 && isCollision(player)) {
            job?.cancel()
            Game.gameOn = false
        }
        removeBlock()
        player.position.y++
        if (isCollision(player)) {
            player.position.y--
            insertBlock(player)
            _uiInstruction.value = UiInstruction.RefreshScreen
            player.position.y = 0
            player.position.x = 5
            Game.newRound = true

            if (Game.gameOn) pickBlock() else onEnd()
        } else {
            insertBlock(player)
            _uiInstruction.value = UiInstruction.RefreshScreen
        }
    }

    private fun pause() {     //main loop
        job?.let { job!!.cancel() }
        job = viewModelScope.launch {
            delay(Game.interval)
            if (Game.gameOn) pickBlock() else onEnd()
        }
    }

    fun checkIfLevitatingBlock(): Boolean {
        for (y in GameScreen.arr.indices) {
            for (x in GameScreen.arr[y].indices) {
                for (number in 1..7) {
                    if (y in 1..18 && x in 1..10) {
                        if (GameScreen.arr[y][x] == number
                            && GameScreen.arr[y - 1][x] != number
                            && GameScreen.arr[y][x + 1] != number
                            && GameScreen.arr[y][x - 1] != number
                            && GameScreen.arr[y + 1][x] == 0
                        ) {
                            GameScreen.arr[y][x] = 0
                            GameScreen.arr[y + 1][x] = number
                            return true
                        }
                    } else {
                        if (y in 1..18) {
                            if (x == 0) {
                                if (GameScreen.arr[y][x] == number
                                    && GameScreen.arr[y - 1][x] != number
                                    && GameScreen.arr[y][x + 1] != number
                                    && GameScreen.arr[y + 1][x] == 0
                                ) {
                                    GameScreen.arr[y][x] = 0
                                    GameScreen.arr[y + 1][x] = number
                                    return true
                                }
                            } else {
                                if (GameScreen.arr[y][x] == number
                                    && GameScreen.arr[y - 1][x] != number
                                    && GameScreen.arr[y][x - 1] != number
                                    && GameScreen.arr[y + 1][x] == 0
                                ) {
                                    GameScreen.arr[y][x] = 0
                                    GameScreen.arr[y + 1][x] = number
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun increaseSpeed() {
        if (Game.interval > 25) Game.interval -= 25L
    }

    private fun removeFullRowsAndRearrangeScreen(): Int {
        var amountOfFullRows = 0
        outer@ while (true) {
            for (y in GameScreen.arr.lastIndex downTo 0) {
                var sum = 0
                for (value in GameScreen.arr[y]) {
                    if (value != 0) sum++
                }
                if (sum == 12) {
                    _uiInstruction.value = UiInstruction.MakeRowSound
                    amountOfFullRows++
                    val new2dArray = Array(20) { Array<Int>(12) { 0 } }
                    for (ind in 1..y) {
                        new2dArray[ind] = GameScreen.arr[ind - 1]
                    }
                    val newFirstLine = Array(12) { 0 }
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
        return amountOfFullRows
    }

    private fun doScoring(rows: Int) {
        if (rows == 4) {
            calculateScore(rows)
            _uiInstruction.value = UiInstruction.MakeTetrisSound(player.score)
        } else {
            calculateScore(rows)
            _uiInstruction.value = UiInstruction.ShowScore(player.score)
        }
    }

    private fun calculateScore(rows: Int) {
        player.score += (rows * 10) * (rows * 2)
    }

    fun insertBlock(player: Player) {
        player.currentBlock.forEachIndexed { rowIndex, _ ->
            player.currentBlock[rowIndex].forEachIndexed { columnIndex, value ->
                if (value != 0) {
                    GameScreen.arr[rowIndex + player.position.y][columnIndex + player.position.x] =
                        value
                }
            }
        }
    }

    fun removeBlock() {
        for (rowIndex in 0..player.currentBlock.lastIndex) {
            for (columnIndex in 0..player.currentBlock.lastIndex) {
                if (player.currentBlock[rowIndex][columnIndex] != 0) {
                    GameScreen.arr[rowIndex + player.position.y][columnIndex + player.position.x] =
                        0
                }
            }
        }
        _uiInstruction.value = UiInstruction.RefreshScreen
    }

    fun performRotation(dir: Int) {
        removeBlock()
        val pos = player.position.x
        var offset = 1
        rotateBlock(dir)
        while (isCollision(player)) {
            player.position.x += offset
            offset = -(offset + if (offset > 0) 1 else -1)
            if (offset > player.currentBlock[0].size) {
                rotateBlock(-dir)
                player.position.x = pos
                return
            }
        }
        insertBlock(player)
        _uiInstruction.value = UiInstruction.RefreshScreen
    }

    private fun rotateBlock(dir: Int) {
        val n = player.currentBlock.size
        val turnedBlock = Array(n) { Array(n) { 0 } }
        for (y in player.currentBlock.indices) {
            for (x in player.currentBlock.indices) {
                if (dir < 0) {
                    turnedBlock[y][x] = player.currentBlock[(n - 1 - x)][y]
                } else {
                    turnedBlock[y][x] = player.currentBlock[x][n - y - 1]
                }
            }
        }
        player.currentBlock = turnedBlock
    }

    fun isCollision(player: Player): Boolean {
        for (y in player.currentBlock.indices) {
            for (x in player.currentBlock[y].indices) {
                if (player.currentBlock[y][x] != 0) {
                    if ((player.position.y + y) in 0..19) {
                        if ((player.position.x + x) in 0..11) {
                            if (GameScreen.arr[y + player.position.y][x + player.position.x] != 0) {
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

    fun moveBlockToTheSides(dir: Int) {
        removeBlock()
        player.position.x += dir
        if (!isCollision(player)) {
            insertBlock(player)
            _uiInstruction.value = UiInstruction.RefreshScreen
        } else {
            player.position.x -= dir
            insertBlock(player)
            _uiInstruction.value = UiInstruction.RefreshScreen
        }
    }

    fun moveBlockDown() {
        removeBlock()
        player.position.y++
        if (!isCollision(player)) {
            insertBlock(player)
            _uiInstruction.value = UiInstruction.RefreshScreen
        } else {
            player.position.y--
            insertBlock(player)
            _uiInstruction.value = UiInstruction.RefreshScreen
        }
    }

    fun showIntroText() {
        var pos = 0
        introJob = viewModelScope.launch {
            while (true) {
                for (indexY in 0..4) {
                    for (indexX in (0 + pos)..(11 + pos)) {
                        GameScreen.arr[indexY + 7][indexX - pos] =
                            GameScreen.introText[indexY][indexX]
                    }
                    if (pos > 33) {
                        pos = 0
                    }
                }
                delay(100)
                _uiInstruction.value = UiInstruction.RefreshScreen
                pos++
            }
        }
    }

    private fun onEnd() {
        job?.cancel()
        _uiInstruction.value = UiInstruction.Restart(player.score)
    }

}