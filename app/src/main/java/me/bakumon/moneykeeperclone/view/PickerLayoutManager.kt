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

package me.bakumon.moneykeeperclone.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

class PickerLayoutManager : LinearLayoutManager {

    private var mScale = 0.5f
    private var mIsAlpha = true
    private var mLinearSnapHelper: LinearSnapHelper? = null
    private var mOnSelectedViewListener: OnSelectedViewListener? = null
    private var mItemViewWidth: Int = 0
    private var mItemViewHeight: Int = 0
    private var mItemCount = -1
    private var mRecyclerView: RecyclerView? = null
    private var mOrientation: Int = 0

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {
        this.mLinearSnapHelper = LinearSnapHelper()
        this.mOrientation = orientation
    }

    constructor(context: Context, recyclerView: RecyclerView, orientation: Int, reverseLayout: Boolean, itemCount: Int, scale: Float, isAlpha: Boolean) : super(context, orientation, reverseLayout) {
        this.mLinearSnapHelper = LinearSnapHelper()
        this.mItemCount = itemCount
        this.mOrientation = orientation
        this.mRecyclerView = recyclerView
        this.mIsAlpha = isAlpha
        this.mScale = scale
        if (mItemCount != 0) {
            isAutoMeasureEnabled = false
        }
    }

    /**
     * 添加LinearSnapHelper
     *
     * @param view
     */
    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        mLinearSnapHelper!!.attachToRecyclerView(view)
    }

    /**
     * 没有指定显示条目的数量时，RecyclerView的宽高由自身确定
     * 指定显示条目的数量时，根据方向分别计算RecyclerView的宽高
     *
     * @param recycler
     * @param state
     * @param widthSpec
     * @param heightSpec
     */
    override fun onMeasure(recycler: RecyclerView.Recycler?, state: RecyclerView.State?, widthSpec: Int, heightSpec: Int) {
        if (itemCount != 0 && mItemCount != 0) {

            val view = recycler!!.getViewForPosition(0)
            measureChildWithMargins(view, widthSpec, heightSpec)

            mItemViewWidth = view.measuredWidth
            mItemViewHeight = view.measuredHeight

            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                val paddingHorizontal = (mItemCount - 1) / 2 * mItemViewWidth
                mRecyclerView?.clipToPadding = false
                mRecyclerView?.setPadding(paddingHorizontal, 0, paddingHorizontal, 0)
                setMeasuredDimension(mItemViewWidth * mItemCount, mItemViewHeight)
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                val paddingVertical = (mItemCount - 1) / 2 * mItemViewHeight
                mRecyclerView?.clipToPadding = false
                mRecyclerView?.setPadding(0, paddingVertical, 0, paddingVertical)
                setMeasuredDimension(mItemViewWidth, mItemViewHeight * mItemCount)
            }
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec)
        }

    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (itemCount < 0 || state.isPreLayout) {
            return
        }

        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            scaleHorizontalChildView()
        } else if (mOrientation == LinearLayoutManager.VERTICAL) {
            scaleVerticalChildView()
        }

    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        scaleHorizontalChildView()
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        scaleVerticalChildView()
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    /**
     * 横向情况下的缩放
     */
    private fun scaleHorizontalChildView() {
        val mid = width / 2.0f
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f
            val scale = 1.0f + -1 * (1 - mScale) * Math.min(mid, Math.abs(mid - childMid)) / mid
            child.scaleX = scale
            child.scaleY = scale
            if (mIsAlpha) {
                child.alpha = scale
            }
        }
    }

    /**
     * 竖向方向上的缩放
     */
    private fun scaleVerticalChildView() {
        val mid = height / 2.0f
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childMid = (getDecoratedTop(child) + getDecoratedBottom(child)) / 2.0f
            val scale = 1.0f + -1 * (1 - mScale) * Math.min(mid, Math.abs(mid - childMid)) / mid
            child.scaleX = scale
            child.scaleY = scale
            if (mIsAlpha) {
                child.alpha = scale
            }
        }
    }


    /**
     * 当滑动停止时触发回调
     *
     * @param state
     */
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == 0) {
            if (mOnSelectedViewListener != null && mLinearSnapHelper != null) {
                val view = mLinearSnapHelper!!.findSnapView(this)
                val position = getPosition(view!!)
                mOnSelectedViewListener!!.onSelectedView(view, position)
            }
        }
    }


    fun OnSelectedViewListener(listener: OnSelectedViewListener) {
        this.mOnSelectedViewListener = listener
    }

    /**
     * 停止时，显示在中间的View的监听
     */
    interface OnSelectedViewListener {
        fun onSelectedView(view: View, position: Int)
    }
}