package com.ledscreen.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.floor

const val GRID_WIDTH = 32
const val GRID_HEIGHT = 18

class LEDCanvas(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val gridPaint = Paint().apply {
        color = Color.parseColor("#333333")
        strokeWidth = 1f
    }

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#1a1a1a")
    }

    private val pixelPaint = Paint().apply {
        isAntiAlias = false
    }

    var pixels: Map<String, String> = emptyMap()
        set(value) {
            field = value
            invalidate()
        }

    var currentTool = "pen"
    var currentColor = "#FF0000"
    var onPixelChanged: ((x: Int, y: Int, color: String?) -> Unit)? = null

    private var pixelSize = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        pixelSize = width / GRID_WIDTH.toFloat()

        // Fondo
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Grid
        for (i in 0..GRID_WIDTH) {
            val x = i * pixelSize
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
        }

        for (i in 0..GRID_HEIGHT) {
            val y = i * pixelSize
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
        }

        // Píxeles
        pixels.forEach { (key, color) ->
            val (x, y) = key.split(",")
            pixelPaint.color = Color.parseColor(color)
            val left = x.toInt() * pixelSize + 1
            val top = y.toInt() * pixelSize + 1
            val right = left + pixelSize - 2
            val bottom = top + pixelSize - 2
            canvas.drawRect(left, top, right, bottom, pixelPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = floor((event.x / pixelSize)).toInt()
        val y = floor((event.y / pixelSize)).toInt()

        if (x in 0 until GRID_WIDTH && y in 0 until GRID_HEIGHT) {
            when (event.action) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    when (currentTool) {
                        "pen" -> onPixelChanged?.invoke(x, y, currentColor)
                        "eraser" -> onPixelChanged?.invoke(x, y, null)
                    }
                    return true
                }
            }
        }
        return false
    }
}
