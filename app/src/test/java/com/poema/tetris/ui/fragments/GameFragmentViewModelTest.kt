package com.poema.tetris.ui.fragments


import com.poema.tetris.GameScreen
import com.poema.tetris.Player
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameFragmentViewModelTest {

    private lateinit var viewModel: GameFragmentViewModel
    private lateinit var player: Player

    @Before
    fun setUp() {
        viewModel = GameFragmentViewModel()
        player = Player()
        player.currentBlock = arrayOf(
            arrayOf(0, 0, 1, 0, 0),
            arrayOf(0, 0, 1, 0, 0),
            arrayOf(0, 0, 1, 0, 0),
            arrayOf(0, 0, 1, 0, 0),
            arrayOf(0, 0, 0, 0, 0)
        )
    }

    @Test
    fun test_if_isCollision_returns_true_when_x_is_out_of_range() {
        GameScreen.emptyGameScreen()
        player.position.x = -3
        val result = viewModel.isCollision(player)
        assertTrue(result)
    }

    @Test
    fun test_if_isCollision_returns_true_when_y_is_out_of_range() {
        GameScreen.emptyGameScreen()
        player.position.y = 21
        val result = viewModel.isCollision(player)
        assertTrue(result)
    }

    @Test
    fun test_if_isCollision_returns_true_when_running_into_object() {
        player.position.x = -2
        player.position.y = 1 //is trying to move one step down from 0 position
        GameScreen.emptyGameScreen()
        GameScreen.arr = arrayOf(
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        )
        val result = viewModel.isCollision(player)
        assertTrue(result)
    }

    @Test
    fun test_if_insert_block_inserts_block_on_screen_at_correct_coordinate() {
        player.position.x = -2
        player.position.y = 0
        GameScreen.emptyGameScreen()
        viewModel.insertBlock(player)
        println("!!!${GameScreen.arr.contentDeepToString()}")
        val result = GameScreen.arr.contentDeepEquals(
            arrayOf(
                arrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            )
        )
        assertTrue(result)
    }
}