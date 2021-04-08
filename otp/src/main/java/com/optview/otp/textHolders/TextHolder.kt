package com.optview.otp.textHolders

import android.graphics.Canvas
import android.graphics.Paint


interface TextHolder {
    fun drawHolder(
        startX: Float,
        startY: Float,
        stopX : Float,
        stopY : Float,
        shapeWidth: Float,
        paint: Paint,
        canvas: Canvas?
    )
}