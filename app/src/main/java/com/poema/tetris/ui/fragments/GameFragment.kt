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
import androidx.fragment.app.viewModels
import com.poema.tetris.*
import com.poema.tetris.R.id.goDown
import com.poema.tetris.ui.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main


const val INCREASE_SPEED_INTERVAL = 15

class GameFragment : Fragment() {

    private val viewModel: GameFragmentViewModel by viewModels()

    private lateinit var gameView: DynamicView
    private lateinit var scoreTV: TextView
    private lateinit var plingSound: MediaPlayer
    lateinit var tetrisSound: MediaPlayer
    private lateinit var startDownBtn: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tetrisSound = MediaPlayer.create(activity, R.raw.tetris)
        plingSound = MediaPlayer.create(activity, R.raw.pling)
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
        viewModel.introBoard()

        startDownBtn.setOnClickListener {
            startDownBtn.text = getString(R.string.down)
            startDownBtn.setTextColor(Color.WHITE)
            if (viewModel.gameOn) viewModel.movePlayerDown()
            else {
                viewModel.onStart()
            }
        }
        goLeft.setOnClickListener {
            viewModel.movePlayerToTheSides(-1)
        }
        goRight.setOnClickListener {
            viewModel.movePlayerToTheSides(1)
        }
        rotateRight.setOnClickListener {
            viewModel.removeBlock()
            viewModel.performRotation(-1)
        }
        rotateLeft.setOnClickListener {
            viewModel.removeBlock()
            viewModel.performRotation(1)
            viewModel.insertBlock()
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
        viewModel.uiInstruction.observe(viewLifecycleOwner) {
            when (it) {
                is GameFragmentViewModel.UiInstruction.MakeTetrisSound -> CoroutineScope(Main).launch {
                    tetrisSound.start()
                    for (index in 0..3) {
                        scoreTV.text = ""

                        delay(200)
                        scoreTV.text = getString(R.string.tetris)
                        delay(300)
                        scoreTV.text = ""
                    }
                }
                is GameFragmentViewModel.UiInstruction.MakeRowSound -> CoroutineScope(Main).launch {
                    plingSound.start()
                }
                is GameFragmentViewModel.UiInstruction.Restart -> restart()

                is GameFragmentViewModel.UiInstruction.RefreshScreen -> view?.invalidate()

                is GameFragmentViewModel.UiInstruction.Scoring -> {
                    val rows = it.row
                    viewModel.score += (rows * 10) * (2 * rows)
                    val text = "SCORE: ${viewModel.score}"
                    scoreTV.text = text

                }
            }
        }
    }
}