package me.bakumon.moneykeeperclone.view.mpchartpatch.barchart

import android.content.Context
import android.util.AttributeSet

import com.github.mikephil.charting.charts.BarChart

/**
 * 上方圆角柱状图
 *
 * @author Bakumon https://bakumon.me
 */
open class RoundBarChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : BarChart(context, attrs, defStyle) {
    override fun init() {
        super.init()
        mRenderer = RoundBarChartRenderer(this, mAnimator, mViewPortHandler)
    }
}
