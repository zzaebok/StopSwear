package com.kobeazz.stopswear

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val TAG = "MAIN_ACTIVITY"
        private const val PACKAGE_NAME = "com.kobeazz.stopswear/com.kobeazz.stopswear.StopSwearingService"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

        // drawer
        initializeDrawer()
        setFunctionsOnNavigation()

        // onOff button
        setOnOffButton()
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
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            findViewById(R.id.drawerLayout),
            findViewById(R.id.toolbar),
            R.string.serviceOn,
            R.string.serviceOff
        )
        findViewById<DrawerLayout>(R.id.drawerLayout).addDrawerListener(actionBarDrawerToggle)
    }

    fun setFunctionsOnNavigation() {
        // set item click listener
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // vibration setting
        val menu = navigationView.menu
        val vibration = menu.findItem(R.id.vibration)
        val switch = vibration.actionView as Switch
        switch.setOnCheckedChangeListener {
            _, isChecked ->
            Log.d(TAG, isChecked.toString())
            val dataManager = DataManager.getInstance(this)
            dataManager.putValue("vibration", isChecked, "Boolean")
        }
        // default vibration = true
        switch.isChecked = true

        // app version
        val versionNumber = packageManager.getPackageInfo(packageName, 0).versionName
        val versionInfo = menu.findItem(R.id.version)
        versionInfo.title = "버전 정보: ${versionNumber}"
    }

    fun checkAccessibilityPermission(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(contentResolver, android.provider.Settings.Secure.ACCESSIBILITY_ENABLED)
        Log.d(TAG, "ACCESSIBILITY_ENABLED: " + accessibilityEnabled)
        val mStringColonSplitter: TextUtils.SimpleStringSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            mStringColonSplitter.setString(settingValue)
            mStringColonSplitter.forEach {
                if (it == PACKAGE_NAME) {
                    return true
                }
            }
        }
        return false
    }

    fun showAccessibilityPermission(mode: String) {
        val message: String
        when (mode) {
            "on" -> message = "나쁜말 탐지기를 이용하시기 위해서는 접근성 권한 설정이 필요합니다. 사용자의 데이터는 절대로 수집되지 않습니다."
            "off" -> message = "나쁜말 탐지기 종료를 위해 접근성 권한을 꺼주세요."
            else -> message = "mode 오류, 버그 리포트 부탁드립니다."
        }

        var builder = AlertDialog.Builder(this)
        builder.setTitle("접근성 권한 설정")
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton("확인", {
            _, _ -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        })
        builder.create().show()
    }

    fun setOnOffButton() {
        findViewById<Switch>(R.id.onOffSwitch).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!checkAccessibilityPermission()) {
                    showAccessibilityPermission("on")
                }
                findViewById<TextView>(R.id.switchText).text = getString(R.string.serviceOn)
                findViewById<ImageView>(R.id.onOffImage).setImageResource(R.drawable.ic_baseline_sentiment_satisfied_alt_24)
            } else {
                if (checkAccessibilityPermission()) {
                    showAccessibilityPermission("off")
                }
                findViewById<TextView>(R.id.switchText).text = getString(R.string.serviceOff)
                findViewById<ImageView>(R.id.onOffImage).setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
            }
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.report -> {
                val intent = Intent(this, ReportActivity::class.java)
                startActivity(intent)
            }
            R.id.vibration -> {
                val switch = findViewById<NavigationView>(R.id.navigationView).menu.findItem(R.id.vibration).actionView as Switch
                switch.isChecked = !switch.isChecked
            }
            R.id.howToUse -> {
                val intent = Intent(this, HowToUseActivity::class.java)
                startActivity(intent)
            }
            R.id.bug_report -> {

            }
            R.id.review -> {

            }
        }
        return true
    }
}