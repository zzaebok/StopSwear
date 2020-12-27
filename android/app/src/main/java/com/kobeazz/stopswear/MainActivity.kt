package com.kobeazz.stopswear

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MAIN_ACTIVITY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // drawer
        initializeDrawer()

        // onOff button
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

        // settings
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val menu = navigationView.menu
        val vibration = menu.findItem(R.id.vibration)
        val switch = vibration.actionView as Switch

        switch.setOnCheckedChangeListener {
            _, isChecked ->
            Log.d(TAG, isChecked.toString())
            val dataManager = DataManager.getInstance(this)
            dataManager.checkVibration(isChecked)
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

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    fun initializeDrawer() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            findViewById(R.id.drawerLayout),
            findViewById(R.id.toolbar),
            R.string.serviceOn,
            R.string.serviceOff
        )
        findViewById<DrawerLayout>(R.id.drawerLayout).addDrawerListener(actionBarDrawerToggle)
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
        builder.setCancelable(false)
        builder.setPositiveButton("확인", {
            _, _ -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        })
        builder.create().show()
    }

    fun unsetAccessibilityPermission() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("접근성 권한 설정")
        builder.setMessage("나쁜말 탐지기 종료를 위해 접근성 권한을 꺼주세요.")
        builder.setCancelable(false)
        builder.setPositiveButton("확인", {
                _, _ -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        })
        builder.create().show()
    }
}