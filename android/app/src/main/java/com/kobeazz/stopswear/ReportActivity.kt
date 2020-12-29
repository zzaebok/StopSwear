package com.kobeazz.stopswear

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.lang.Math.round

// ref: https://chjune0205.tistory.com/81

class ReportActivity : AppCompatActivity() {

    companion object {
        private val TAG = "REPORT_ACTIVITY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        drawBarChart()
    }

    private fun drawBarChart() {
        val entries = ArrayList<BarEntry>()

        val dataManager = DataManager.getInstance(this)
        val today = dataManager.getToday()
        if (!dataManager.contains(today)) {
            dataManager.updateDates()
        }
        val daysString = dataManager.getValue("days", "String").toString()
        val days = daysString.split("/").reversed()
        val onlyDates = days.map {it.split("-").slice(1..2).joinToString("-")}
        var averageSwear = 0f
        days.forEachIndexed { i, day ->
            val swearTime = dataManager.getValue(day, "Int") as Int
            entries.add(BarEntry(i.toFloat(), (swearTime).toFloat()))
            averageSwear += swearTime
        }
        averageSwear /= days.size

        val chart = findViewById<com.github.mikephil.charting.charts.BarChart>(R.id.chart)
        chart.run {
            description.isEnabled = false

            setMaxVisibleValueCount(5)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)

            axisLeft.run {
                axisMaximum = 100f
                axisMinimum = 0f
                granularity = 25f

                setDrawLabels(true)
                setDrawGridLines(true)
                setDrawAxisLine(true)
                axisLineColor = ContextCompat.getColor(context, R.color.black)
                gridColor = ContextCompat.getColor(context, R.color.material_on_primary_disabled)
                textColor = ContextCompat.getColor(context, R.color.black)
                textSize = 10f
            }

            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawAxisLine(true)
                setDrawGridLines(false)
                textColor = ContextCompat.getColor(context, R.color.black)
                valueFormatter = IndexAxisValueFormatter(onlyDates)
                textSize = 10f
            }

            axisRight.isEnabled = false
            setTouchEnabled(false)
            animateY(1000)
            legend.isEnabled = false
        }

        var dataset = BarDataSet(entries, "DataSet")
        dataset.color = ContextCompat.getColor(this, R.color.design_default_color_error)

        val datasetList = ArrayList<IBarDataSet>()
        datasetList.add(dataset)
        val barData= BarData(datasetList)
        barData.barWidth = 0.3f
        chart.run {
            this.data = barData
            setFitBars(true)
            invalidate()
        }

        findViewById<TextView>(R.id.averageSwear).text = "일 평균 ${round(averageSwear * 10)/10.0}회"

    }
}
