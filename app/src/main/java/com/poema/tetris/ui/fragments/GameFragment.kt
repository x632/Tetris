package com.poema.tetris.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.poema.tetris.*
import com.poema.tetris.R.id.goDown
import com.poema.tetris.ui.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import com.poema.tetris.Game

const val INCREASE_SPEED_INTERVAL = 15

class GameFragment : Fragment() {

    private val viewModel: GameFragmentViewModel by viewModels()

    private lateinit var gameView: DynamicView
    private lateinit var scoreTV: TextView
    private lateinit var rowSound: MediaPlayer
    private lateinit var tetrisSound: MediaPlayer
    private lateinit var startDownBtn: Button
    private lateinit var highScoreTV: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tetrisSound = MediaPlayer.create(activity, R.raw.tetris)
        rowSound = MediaPlayer.create(activity, R.raw.pling)
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
        highScoreTV = requireActivity().findViewById(R.id.highScore)

        startDownBtn = requireActivity().findViewById(goDown)
        startDownBtn.text = getString(R.string.start)
        viewModel.showIntroText()

        val sharedPref = requireActivity().getPreferences(AppCompatActivity.MODE_PRIVATE)
        val hsC = sharedPref!!.getInt("HighestScore", 0)
        val text = "HIGHSCORE: $hsC"
        highScoreTV.text = text
        println("!!! highest score: $hsC")


        startDownBtn.setOnClickListener {
            startDownBtn.text = getString(R.string.down)
            startDownBtn.setTextColor(Color.WHITE)
            if (Game.gameOn) viewModel.moveBlockDown()
            else {
                viewModel.onStart()
            }
        }
        goLeft.setOnClickListener {
            viewModel.moveBlockToTheSides(-1)
        }
        goRight.setOnClickListener {
            viewModel.moveBlockToTheSides(1)
        }
        rotateRight.setOnClickListener {
            viewModel.removeBlock()
            viewModel.performRotation(-1)
        }
        rotateLeft.setOnClickListener {
            viewModel.removeBlock()
            viewModel.performRotation(1)
        }

        observeInstructionsFromViewModel()
        return gameView
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


    private fun observeInstructionsFromViewModel() {
        viewModel.uiInstruction.observe(viewLifecycleOwner) { instruction ->
            when (instruction) {
                is GameFragmentViewModel.UiInstruction.MakeTetrisSound -> CoroutineScope(Main).launch {
                    tetrisSound.start()
                    for (index in 0..3) {
                        scoreTV.text = ""
                        delay(200)
                        scoreTV.text = getString(R.string.tetris)
                        delay(300)
                        scoreTV.text = ""
                        showScore(instruction.score)
                    }
                }
                is GameFragmentViewModel.UiInstruction.MakeRowSound -> CoroutineScope(Main).launch {
                    rowSound.start()
                }
                is GameFragmentViewModel.UiInstruction.Restart -> {

                        val sharedPref = requireActivity().getPreferences(AppCompatActivity.MODE_PRIVATE)
                        val hs = sharedPref!!.getInt("HighestScore", 0)
                        println("!!! har poängen kommit med? ..:${instruction.score}")
                        if (hs < instruction.score) {
                            val editor = sharedPref.edit()
                            editor!!.putInt("HighestScore", instruction.score)
                            editor.commit()
                        }
                        restart()
                }

                is GameFragmentViewModel.UiInstruction.RefreshScreen -> view?.invalidate()

                is GameFragmentViewModel.UiInstruction.ShowScore -> {
                    showScore(instruction.score)
                }
            }
        }
    }

    private fun showScore(score: Int) {
        val text = "SCORE: $score"
        scoreTV.text = text
    }
}