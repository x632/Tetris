package com.poema.tetris.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.poema.tetris.*
import com.poema.tetris.R.id.goDown
import com.poema.tetris.ui.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class GameFragment : Fragment() {

    private var introJob: Job? = null
    private lateinit var gameView: DynamicView
    private var currentBlock: Array<Array<Int>> = arrayOf()
    private var position = Position(5, 0)
    private var newRound = true
    private var job: Job? = null
    private var score = 0
    private lateinit var scoreTV: TextView
    lateinit var mp: MediaPlayer
    lateinit var tetrisSound: MediaPlayer
    private var gameOn = false
    private lateinit var startDownBtn: Button
    private var roundNumber = 0
    private var interval = 1000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tetrisSound = MediaPlayer.create(activity, R.raw.tetris)
        mp = MediaPlayer.create(activity, R.raw.pling)
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        val w =
            displayMetrics.widthPixels
        val h =
            displayMetrics.heightPixels
        gameView = DynamicView(activity, w, h)

        scoreTV = requireActivity().findViewById(R.id.score)
        val goRight: View = requireActivity().findViewById(R.id.goRight)
        val goLeft: View = requireActivity().findViewById(R.id.goLeft)
        val rotateLeft: View = requireActivity().findViewById(R.id.rotateLeft)
        val rotateRight: View = requireActivity().findViewById(R.id.rotateRight)
        startDownBtn = requireActivity().findViewById(goDown)

        startDownBtn.text = getString(R.string.start)
        introBoard()

        startDownBtn.setOnClickListener {
            startDownBtn.text = getString(R.string.down)
            startDownBtn.setTextColor(Color.WHITE)
            if (gameOn) movePlayerDown()
            else {
                //start
                introJob?.let { introJob!!.cancel() }
                gameOn = true
                GameScreen.emptyGameBoard()
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
            removeFullRowsAndDoScoreCount()
            val code = "ILJOZST".random()
            currentBlock = GameScreen.createBlock(code)
            roundNumber++
            if (roundNumber == INCREASE_SPEED_INTERVAL) {
                increaseSpeed()
            }
        }

        newRound = false
        mainFunction()
        pause()
    }

    private fun increaseSpeed() {
        if (interval >50) interval -= 50L
        roundNumber = 0
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
        GameScreen.emptyGameBoard()
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

    private fun removeFullRowsAndDoScoreCount() {
        var amountOfRows = 0
        outer@ while (true) {
            for (y in GameScreen.arr.lastIndex downTo 0) {
                var sum = 0
                for (value in GameScreen.arr[y]) {
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
            playTetrisSound()
        }
        updateScore(amountOfRows)
    }

    private fun updateScore(rows: Int) {
        score += (rows * 10) * (2 * rows)
        val text = "SCORE: $score"
        scoreTV.text = text
    }

    private fun playTetrisSound() {
        CoroutineScope(Main).launch {
            tetrisSound.start()
            for (index in 0..3) {
                scoreTV.text = ""

                delay(200)
                scoreTV.text = getString(R.string.tetris)
                delay(300)
                scoreTV.text = ""
            }
        }
    }

    private fun pause() {
        job?.let { job!!.cancel() }
        job = CoroutineScope(Main).launch {
            delay(interval)
            pickBlock()
        }
    }

    private fun insertBlock() {
        currentBlock.forEachIndexed { rowIndex, _ ->
            currentBlock[rowIndex].forEachIndexed { columnIndex, value ->
                if (value != 0) {
                    GameScreen.arr[rowIndex + position.y][columnIndex + position.x] = value
                }
            }
        }
        gameView.invalidate()
    }

    private fun removeBlock() {
        for (rowIndex in 0..currentBlock.lastIndex) {
            for (columnIndex in 0..currentBlock.lastIndex) {
                if (currentBlock[rowIndex][columnIndex] != 0) {
                    GameScreen.arr[rowIndex + position.y][columnIndex + position.x] = 0
                }
            }
        }
        gameView.invalidate()
    }

    private fun performRotation(dir: Int) {
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
                for (indexY in 0..4) {
                    for (indexX in (0 + pos)..(11 + pos)) {
                        GameScreen.arr[indexY+7][indexX - pos] = GameScreen.introBlock[indexY][indexX]
                    }
                    if (pos > 33) {
                        pos = 0
                    }
                }
                delay(100)
                gameView.invalidate()
                pos++
            }
        }
    }
}