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

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.MonthSumMoneyBean
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.utill.DateUtils
import java.math.BigDecimal
import java.util.*

/**
 * 折线图数据转换器
 *
 * @author Bakumon https://bakumon.me
 */
object LineEntryConverter {
    /**
     * 获取饼状图所需数据格式 PieEntry
     *
     * @param beans 类型汇总数据
     * @return List<PieEntry>
    </PieEntry> */
    fun getBarEntryList(sourceBeans: List<MonthSumMoneyBean>): LineData {

        val beans = sourceBeans.asReversed()

        val yVals1 = ArrayList<Entry>()
        initYVals(yVals1)
        val yVals2 = ArrayList<Entry>()
        initYVals(yVals2)
        if (beans.isNotEmpty()) {
            for (i in beans.indices) {
                val index = DateUtils.month2Index(beans[i].month)
                if (beans[i].type == RecordType.TYPE_OUTLAY) {
                    yVals1[index].y = beans[i].sumMoney.toFloat()
                    yVals1[index].data = beans[i]
                } else {
                    yVals2[index].y = beans[i].sumMoney.toFloat()
                    yVals2[index].data = beans[i]
                }
            }
        }
        val set1 = LineDataSet(yVals1, App.instance.getString(R.string.text_outlay))
        setupDateSet(set1, true)
        // 横向贝塞尔平滑
        set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

        val set2 = LineDataSet(yVals2, App.instance.getString(R.string.text_income))
        setupDateSet(set2, false)
        set2.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

        return LineData(set1, set2)
    }

    private fun initYVals(yVals: ArrayList<Entry>) {
        for (i in 0..11) {
            val entry = Entry(i.toFloat(), 0f)
            entry.data = MonthSumMoneyBean("", 0, BigDecimal(0))
            yVals.add(entry)
        }
    }

    private fun setupDateSet(set: LineDataSet, isOutlay: Boolean) {
        val colorId = if (isOutlay) R.color.colorOutlay else R.color.colorIncome
        set.color = ContextCompat.getColor(App.instance, colorId)
        set.circleColors = arrayListOf(ContextCompat.getColor(App.instance, colorId))
        set.setDrawCircleHole(false)
        set.setDrawValues(false)
        set.setDrawHighlightIndicators(false)
    }
}
