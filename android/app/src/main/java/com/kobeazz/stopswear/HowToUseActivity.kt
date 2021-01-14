package com.kobeazz.stopswear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator

class HowToUseActivity : AppCompatActivity() {
    private var howToUseDataList = ArrayList<HowToUseData>()
    private lateinit var howToUseViewHolderAdapater: HowToUseViewHolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        howToUseDataList.add(HowToUseData(R.drawable.ic_baseline_menu_24, "버튼을 눌러 나쁜말 탐지기를 작동합니다."))
        howToUseDataList.add(HowToUseData(R.drawable.ic_baseline_menu_24, "접근성 권한을 설정해주어야 합니다."))
        howToUseDataList.add(HowToUseData(R.drawable.ic_baseline_menu_24, "접근성 권한 사용에 동의해주세요.\n정보는 절대 서버로 보내지지 않습니다."))
        howToUseDataList.add(HowToUseData(R.drawable.ic_baseline_menu_24, "메세지를 입력할 때 나쁜말을 탐지하면 경고가 발생합니다."))

        howToUseViewHolderAdapater = HowToUseViewHolderAdapter(howToUseDataList)

        val pager = findViewById<ViewPager2>(R.id.pager)

        pager.apply {
            adapter = howToUseViewHolderAdapater
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        findViewById<SpringDotsIndicator>(R.id.spring_dots_indicator).setViewPager2(pager)

    }
}