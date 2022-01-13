package com.poema.tetris.ui.fragments

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.poema.tetris.*
import com.poema.tetris.R.id.goDown
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class GameFragment : Fragment() {

    private var introJob: Job? = null
    private lateinit var gameView: DynamicView
    private var currentBlock: Array<Array<Int>> = arrayOf<Array<Int>>()
    private var position = Position(5, 0)
    private var newRound = true
    private var job: Job? = null
    private var score = 0
    private lateinit var scoreTV: TextView
    lateinit var mp: MediaPlayer
    lateinit var tetrisSound: MediaPlayer
    private var gameOn = false
    private lateinit var startDownBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tetrisSound = MediaPlayer.create(activity, R.raw.tetris)
        mp = MediaPlayer.create(activity, R.raw.pling)
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        val w =
            displayMetrics.widthPixels - (displayMetrics.widthPixels * (PERCENTAGE_OF_WIDTH_TO_LEAVE_OUT))
        val h =
            displayMetrics.heightPixels - (displayMetrics.heightPixels * (PERCENTAGE_OF_HEIGHT_TO_LEAVE_OUT))
        gameView = DynamicView(activity, w.toInt(), h.toInt())

        scoreTV = requireActivity().findViewById(R.id.score)
        val goRight: View = requireActivity().findViewById(R.id.goRight)
        val goLeft: View = requireActivity().findViewById(R.id.goLeft)
        val rotateLeft: View = requireActivity().findViewById(R.id.rotateLeft)
        val rotateRight: View = requireActivity().findViewById(R.id.rotateRight)
        startDownBtn = requireActivity().findViewById(goDown)

        startDownBtn.text = "START"
        introBoard()

        startDownBtn.setOnClickListener {
            startDownBtn.text = "DOWN"
            if (gameOn) movePlayerDown()
            else {
                //start
                introJob?.let { introJob!!.cancel() }
                gameOn = true
                GameBoard.emptyGameBoard()
                pickBlock()
            }
        }
        goLeft.setOnClickListener {
            movePlayerToTheSides(-1)
        }
        goRight.setOnClickListener {
            movePlayerToTheSides(1)
        }
        rotateRight.setOnClickListener {
            removeBlock()
            performRotation(-1)
        }
        rotateLeft.setOnClickListener {
            removeBlock()
            performRotation(1)
            insertBlock()
        }
        return gameView
    }

    private fun pickBlock() {

        if (newRound) {
            removeFullRows()
            val code = BLOCK_CODES.random()
            currentBlock = GameBoard.createBlock(code)
        }
        newRound = false
        mainFunction()
        pause()
    }

    private fun mainFunction() {

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

    private fun onEnd() {
        GameBoard.emptyGameBoard()
        restart()
    }

    private fun restart() {
        val context = requireContext()
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    private fun removeFullRows() {

        var amountOfRows = 0
        outer@ while (true) {
            for (y in GameBoard.arr.lastIndex downTo 0) {
                var sum = 0
                for (value in GameBoard.arr[y]) {
                    if (value != 0) sum++
                }
                if (sum == 12) {
                    CoroutineScope(Main).launch {
                        //make sound
                        mp.start()
                    }
                    amountOfRows++
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
        if (amountOfRows != 0) {
            if (amountOfRows == 4) {
                playTetrisSound(amountOfRows)
            }
            updateScore(amountOfRows)
        }
    }

    private fun updateScore(rows: Int) {

        score += (rows * 10) * (2 * rows)
        val text = "SCORE: ${score}"
        scoreTV.text = text
    }

    private fun playTetrisSound(rows: Int) {

        CoroutineScope(Main).launch {
            tetrisSound.start()
            for (index in 0..3) {
                scoreTV.text = ""
                delay(200)
                scoreTV.text = "TETRIS 320 POINTS!!"
                delay(300)
                scoreTV.text = ""
            }
        }
    }

    private fun pause() {

        job?.let { job!!.cancel() }
        job = CoroutineScope(Main).launch {
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

        for (rowIndex in 0..currentBlock.lastIndex) {
            for (columnIndex in 0..currentBlock.lastIndex) {
                if (currentBlock[rowIndex][columnIndex] != 0) {
                    GameBoard.arr[rowIndex + position.y][columnIndex + position.x] = 0
                }
            }
        }
        gameView.invalidate()
    }

    private fun performRotation(dir: Int) {

        val pos = position.x;
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
                            if (GameBoard.arr[y + position.y][x + position.x] != 0) {
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

    private fun movePlayerDown() {

        removeBlock()
        position.y++
        if (!isCollision()) {
            insertBlock()
        } else {
            position.y--
            insertBlock()
        }
    }

    private fun introBoard() {

        var pos = 0
        introJob = CoroutineScope(Main).launch {
            while (true) {
                for (indexY in 0..19) {
                    for (indexX in (0 + pos)..(11 + pos)) {
                        GameBoard.arr[indexY][indexX - pos] = GameBoard.introBlock[indexY][indexX]
                    }
                    delay(2)
                    gameView.invalidate()
                    if (pos > 33) {
                        pos = 0
                    }
                }
                pos++
            }
        }
    }
}