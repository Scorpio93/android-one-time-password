package com.optview.otp.background

import android.graphics.Canvas
import android.graphics.Paint
import com.optview.otp.Shape



class OutlinedRectangleBackground : Shape {
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
        paint.style = Paint.Style.STROKE
        canvas?.drawRoundRect(
            startX + padding,
            startY + padding,
            stopX - padding,
            stopY - padding,
            corners,
            corners,
            paint
        )
    }
}