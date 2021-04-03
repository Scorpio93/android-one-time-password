package com.optdemo.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.optview.otp.OtpCodeView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<OtpCodeView>(R.id.editTextOtp).apply {
            requestFocus()
            setOnClickListener { showSoftKeyboard() }
        }
    }
}