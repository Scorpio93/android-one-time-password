package com.optview.otp.symbolHolders

import android.graphics.Canvas
import android.graphics.Paint
import com.optview.otp.Shape


class Empty : Shape {
    override fun drawHolder(
        startX: Float,
        startY: Float,
        stopX: Float,
        stopY: Float,
        padding: Float,
        paint: Paint,
        canvas: Canvas?
    ) {
        // don't draw any shapes
    }
}