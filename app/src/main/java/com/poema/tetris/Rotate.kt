package com.poema.tetris

class Rotate {


    fun rotate(passedIn: Array<IntArray>, n: Int): Array<IntArray> {
        val newarray = Array(n) { IntArray(n) }
        for (i in 0 until n) {
            for (j in 0 until n) {
                newarray[i][j] = passedIn[j][n - 1 - i]
            }
        }
        return newarray
    }

}