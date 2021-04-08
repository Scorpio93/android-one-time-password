package com.optview.otp.textHolders


object TextHolderFactory {
    fun getShape(textHolderType: TextHolderType): TextHolder {
        return when (textHolderType) {
            TextHolderType.CIRCLE -> CircleTextHolder()
            TextHolderType.LINE -> LineTextHolder()
            else -> EmptyTextHolder()
        }
    }
}