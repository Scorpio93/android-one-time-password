package com.optview.otp

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
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
import com.optview.otp.background.BackgroundShapeFactory
import com.optview.otp.background.BackgroundShapeType
import com.optview.otp.background.FillRectangleBackground
import com.optview.otp.background.OutlinedRectangleBackground
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
    private var otpHighlightTextHolderColor = 0
    private var textHolderType : TextHolderType = TextHolderType.NONE


    // background shape
    private var otpBackgroundShapeColor = 0
    private var otpBackgroundShapePadding = 0F
    private var otpBackgroundShapeStroke = 0F
    private var otpBackgroundShapeRoundCorners = 0F
    private var otpHighlightBackgroundColor = 0
    private var backgroundType : BackgroundShapeType = BackgroundShapeType.NONE

    // other features
    private var otpTextColor = 0
    private var otpTextSize = 0F
    private var otpFontFamily = 0
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
                otpHighlightTextHolderColor = typedArray.getColor(R.styleable.OtpCodeView_otpHighlightTextHolderColor, 0)
                textHolderType = typedArray.getEnum(R.styleable.OtpCodeView_otpTextHolderShape, TextHolderType.NONE)

                // background properties
                otpBackgroundShapeColor = typedArray.getColor(R.styleable.OtpCodeView_optBackgroundShapeColor, 0)
                otpBackgroundShapePadding = typedArray.getDimension(R.styleable.OtpCodeView_otpBackgroundShapePadding, 0F)
                otpBackgroundShapeStroke = typedArray.getDimension(R.styleable.OtpCodeView_otpBackgroundShapeStroke, 0F)
                otpBackgroundShapeRoundCorners = typedArray.getDimension(R.styleable.OtpCodeView_otpBackgroundRoundedCorners, 0F)
                otpHighlightBackgroundColor = typedArray.getColor(R.styleable.OtpCodeView_otpBackgroundHighlightColor, 0)
                backgroundType = typedArray.getEnum(R.styleable.OtpCodeView_otpBackgroundShape, BackgroundShapeType.NONE)

                // other
                otpMaxSymbolsAmount = typedArray.getInt(R.styleable.OtpCodeView_otpMaxSymbolsAmount, MIX_SYMBOL_SIZE)
                otpTextColor = typedArray.getColor(R.styleable.OtpCodeView_otpTextColor, 0)
                otpTextSize = typedArray.getDimension(R.styleable.OtpCodeView_otpTextSize, 0f)
                otpFontFamily = typedArray.getResourceId(R.styleable.OtpCodeView_otpFontFamily, 0)
            } finally {
                recycle()
            }
        }
    }

    private var textChangeListener: OnTextChangeListener? = null
    private val codeBuilder: StringBuilder by lazy { StringBuilder() }

    private val textHolderPaint = Paint().apply {
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
        val shapeTextHolder: Shape = TextHolderFactory.getShape(textHolderType)
        val backgroundShape : Shape = BackgroundShapeFactory.getShape(backgroundType)

        if (otpFontFamily != 0) {
            val typeface = ResourcesCompat.getFont(context, otpFontFamily)
            textPaint.typeface = typeface
        }

        val inputLength = codeBuilder.length

        for (i in 0 until otpMaxSymbolsAmount) {
            val highlightIndex = codeBuilder.length
            val needToHighlight = highlightIndex == i
            val hasTextHolderHighlightColor = otpHighlightTextHolderColor != 0
            val hasBackgroundHighlightColor = otpHighlightBackgroundColor != 0

            textHolderPaint.color = if (needToHighlight and hasTextHolderHighlightColor) otpHighlightTextHolderColor else otpTextPlaceHolderColor
            backgroundPaint.color = if (needToHighlight and hasBackgroundHighlightColor) otpHighlightBackgroundColor else otpBackgroundShapeColor

            val currentHostStepWidth = width / otpMaxSymbolsAmount
            val halfCurrentHostWidth = currentHostStepWidth / 2F
            val halfCurrentHostHeight = height / 2F
            val currentHostWidth = currentHostStepWidth * i.toFloat()
            val halfLineWidth = otpTextPlaceHolderWidth / 2F

            backgroundShape.drawHolder(
                startX = currentHostWidth,
                startY = 0F,
                stopX = currentHostWidth + currentHostStepWidth,
                stopY = height.toFloat(),
                padding = otpBackgroundShapePadding,
                corners = otpBackgroundShapeRoundCorners,
                paint = backgroundPaint,
                canvas = canvas
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
                    startX = currentHostWidth + halfCurrentHostWidth,
                    startY = halfCurrentHostHeight,
                    stopX = currentHostWidth + halfCurrentHostWidth,
                    stopY =  halfCurrentHostHeight,
                    padding = halfLineWidth / 2F,
                    paint =  textHolderPaint,
                    canvas = canvas
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

    fun setTextHolderShape(textHolderType: TextHolderType) {
        this.textHolderType = textHolderType
        invalidate()
    }

    fun setBackgroundShape(backgroundShapeType: BackgroundShapeType) {
        this.backgroundType = backgroundShapeType
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        textChangeListener = null
    }
}