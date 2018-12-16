package me.bakumon.moneykeeperclone.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View

/**
 * 高度自适应的 ViewPager
 *
 * @author Bakumon https://bakumon.me
 */

open class WrapContentViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        // 遍历所有child的高度，确认高度为子View的最大高度
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                val h = child.measuredHeight
                if (h > height) {
                    height = h
                }
            }
        }
        // 构造 heightMeasureSpec
        val resultHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        // 调用父类 onMeasure
        super.onMeasure(widthMeasureSpec, resultHeightMeasureSpec)
    }
}