package com.kobeazz.stopswear

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator

class HowToUseActivity : AppCompatActivity() {
    private var howToUseDataList = ArrayList<HowToUseData>()
    private lateinit var howToUseViewHolderAdapater: HowToUseViewHolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        howToUseDataList.add(HowToUseData(R.drawable.usage1, "버튼을 눌러 나쁜말 탐지기를 작동합니다."))
        howToUseDataList.add(HowToUseData(R.drawable.usage2, "접근성 권한을 설정해주어야 합니다.\n나쁜말 탐지기를 클릭해주세요."))
        howToUseDataList.add(HowToUseData(R.drawable.usage3, "접근성 권한 사용에 동의해주세요.\n정보는 절대 서버로 보내지지 않습니다."))
        howToUseDataList.add(HowToUseData(R.drawable.usage4, "나쁜말 탐지기가 성공적으로 켜졌습니다."))
        howToUseDataList.add(HowToUseData(R.drawable.usage5, "메세지를 입력할 때 나쁜말을 탐지하면 경고가 발생합니다."))

        howToUseViewHolderAdapater = HowToUseViewHolderAdapter(howToUseDataList)

        val pager = findViewById<ViewPager2>(R.id.pager)

        pager.apply {
            adapter = howToUseViewHolderAdapater
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        findViewById<SpringDotsIndicator>(R.id.spring_dots_indicator).setViewPager2(pager)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

    }
}
