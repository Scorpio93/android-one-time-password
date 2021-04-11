package com.optdemo.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import com.optview.otp.OtpCodeView
import com.optview.otp.background.BackgroundShapeType
import com.optview.otp.symbolHolders.TextHolderType

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val otpCodeView: OtpCodeView = findViewById<OtpCodeView>(R.id.editTextOtp).apply {
            requestFocus()
            setOnClickListener { showSoftKeyboard() }
            setOtpCodeFinishListener { code ->
                Toast.makeText(this@MainActivity, code, Toast.LENGTH_LONG).show()
            }
        }


        findViewById<RadioGroup>(R.id.rgTextHolderShape).apply {
            setOnCheckedChangeListener { group, checkedId ->
                val shapeType = when (checkedId) {
                    R.id.rbTextCircle -> TextHolderType.CIRCLE
                    R.id.rbTextLine -> TextHolderType.LINE
                    else -> TextHolderType.NONE
                }
                otpCodeView.setTextHolderShape(shapeType)
            }
        }

        findViewById<RadioGroup>(R.id.rgBackgroundShape).apply {
            setOnCheckedChangeListener { group, checkedId ->
                val shapeBackground = when (checkedId) {
                    R.id.rbBackgroundBottomLine -> BackgroundShapeType.BOTTOM_LINE
                    R.id.rbBackgroundFillRectangle -> BackgroundShapeType.FILL_RECTANGLE
                    R.id.rbBackgroundOutlinedRectangle -> BackgroundShapeType.OUTLINED_RECTANGLE
                    else -> BackgroundShapeType.NONE
                }
                otpCodeView.setBackgroundShape(shapeBackground)
            }
        }
    }
}