package com.optview.otp.textHolders

import android.graphics.Canvas
import android.graphics.Paint


class EmptyTextHolder : TextHolder {
    override fun drawHolder(
        startX: Float,
        startY: Float,
        stopX: Float,
        stopY: Float,
        shapeWidth: Float,
        paint: Paint,
        canvas: Canvas?
    ) {
        // don't draw any shapes
    }
}