package com.optview.otp.background

import android.graphics.Canvas
import android.graphics.Paint
import com.optview.otp.Shape


class BottomLineBackground : Shape {
    override fun drawHolder(
        startX: Float,
        startY: Float,
        stopX: Float,
        stopY: Float,
        padding: Float,
        corners: Float,
        paint: Paint,
        canvas: Canvas?
    ) {
        paint.strokeCap = Paint.Cap.ROUND
        canvas?.drawLine(startX + padding, stopY - padding, stopX - padding, stopY - padding, paint)
    }
}