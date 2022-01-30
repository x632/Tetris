package com.poema.tetris.ui.fragments


import com.poema.tetris.GameScreen
import com.poema.tetris.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameFragmentViewModelTest {

    private lateinit var viewModel: GameFragmentViewModel
    private lateinit var player: Player
    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
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
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown(){
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
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

    @Test
    fun `test if there is a levitating block of size one`() {
        GameScreen.arr = arrayOf(
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
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(2, 2, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(3, 3, 3, 3, 0, 4, 4, 4, 0, 0, 0, 0),
        )
        val result = viewModel.checkIfLevitatingBlock()
        assertTrue(result)
    }

    @Test
    fun `test if levitating block has fallen`()   {

        var result = false
        GameScreen.arr = arrayOf(
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
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(2, 2, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(3, 3, 3, 3, 0, 4, 4, 4, 0, 0, 0, 0),
        )
        viewModel.checkIfLevitatingBlock()

        if (GameScreen.arr[18][4]==0 && GameScreen.arr[19][4]==1
        ) {
            result = true

        }
        assertTrue(result)
    }

    @Test
    fun `check if levitating blocks have fallen all the way`() = runBlocking{
        var result = false
        GameScreen.arr = arrayOf(
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1),
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
            arrayOf(0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        )
        viewModel.checkAndMoveLevitatingBlocks()

        if (GameScreen.arr[18][0]==1 && GameScreen.arr[16][2]==1 && GameScreen.arr[19][4]==1
            && GameScreen.arr[19][4]==1 && GameScreen.arr[19][6]==1 && GameScreen.arr[19][8]==1
            && GameScreen.arr[19][11]==1){
            result = true
        }
        assertTrue(result)
    }

}