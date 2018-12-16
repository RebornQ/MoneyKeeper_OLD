package me.bakumon.moneykeeperclone.ui.statistics.reports.piechart

import android.content.Context
import com.github.mikephil.charting.utils.ColorTemplate
import me.bakumon.moneykeeperclone.R

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
