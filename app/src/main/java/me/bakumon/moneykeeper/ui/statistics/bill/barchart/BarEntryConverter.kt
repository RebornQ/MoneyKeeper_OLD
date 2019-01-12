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

import com.github.mikephil.charting.data.BarEntry
import me.bakumon.moneykeeper.database.entity.DaySumMoneyBean
import me.bakumon.moneykeeper.utill.DateUtils
import java.math.BigDecimal
import java.util.*

/**
 * 柱状图数据转换器
 *
 * @author Bakumon https://bakumon.me
 */
object BarEntryConverter {
    /**
     * 获取柱状图所需数据格式 BarEntry
     *
     * @param dateList            日期
     * @param daySumMoneyBeans 包含日期和该日期汇总数据
     * @return 柱形图所需数据
     */
    fun getBarEntryList(dateList: ArrayList<Date>, daySumMoneyBeans: List<DaySumMoneyBean>?): List<BarEntry> {
        val entryList = ArrayList<BarEntry>()
        if (daySumMoneyBeans != null && daySumMoneyBeans.isNotEmpty()) {
            val max = getMax(daySumMoneyBeans)
            var barEntry: BarEntry
            for (i in 0 until dateList.size) {
                for (j in daySumMoneyBeans.indices) {
                    if (DateUtils.isSameDay(dateList[i], daySumMoneyBeans[j].time)) {
                        // 高度补偿
                        // 加上最大值的十分之一来调整每个柱形的高度，避免数据差距太大，小数据显示太低
                        val y = max.divide(BigDecimal(10), 0, BigDecimal.ROUND_HALF_DOWN)
                            .add(daySumMoneyBeans[j].daySumMoney)
                        barEntry = BarEntry((i + 1).toFloat(), y.toFloat())
                        // 这里的 y 由于是 float，所以数值很大的话，还是会出现科学计数法
                        // 为了避免科学计数法显示,marker从data中取值
                        barEntry.data = daySumMoneyBeans[j]
                        entryList.add(barEntry)
                    }
                }
                barEntry = BarEntry((i + 1).toFloat(), 0f)
                entryList.add(barEntry)
            }
        }
        return entryList
    }

    private fun getMax(daySumMoneyBeans: List<DaySumMoneyBean>?): BigDecimal {
        // 找出最大值
        var max = BigDecimal(0)
        daySumMoneyBeans?.forEach {
            if (it.daySumMoney > max) {
                max = it.daySumMoney
            }
        }
        return max
    }
}
