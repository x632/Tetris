package com.poema.tetris

import android.content.Context
import android.graphics.*
import android.graphics.Color.*
import android.view.View
import java.util.*


class DynamicView(context: Context?, w: Int, h: Int) :
    View(context) {

    private var bitmap: Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    private var frameDrawer: Canvas = Canvas(bitmap)
    private var bounds: Rect = Rect(0, 0, w, h)
    private var paint: Paint = Paint()
    //extraCanvas.drawColor(backgroundColor)

    override fun onDraw(canvas: Canvas) {
        val h = height - (height * (PERCENTAGE_OF_BOARD_HEIGHT))
        val w = width - (width * (PERCENTAGE_OF_BOARD_WIDTH))

        val blockWidth = (w / 12).toFloat()
        val blockHeight = (h / 20).toFloat()

        for (y in 0..19) {
            for (x in 0..11) {
                if (GameBoard.arr[y][x] != 0) {
                    paint.color = when (GameBoard.arr[y][x]) {
                        1 -> BLACK
                        2 -> RED
                        3 -> GREEN
                        4 -> BLUE
                        5 -> YELLOW
                        6 -> MAGENTA
                        7 -> GRAY
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