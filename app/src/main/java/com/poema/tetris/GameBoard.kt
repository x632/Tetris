package com.poema.tetris


object GameBoard {
    var arr = Array(20) { Array<Int>(12) { 0 } }

    fun printArray() {

        for (array in arr) {
            println("!!! ${array.contentToString()}")
        }
        println("!!!________________________________")
    }

    fun printBlock(block:Array<Array<Int>>) {

        for (array in block) {
            println("!!! ${array.contentToString()}")
        }
        println("!!!________________________________")
    }




}