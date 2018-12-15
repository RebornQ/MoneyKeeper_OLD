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
package me.bakumon.moneykeeper.ui.statistics.reports.piechart

import android.content.Context
import com.github.mikephil.charting.utils.ColorTemplate
import me.bakumon.moneykeeper.R

/**
 * 生成饼图的颜色数组
 *
 * @author Bakumon https://bakumon.me
 */
object PieColorsCreator {
    fun colors(context: Context?, count: Int): List<Int> {

        val ids = arrayListOf(
                R.color.colorPieChart1,
                R.color.colorPieChart2,
                R.color.colorPieChart3,
                R.color.colorPieChart4,
                R.color.colorPieChart5,
                R.color.colorPieChart6)

        if (count % 2 != 0) {
            ids.add(R.color.colorPieChart7)
        }

        val allColors = ColorTemplate.createColors(context?.resources, ids.toIntArray())

        val result: ArrayList<Int> = ArrayList()

        for (i in 0..count) {
            result.add(allColors[i % allColors.size])
        }
        return result
    }
}
