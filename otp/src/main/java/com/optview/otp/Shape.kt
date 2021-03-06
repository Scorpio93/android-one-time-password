package com.optview.otp

import android.graphics.Canvas
import android.graphics.Paint


interface Shape {
    fun drawHolder(
        startX: Float,
        startY: Float,
        stopX : Float,
        stopY : Float,
        padding: Float,
        corners : Float = 0F,
        paint: Paint,
        canvas: Canvas?
    )
}