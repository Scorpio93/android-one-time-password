package com.optview.otp.symbolHolders

import android.graphics.Canvas
import android.graphics.Paint
import com.optview.otp.Shape


class Circle : Shape {
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
        canvas?.drawCircle(startX, startY, padding, paint)
    }
}