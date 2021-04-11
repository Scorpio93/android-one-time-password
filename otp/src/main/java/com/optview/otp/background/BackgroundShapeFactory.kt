package com.optview.otp.background

import com.optview.otp.Shape
import com.optview.otp.symbolHolders.Empty


object BackgroundShapeFactory {
    fun getShape(backgroundShapeType: BackgroundShapeType): Shape {
        return when (backgroundShapeType) {
            BackgroundShapeType.FILL_RECTANGLE -> FillRectangleBackground()
            BackgroundShapeType.OUTLINED_RECTANGLE -> OutlinedRectangleBackground()
            BackgroundShapeType.BOTTOM_LINE -> BottomLineBackground()
            else -> Empty()
        }
    }
}