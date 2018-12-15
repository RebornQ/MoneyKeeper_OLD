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
package me.bakumon.moneykeeper.view.mpchartpatch.piechart

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.PieChart

/**
 * 大于 2% 显示
 * @author mafei
 */
open class NoSmallPieChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : PieChart(context, attrs, defStyle) {

    override fun init() {
        super.init()
        mRenderer = NoSmallPieChartRenderer(this, mAnimator, mViewPortHandler)
    }

    override fun setHoleColor(color: Int) {
        (mRenderer as NoSmallPieChartRenderer).paintHole.color = color
    }

    override fun setEntryLabelColor(color: Int) {
        (mRenderer as NoSmallPieChartRenderer).paintEntryLabels.color = color
    }
}
