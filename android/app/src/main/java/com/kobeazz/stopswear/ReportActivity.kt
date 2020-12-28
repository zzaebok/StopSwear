package com.kobeazz.stopswear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

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

        days.forEachIndexed { i, day ->
            entries.add(BarEntry(i.toFloat(), (dataManager.getValue(day, "Int") as Int).toFloat()))
        }

        val chart = findViewById<com.github.mikephil.charting.charts.BarChart>(R.id.chart)
        chart.run {
            description.isEnabled = false

            setMaxVisibleValueCount(5)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)

            axisLeft.run {
                axisMaximum = 100F
                axisMinimum = 0F
                granularity = 25F

                setDrawLabels(true)
                setDrawGridLines(true)
                setDrawAxisLine(true)
                axisLineColor = ContextCompat.getColor(context, R.color.black)
                gridColor = ContextCompat.getColor(context, R.color.material_on_primary_disabled)
                textColor = ContextCompat.getColor(context, R.color.black)
                textSize = 14F
            }

            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1F
                setDrawAxisLine(true)
                setDrawGridLines(false)
                textColor = ContextCompat.getColor(context, R.color.black)
                valueFormatter = IndexAxisValueFormatter(onlyDates)
                textSize = 14F
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
        barData.barWidth = 0.3F
        chart.run {
            this.data = barData
            setFitBars(true)
            invalidate()
        }

    }
}
