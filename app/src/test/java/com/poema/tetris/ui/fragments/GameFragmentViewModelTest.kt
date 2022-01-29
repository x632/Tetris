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
    fun `test if isCollision returns true when x is out of range`() {
        GameScreen.emptyGameScreen()
        player.position.x = -3
        val result = viewModel.isCollision(player)
        assertTrue(result)
    }

    @Test
    fun `test if isCollision returns true when y is out of range`() {
        GameScreen.emptyGameScreen()
        player.position.y = 21
        val result = viewModel.isCollision(player)
        assertTrue(result)
    }

    @Test
    fun `test if isCollision returns true when running into object`() {
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
    fun `test if insert block inserts block on screen at correct coordinate`() {
        player.position.x = 4
        player.position.y = 0
        GameScreen.emptyGameScreen()
        viewModel.insertBlock(player)
        val result = GameScreen.arr.contentDeepEquals(
            arrayOf(
                arrayOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
                arrayOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
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