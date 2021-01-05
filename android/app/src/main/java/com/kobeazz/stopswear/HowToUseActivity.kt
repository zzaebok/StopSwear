package com.kobeazz.stopswear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class HowToUseActivity : AppCompatActivity() {
    private var howToUseDataList = ArrayList<HowToUseData>()
    private lateinit var howToUseViewHolderAdapater: HowToUseViewHolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        howToUseDataList.add(HowToUseData(R.drawable.ic_baseline_menu_24, "1 페이지"))
        howToUseDataList.add(HowToUseData(R.drawable.ic_baseline_menu_24, "2 페이지"))
        howToUseDataList.add(HowToUseData(R.drawable.ic_baseline_menu_24, "3 페이지"))

        howToUseViewHolderAdapater = HowToUseViewHolderAdapter(howToUseDataList)

        findViewById<ViewPager2>(R.id.pager).apply {
            adapter = howToUseViewHolderAdapater
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

    }
}