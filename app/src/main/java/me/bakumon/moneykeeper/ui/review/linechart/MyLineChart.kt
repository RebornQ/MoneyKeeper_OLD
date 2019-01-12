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
package me.bakumon.moneykeeper.ui.review.linechart

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.MonthSumMoneyBean

class MyLineChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LineChart(context, attrs, defStyle) {

    init {
        this.setNoDataText("")
        this.setScaleEnabled(false)

        this.description.isEnabled = true
        val description = Description()
        description.text = App.instance.getString(R.string.text_month_tip)
        description.textColor = ContextCompat.getColor(context, R.color.colorTextHint)
        description.yOffset = -30F
        this.description = description

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
