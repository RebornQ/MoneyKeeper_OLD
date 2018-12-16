package me.bakumon.moneykeeperclone.ui.review.linechart

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.database.entity.MonthSumMoneyBean

class MyLineChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LineChart(context, attrs, defStyle) {

    init {
        this.setNoDataText("")
        this.setScaleEnabled(false)
        this.description.isEnabled = false
        this.legend.isEnabled = true
        this.legend.textColor = ContextCompat.getColor(context, R.color.colorText)
        val marker = LineChartMarkerView(context)
        marker.chartView = this
        this.marker = marker

        val xAxis = this.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = ContextCompat.getColor(context, R.color.colorTextHint)
        xAxis.setLabelCount(12, true)
        xAxis.setValueFormatter { value, _ ->
            val intValue = value.toInt()
            if (intValue >= 0) {
                (intValue + 1).toString() + context.getString(R.string.text_month)
            } else {
                ""
            }
        }
        val leftAxis = this.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawAxisLine(true)
        leftAxis.setDrawGridLines(false)
        leftAxis.textSize = 0f
        leftAxis.textColor = Color.TRANSPARENT
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        val rightAxis = this.axisRight
        rightAxis.axisMinimum = 0f
        rightAxis.setDrawAxisLine(true)
        rightAxis.setDrawGridLines(false)
        rightAxis.textSize = 0f
        rightAxis.textColor = Color.TRANSPARENT
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
    }

    fun setLineChartData(beans: List<MonthSumMoneyBean>) {
        this.clear()
        if (beans.isNotEmpty()) {
            val lineData = LineEntryConverter.getBarEntryList(beans)
            this.data = lineData
            this.animateY(1000)
        }
    }
}
