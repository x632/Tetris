package com.poema.tetris.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Color.*
import android.view.View
import com.poema.tetris.GameBoard




class DynamicView(context: Context?, w: Int, h: Int) :
    View(context) {

    private var bitmap: Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    private var frameDrawer: Canvas = Canvas(bitmap)
    private var bounds: Rect = Rect(24, 0, w-24, h)
    private var paint: Paint = Paint()


    override fun onDraw(canvas: Canvas) {
        val blockWidth = (width / 12).toFloat()
        val blockHeight = (height / 20).toFloat()

        for (y in 0..19) {
            for (x in 0..11) {
                if (GameBoard.arr[y][x] != 0) {
                    paint.color = when (GameBoard.arr[y][x]) {
                        1 -> rgb(255,51,51) //red
                        2 -> rgb(255,153,51) //orange
                        3 -> rgb(255,255,51) //yellow
                        4 -> rgb(153,255,51) //lightgreen
                        5 -> rgb(51,153,255)  //magenta
                        6 -> rgb(153,51,255) //purple
                        7 -> rgb(255,51,255) //pink
                        else -> {
                            0
                        }
                    }
                    frameDrawer.drawRect(
                        x * blockWidth,
                        y * blockHeight,
                        x * blockWidth + blockWidth,
                        y * blockHeight + blockHeight,
                        paint
                    )
                } else {
                    paint.color = argb(
                        255, 35,
                        37, 30
                    )
                    frameDrawer.drawRect(
                        x * blockWidth,
                        y * blockHeight,
                        x * blockWidth + blockWidth,
                        y * blockHeight + blockHeight,
                        paint
                    )
                }
            }
        }
        canvas.drawBitmap(bitmap, null, bounds, null)
    }
}