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

package me.bakumon.moneykeeperclone.ui.statistics.reports.piechart

import com.github.mikephil.charting.data.PieEntry
import me.bakumon.moneykeeperclone.database.entity.TypeSumMoneyBean
import java.math.BigDecimal
import java.util.*

/**
 * 饼状图数据转换器
 *
 * @author Bakumon https://bakumon.me
 */
object PieEntryConverter {
    /**
     * 获取饼状图所需数据格式 PieEntry
     *
     * @param typeSumMoneyBeans 类型汇总数据
     * @return List<PieEntry>
    </PieEntry> */
    fun getBarEntryList(typeSumMoneyBeans: List<TypeSumMoneyBean>): List<PieEntry> {
        val entryList = ArrayList<PieEntry>()
        for (i in typeSumMoneyBeans.indices) {
            val typeMoney = typeSumMoneyBeans[i].typeSumMoney
            entryList.add(PieEntry(typeMoney.toInt().toFloat(), typeSumMoneyBeans[i].typeName, typeSumMoneyBeans[i]))
        }
        return entryList
    }

    fun getMax(typeSumMoneyBeans: List<TypeSumMoneyBean>?): BigDecimal {
        // 找出最大值
        var max = BigDecimal(0)
        typeSumMoneyBeans?.forEach {
            if (it.typeSumMoney > max) {
                max = it.typeSumMoney
            }
        }
        return max
    }
}
