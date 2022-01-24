package com.poema.tetris

import com.poema.tetris.Position

class Player {
    val position: Position = Position(5, 0)
    var currentBlock : Array<Array<Int>> = arrayOf()
    var score = 0
}