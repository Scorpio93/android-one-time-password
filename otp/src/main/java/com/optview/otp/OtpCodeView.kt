package com.optview.otp

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.InputType
import android.text.TextPaint
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.content.res.ResourcesCompat
import com.optview.otp.background.OutlinedBackground
import com.optview.otp.symbolHolders.*


private const val MIX_SYMBOL_SIZE = 4

class OtpCodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val typedArray: TypedArray by lazy {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.OtpCodeView
        )
    }

    // text holders shape
    private var otpTextPlaceHolderColor = 0
    private var otpTextPlaceHolderWidth = 0F
    private var otpTextPlaceHolderStrokeWidth = 0F

    // background shape
    private var otpBackgroundShapeColor = 0
    private var otpBackgroundShapePadding = 0F
    private var otpBackgroundShapeStroke = 0F

    // other features
    private var otpTextColor = 0
    private var otpHighlightNextSymbol = false
    private var otpHighlightNextColor = 0
    private var otpTextSize = 0F
    private var otpFontFamily = 0
    private var textHolderType : TextHolderType = TextHolderType.NONE
    private var otpMaxSymbolsAmount = 0
        set(value) {
            field = value.coerceAtLeast(MIX_SYMBOL_SIZE)
        }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.OtpCodeView, 0, 0).apply {
            try {

                // text holders properties
                otpTextPlaceHolderColor = typedArray.getColor(R.styleable.OtpCodeView_otpTextHolderColor, 0)
                otpTextPlaceHolderWidth = typedArray.getDimension(R.styleable.OtpCodeView_otpTextHolderWidth, 0F)
                otpTextPlaceHolderStrokeWidth = typedArray.getDimension(R.styleable.OtpCodeView_otpTextHolderStrokeWidth, 0F)

                // background properties
                otpBackgroundShapeColor = typedArray.getColor(R.styleable.OtpCodeView_optBackgroundShapeColor, 0)
                otpBackgroundShapePadding = typedArray.getDimension(R.styleable.OtpCodeView_otpBackgroundShapePadding, 0F)
                otpBackgroundShapeStroke = typedArray.getDimension(R.styleable.OtpCodeView_otpBackgroundShapeStroke, 0F)

                // other
                otpMaxSymbolsAmount = typedArray.getInt(R.styleable.OtpCodeView_otpMaxSymbolsAmount, MIX_SYMBOL_SIZE)
                otpTextColor = typedArray.getColor(R.styleable.OtpCodeView_otpTextColor, 0)
                otpHighlightNextSymbol = typedArray.getBoolean(R.styleable.OtpCodeView_otpHighlightNextSymbol, false)
                otpHighlightNextColor = typedArray.getColor(R.styleable.OtpCodeView_otpHighlightNextColor, 0)
                otpTextSize = typedArray.getDimension(R.styleable.OtpCodeView_otpTextSize, 0f)
                otpFontFamily = typedArray.getResourceId(R.styleable.OtpCodeView_otpFontFamily, 0)
                textHolderType = typedArray.getEnum(R.styleable.OtpCodeView_textHolderShape, TextHolderType.NONE)
            } finally {
                recycle()
            }
        }
    }

    private val shapeTextHolder: Shape = TextHolderFactory.getShape(textHolderType)
    private val backgroundShape : Shape = OutlinedBackground()
    private var textChangeListener: OnTextChangeListener? = null
    private val codeBuilder: StringBuilder by lazy { StringBuilder() }

    private val linePaint = Paint().apply {
        isAntiAlias = true
        color = otpTextPlaceHolderColor
        strokeWidth = otpTextPlaceHolderStrokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        color = otpBackgroundShapeColor
        strokeWidth = otpBackgroundShapeStroke
    }

    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
        isFocusableInTouchMode = true
        color = otpTextColor
        textSize = otpTextSize
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        val baseInputConnection: BaseInputConnection = object : BaseInputConnection(this, false) {
            override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
                val downKeyAction = sendKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_DEL
                    )
                )
                val upKeyAction = sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                return downKeyAction && upKeyAction
            }
        }

        outAttrs?.run {
            actionLabel = null
            inputType = InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_NEXT
        }

        return baseInputConnection
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        super.onKeyDown(keyCode, event)
        if (keyCode == KeyEvent.KEYCODE_DEL && codeBuilder.isNotEmpty()) {
            codeBuilder.deleteCharAt(codeBuilder.length - 1)
            invalidate()
        } else if (keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 && codeBuilder.length < otpMaxSymbolsAmount) {
            codeBuilder.append(event?.displayLabel)
            invalidate()
        }

        if (codeBuilder.length >= otpMaxSymbolsAmount || keyCode == KeyEvent.KEYCODE_ENTER) {
            textChangeListener?.textEntered(codeBuilder.toString())
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val typeface = ResourcesCompat.getFont(context, otpFontFamily)
        textPaint.typeface = typeface

        val inputLength = codeBuilder.length

        for (i in 0 until otpMaxSymbolsAmount) {
            val highlightIndex = codeBuilder.length
            val needToHighlight = highlightIndex == i
            linePaint.color = if (needToHighlight and otpHighlightNextSymbol) otpHighlightNextColor else otpTextPlaceHolderColor

            val currentHostStepWidth = width / otpMaxSymbolsAmount
            val halfCurrentHostWidth = currentHostStepWidth / 2F
            val halfCurrentHostHeight = height / 2F
            val currentHostWidth = currentHostStepWidth * i.toFloat()
            val halfLineWidth = otpTextPlaceHolderWidth / 2F

            backgroundShape.drawHolder(
                currentHostWidth,
                0F,
                currentHostWidth + currentHostStepWidth,
                height.toFloat(),
                otpBackgroundShapePadding,
                backgroundPaint,
                canvas
            )

            if (inputLength > i) {
                canvas?.drawText(
                    codeBuilder.toString(),
                    i,
                    i + 1,
                    currentHostWidth + halfCurrentHostWidth,
                    halfCurrentHostHeight - (textPaint.descent() + textPaint.ascent()) / 2F,
                    textPaint
                )
            } else {
                shapeTextHolder.drawHolder(
                    currentHostWidth + halfCurrentHostWidth,
                    halfCurrentHostHeight,
                    currentHostWidth + halfCurrentHostWidth,
                    halfCurrentHostHeight,
                    halfLineWidth / 2F,
                    linePaint,
                    canvas
                )
            }
        }
    }

    fun setText(code: String) {
        if (code.length > otpMaxSymbolsAmount) return
        if (code.length == otpMaxSymbolsAmount) textChangeListener?.textEntered(code)
        if (codeBuilder.isNotBlank()) codeBuilder.clear()
        codeBuilder.append(code)
        invalidate()
    }

    fun setTextChangeListener(changeListener: OnTextChangeListener) {
        this.textChangeListener = changeListener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        textChangeListener = null
    }
}