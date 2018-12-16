package me.bakumon.moneykeeperclone.view.mpchartpatch.piechart

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
