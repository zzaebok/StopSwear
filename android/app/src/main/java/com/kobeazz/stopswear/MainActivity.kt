package com.kobeazz.stopswear

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MAIN_ACTIVITY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Switch>(R.id.onOffSwitch).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (!checkAccessibilityPermission()) {
                    setAccessibilityPermission()
                }
                findViewById<TextView>(R.id.switchText).text = getString(R.string.serviceOn)
                findViewById<ImageView>(R.id.onOffImage).setImageResource(R.drawable.ic_baseline_sentiment_satisfied_alt_24)

            } else {
                if (checkAccessibilityPermission()) {
                    unsetAccessibilityPermission()
                }
                findViewById<TextView>(R.id.switchText).text = getString(R.string.serviceOff)
                findViewById<ImageView>(R.id.onOffImage).setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkAccessibilityPermission()) {
            findViewById<TextView>(R.id.switchText).text = getString(R.string.serviceOn)
            findViewById<ImageView>(R.id.onOffImage).setImageResource(R.drawable.ic_baseline_sentiment_satisfied_alt_24)
            findViewById<Switch>(R.id.onOffSwitch).setChecked(true)
        } else {
            findViewById<TextView>(R.id.switchText).text = getString(R.string.serviceOff)
            findViewById<ImageView>(R.id.onOffImage).setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
            findViewById<Switch>(R.id.onOffSwitch).setChecked(false)
        }
    }

    fun checkAccessibilityPermission(): Boolean {
        val packageName = "com.kobeazz.stopswear/com.kobeazz.stopswear.StopSwearingService"
        val accessibilityEnabled = Settings.Secure.getInt(this.contentResolver, android.provider.Settings.Secure.ACCESSIBILITY_ENABLED)
        Log.d(TAG, "ACCESSIBILITY_ENABLED: " + accessibilityEnabled)
        val mStringColonSplitter: TextUtils.SimpleStringSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    Log.d(TAG, "ACCESSIBILITY_SERVICE: " + accessibilityService)
                    if (accessibilityService == packageName) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun setAccessibilityPermission() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("접근성 권한 설정")
        builder.setMessage("나쁜말 탐지기를 이용하시기 위해서는 접근성 권한 설정이 필요합니다.")
        builder.setPositiveButton("확인", {
            _, _ -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        })
        builder.create().show()
    }

    fun unsetAccessibilityPermission() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("접근성 권한 설정")
        builder.setMessage("나쁜말 탐지기 종료를 위해 접근성 권한을 꺼주세요.")
        builder.setPositiveButton("확인", {
                _, _ -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        })
        builder.create().show()
    }
}