/*
 * Copyright (C) 2013 Leszek Mzyk
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

package me.relex.circleindicator

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import me.bakumon.circleindicator.R

/**
 * 指示器
 *
 * @author https://github.com/ongakuer/CircleIndicator
 */
class CircleIndicator : LinearLayout {
    private var mIndicatorMargin = -1
    private var mIndicatorWidth = -1
    private var mIndicatorHeight = -1
    private var mAnimatorResId = R.animator.scale_with_alpha
    private var mAnimatorReverseResId = 0
    private var mIndicatorBackgroundResId = R.drawable.white_radius
    private var mIndicatorUnselectedBackgroundResId = R.drawable.white_radius
    private var mImmediateAnimatorOut: Animator? = null
    private var mImmediateAnimatorIn: Animator? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        handleTypedArray(context, attrs)
        checkIndicatorConfig(context)
    }

    private fun handleTypedArray(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicator)
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_width, -1)
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_height, -1)
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_margin, -1)

        mAnimatorResId = typedArray.getResourceId(
            R.styleable.CircleIndicator_ci_animator,
            R.animator.scale_with_alpha
        )
        mAnimatorReverseResId = typedArray.getResourceId(R.styleable.CircleIndicator_ci_animator_reverse, 0)
        mIndicatorBackgroundResId = typedArray.getResourceId(
            R.styleable.CircleIndicator_ci_drawable,
            R.drawable.white_radius
        )
        mIndicatorUnselectedBackgroundResId = typedArray.getResourceId(
            R.styleable.CircleIndicator_ci_drawable_unselected,
            mIndicatorBackgroundResId
        )

        val orientation = typedArray.getInt(R.styleable.CircleIndicator_ci_orientation, -1)
        setOrientation(if (orientation == LinearLayout.VERTICAL) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL)

        val gravity = typedArray.getInt(R.styleable.CircleIndicator_ci_gravity, -1)
        setGravity(if (gravity >= 0) gravity else Gravity.CENTER)

        typedArray.recycle()
    }

    private fun checkIndicatorConfig(context: Context) {
        mIndicatorWidth = if (mIndicatorWidth < 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else mIndicatorWidth
        mIndicatorHeight = if (mIndicatorHeight < 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else mIndicatorHeight
        mIndicatorMargin = if (mIndicatorMargin < 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else mIndicatorMargin

        mAnimatorResId = if (mAnimatorResId == 0) R.animator.scale_with_alpha else mAnimatorResId

        mImmediateAnimatorOut = createAnimatorOut(context)
        mImmediateAnimatorOut!!.duration = 0

        mImmediateAnimatorIn = createAnimatorIn(context)
        mImmediateAnimatorIn!!.duration = 0

        mIndicatorBackgroundResId = if (mIndicatorBackgroundResId == 0)
            R.drawable.white_radius
        else
            mIndicatorBackgroundResId
        mIndicatorUnselectedBackgroundResId = if (mIndicatorUnselectedBackgroundResId == 0)
            mIndicatorBackgroundResId
        else
            mIndicatorUnselectedBackgroundResId
    }

    private fun createAnimatorOut(context: Context): Animator {
        return AnimatorInflater.loadAnimator(context, mAnimatorResId)
    }

    private fun createAnimatorIn(context: Context): Animator {
        val animatorIn: Animator
        if (mAnimatorReverseResId == 0) {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorResId)
            animatorIn.interpolator = ReverseInterpolator()
        } else {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorReverseResId)
        }
        return animatorIn
    }

    fun setTotal(count: Int, currentItem: Int) {
        removeAllViews()
        if (count <= 0) {
            return
        }

        val orientation = orientation

        for (i in 0 until count) {
            if (currentItem == i) {
                addIndicator(orientation, mIndicatorBackgroundResId, mImmediateAnimatorOut!!)
            } else {
                addIndicator(orientation, mIndicatorUnselectedBackgroundResId, mImmediateAnimatorIn!!)
            }
        }
    }

    private fun addIndicator(
        orientation: Int, @DrawableRes backgroundDrawableId: Int,
        animator: Animator
    ) {
        if (animator.isRunning) {
            animator.end()
            animator.cancel()
        }

        val indicator = View(context)
        indicator.setBackgroundResource(backgroundDrawableId)
        addView(indicator, mIndicatorWidth, mIndicatorHeight)
        val lp = indicator.layoutParams as LinearLayout.LayoutParams

        if (orientation == LinearLayout.HORIZONTAL) {
            lp.leftMargin = mIndicatorMargin
            lp.rightMargin = mIndicatorMargin
        } else {
            lp.topMargin = mIndicatorMargin
            lp.bottomMargin = mIndicatorMargin
        }

        indicator.layoutParams = lp

        animator.setTarget(indicator)
        animator.start()
    }

    private inner class ReverseInterpolator : Interpolator {
        override fun getInterpolation(value: Float): Float {
            return Math.abs(1.0f - value)
        }
    }

    private fun dip2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    companion object {
        private const val DEFAULT_INDICATOR_WIDTH = 5
    }
}
