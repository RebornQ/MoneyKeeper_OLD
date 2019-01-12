/*
 * Copyright 2018 Bakumon. https://github.com/Bakumon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.bakumon.moneykeeper.ui.statistics.bill.barchart

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.DaySumMoneyBean
import me.bakumon.moneykeeper.utill.DateUtils
import me.bakumon.moneykeeper.view.mpchartpatch.barchart.RoundBarChart
import java.util.*

class MyBarChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    RoundBarChart(context, attrs, defStyle) {

    init {
        this.setNoDataText("")
        this.setScaleEnabled(false)
        this.description.isEnabled = false
        this.legend.isEnabled = false

        this.axisLeft.axisMinimum = 0f
        this.axisLeft.isEnabled = false
        this.axisRight.isEnabled = false

        val mv = BarChartMarkerView(context)
        mv.chartView = this
        this.marker = mv
    }

    fun setChartData(daySumMoneyBeans: List<DaySumMoneyBean>?, dateFrom: Date, dateTo: Date) {
        if (daySumMoneyBeans == null || daySumMoneyBeans.isEmpty()) {
            this.visibility = View.INVISIBLE
            return
        } else {
            this.visibility = View.VISIBLE
        }

        val dateList = DateUtils.getRangeDate(dateFrom, dateTo)
        val barEntries = BarEntryConverter.getBarEntryList(dateList, daySumMoneyBeans)

        // x 轴显示日期
        val xAxis = this.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textColor = ContextCompat.getColor(context, R.color.colorTextHint)
        xAxis.axisMinimum = 0F
        xAxis.setValueFormatter { value, _ ->
            val intValue = value.toInt()
            if (intValue >= 0 && intValue < dateList.size) {
                DateUtils.date2MonthDay(dateList[intValue])
            } else {
                ""
            }
        }

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
