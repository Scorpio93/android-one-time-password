# Android one time password view

[![](https://jitpack.io/v/Scorpio93/android-one-time-password.svg)](https://jitpack.io/#Scorpio93/android-one-time-password)

## Installation and using
1. Add it in your root build.gradle at the end of repositories:
```javascript
allprojects {
    repositories {
        ...
    	maven { url 'https://jitpack.io' }
    }
}
```
2. Add the dependency to your application gradle file:
```javascript
dependencies {
    implementation 'com.github.Scorpio93:android-one-time-password:1.0.0'
}
```

3. Define view in your layout file:
```javascript
<com.optview.otp.OtpCodeView
    android:id="@+id/editTextOtp"
    android:layout_width="0dp"
    android:layout_height="55dp"
    app:optBackgroundShapeColor="@color/teal_700"
    app:otpBackgroundHighlightColor="@color/white"
    app:otpBackgroundRoundedCorners="4dp"
    app:otpBackgroundShape="none"
    app:otpBackgroundShapePadding="6dp"
    app:otpBackgroundShapeStroke="2dp"
    app:otpFontFamily="@font/train_one_regular"
    app:otpHighlightTextHolderColor="@android:color/white"
    app:otpTextColor="@android:color/white"
    app:otpTextHolderColor="@color/teal_700"
    app:otpTextHolderShape="none"
    app:otpTextHolderStrokeWidth="2dp"
    app:otpTextHolderWidth="16dp"
    app:otpMaxLength="6"
    app:otpTextSize="24dp" />
```
4. Listen to input event result: 
```javascript
val otpCodeView: OtpCodeView = findViewById<OtpCodeView>(R.id.editTextOtp).apply {
    requestFocus()
    setOnClickListener { showSoftKeyboard() }
    setOtpCodeFinishListener { code ->
        Toast.makeText(this@MainActivity, code, Toast.LENGTH_LONG).show()
    }
}
```
Advanced features and view abilities you can find in an example application.
