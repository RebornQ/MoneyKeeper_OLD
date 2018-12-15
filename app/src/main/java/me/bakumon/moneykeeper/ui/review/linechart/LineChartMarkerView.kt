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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.MonthSumMoneyBean
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.utill.BigDecimalUtil

/**
 * 折线图 MarkerView
 *
 * @author Bakumon https://bakumon.me
 */
@SuppressLint("ViewConstructor")
class LineChartMarkerView(context: Context) : MarkerView(context, R.layout.bar_chart_marker_view) {
    private val tvContent: TextView = findViewById(R.id.tv_content)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val bean = e!!.data as MonthSumMoneyBean
        val content = DefaultSPHelper.symbol + BigDecimalUtil.fen2Yuan(bean.sumMoney)
        tvContent.text = content
        if (e.y > 0) {
            tvContent.visibility = View.VISIBLE
            if (bean.type == RecordType.TYPE_OUTLAY) {
                tvContent.setBackgroundColor(ContextCompat.getColor(App.instance, R.color.colorOutlay))
            } else {
                tvContent.setBackgroundColor(ContextCompat.getColor(App.instance, R.color.colorIncome))
            }
            tvContent.setTextColor(Color.WHITE)
        } else {
            tvContent.visibility = View.GONE
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}
