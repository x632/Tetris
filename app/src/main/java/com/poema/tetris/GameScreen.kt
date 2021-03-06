package com.poema.tetris


object GameScreen {
    var arr = Array(20) { Array(12) { 0 } }

    fun emptyGameScreen() {
        for (y in arr.indices) {
            for (x in arr[y].indices) {
                arr[y][x] = 0
            }
        }
    }

    fun createBlock(type: Char): Array<Array<Int>> {
        return when (type) {
            'I' -> {
                return arrayOf(
                    arrayOf(0, 0, 1, 0, 0),
                    arrayOf(0, 0, 1, 0, 0),
                    arrayOf(0, 0, 1, 0, 0),
                    arrayOf(0, 0, 1, 0, 0),
                    arrayOf(0, 0, 0, 0, 0)
                )
            }
            'L' -> {
                return arrayOf(
                    arrayOf(0, 2, 0),
                    arrayOf(0, 2, 0),
                    arrayOf(0, 2, 2),
                )
            }
            'J' -> {
                return arrayOf(
                    arrayOf(0, 3, 0),
                    arrayOf(0, 3, 0),
                    arrayOf(3, 3, 0),
                )
            }
            'O' -> {
                return arrayOf(
                    arrayOf(4, 4),
                    arrayOf(4, 4)
                )
            }
            'Z' -> {
                return arrayOf(
                    arrayOf(5, 5, 0),
                    arrayOf(0, 5, 5),
                    arrayOf(0, 0, 0),
                )
            }
            'S' -> {
                return arrayOf(
                    arrayOf(0, 0, 0),
                    arrayOf(0, 6, 6),
                    arrayOf(6, 6, 0),
                )
            }
            'T' -> {
                return arrayOf(
                    arrayOf(0, 7, 0),
                    arrayOf(7, 7, 7),
                    arrayOf(0, 0, 0),
                )
            }
            else -> {
                emptyArray()
            }
        }
    }

    var introText = arrayOf(
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0, 1, 2, 3, 0, 6, 5, 4, 0, 3, 4, 0, 0, 3, 0, 6, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 4, 0, 0, 0, 0, 3, 0, 0, 4, 0, 6, 0, 4, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 5, 1, 0, 0, 0, 2, 0, 0, 5, 3, 0, 0, 5, 0, 4, 3, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 6, 0, 0, 0, 0, 1, 0, 0, 6, 0, 4, 0, 6, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 2, 3, 4, 0, 0, 6, 0, 0, 1, 0, 5, 0, 1, 0, 6, 2, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        )
}