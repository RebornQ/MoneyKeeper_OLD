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

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.DaySumMoneyBean
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.DateUtils
import java.math.BigDecimal

/**
 * 柱状图 MarkerView
 *
 * @author Bakumon https://bakumon.me
 */
@SuppressLint("ViewConstructor")
class BarChartMarkerView(context: Context) : MarkerView(context, R.layout.bar_chart_marker_view) {
    private val tvContent: TextView = findViewById(R.id.tv_content)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val data = e?.data
        if (data is DaySumMoneyBean) {
            val date = DateUtils.date2MonthDay(data.time)
            val money = DefaultSPHelper.symbol + BigDecimalUtil.fen2Yuan(data.daySumMoney)
            val content = "$date $money"
            tvContent.text = content
            if (data.daySumMoney > BigDecimal(0)) {
                tvContent.visibility = View.VISIBLE
            } else {
                tvContent.visibility = View.GONE
            }
        } else {
            tvContent.visibility = View.GONE
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}
