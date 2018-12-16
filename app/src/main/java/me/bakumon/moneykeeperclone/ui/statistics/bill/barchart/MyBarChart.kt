package me.bakumon.moneykeeperclone.ui.statistics.bill.barchart

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.database.entity.DaySumMoneyBean
import me.bakumon.moneykeeperclone.utill.DateUtils
import me.bakumon.moneykeeperclone.view.mpchartpatch.barchart.RoundBarChart
import java.util.*

class MyBarChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RoundBarChart(context, attrs, defStyle) {

    init {
        this.setNoDataText("")
        this.setScaleEnabled(false)
        this.description.isEnabled = false
        this.legend.isEnabled = false

        this.axisLeft.axisMinimum = 0f
        this.axisLeft.isEnabled = false
        this.axisRight.isEnabled = false
        val xAxis = this.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textColor = ContextCompat.getColor(context, R.color.colorTextHint)
        xAxis.labelCount = 5
        xAxis.setValueFormatter { value, _ ->
            val intValue = value.toInt()
            if (intValue >= 0) {
                intValue.toString() + context.getString(R.string.text_day)
            } else {
                ""
            }
        }

        val mv = BarChartMarkerView(context)
        mv.chartView = this
        this.marker = mv
    }

    fun setChartData(daySumMoneyBeans: List<DaySumMoneyBean>?, year: Int, month: Int) {
        if (daySumMoneyBeans == null || daySumMoneyBeans.isEmpty()) {
            this.visibility = View.INVISIBLE
            return
        } else {
            this.visibility = View.VISIBLE
        }

        val count = DateUtils.getDayCount(year, month)
        val barEntries = BarEntryConverter.getBarEntryList(count, daySumMoneyBeans)

        val set1: BarDataSet
        if (this.data != null && this.data.dataSetCount > 0) {
            set1 = this.data.getDataSetByIndex(0) as BarDataSet
            set1.values = barEntries
            this.data.notifyDataChanged()
            this.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(barEntries, "")
            set1.setDrawIcons(false)
            set1.setDrawValues(false)
            set1.color = ContextCompat.getColor(context, R.color.colorAccent)
            set1.highLightAlpha = 70

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.barWidth = 0.5f
            data.isHighlightEnabled = true
            this.data = data
        }
        this.invalidate()
        this.animateY(1000)
    }

}
