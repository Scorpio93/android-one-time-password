package com.optview.otp.symbolHolders

import com.optview.otp.Shape


object TextHolderFactory {
    fun getShape(textHolderType: TextHolderType): Shape {
        return when (textHolderType) {
            TextHolderType.CIRCLE -> Circle()
            TextHolderType.LINE -> Line()
            else -> Empty()
        }
    }
}