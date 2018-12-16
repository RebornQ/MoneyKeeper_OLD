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

package me.bakumon.moneykeeperclone.view.pagerlayoutmanager

import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.view.View

import me.bakumon.moneykeeperclone.view.pagerlayoutmanager.PagerConfig.Loge

/**
 * 作用：分页居中工具
 * 作者：GcsSloop
 * 摘要：每次只滚动一个页面
 */
class PagerGridSnapHelper : SnapHelper() {
    private var mRecyclerView: RecyclerView? = null                     // RecyclerView

    /**
     * 用于将滚动工具和 Recycler 绑定
     *
     * @param recyclerView RecyclerView
     * @throws IllegalStateException 状态异常
     */
    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    /**
     * 计算需要滚动的向量，用于页面自动回滚对齐
     *
     * @param layoutManager 布局管理器
     * @param targetView    目标控件
     * @return 需要滚动的距离
     */
    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager,
                                              targetView: View): IntArray? {
        val pos = layoutManager.getPosition(targetView)
        Loge("findTargetSnapPosition, pos = $pos")
        var offset = IntArray(2)
        if (layoutManager is PagerGridLayoutManager) {
            offset = layoutManager.getSnapOffset(pos)
        }
        return offset
    }

    /**
     * 获得需要对齐的View，对于分页布局来说，就是页面第一个
     *
     * @param layoutManager 布局管理器
     * @return 目标控件
     */
    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return if (layoutManager is PagerGridLayoutManager) {
            layoutManager.findSnapView()
        } else null
    }

    /**
     * 获取目标控件的位置下标
     * (获取滚动后第一个View的下标)
     *
     * @param layoutManager 布局管理器
     * @param velocityX     X 轴滚动速率
     * @param velocityY     Y 轴滚动速率
     * @return 目标控件的下标
     */
    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager?,
                                        velocityX: Int, velocityY: Int): Int {
        var target = RecyclerView.NO_POSITION
        Loge("findTargetSnapPosition, velocityX = $velocityX, velocityY$velocityY")
        if (null != layoutManager && layoutManager is PagerGridLayoutManager) {
            val manager = layoutManager as PagerGridLayoutManager?
            if (manager!!.canScrollHorizontally()) {
                if (velocityX > PagerConfig.flingThreshold) {
                    target = manager.findNextPageFirstPos()
                } else if (velocityX < -PagerConfig.flingThreshold) {
                    target = manager.findPrePageFirstPos()
                }
            } else if (manager.canScrollVertically()) {
                if (velocityY > PagerConfig.flingThreshold) {
                    target = manager.findNextPageFirstPos()
                } else if (velocityY < -PagerConfig.flingThreshold) {
                    target = manager.findPrePageFirstPos()
                }
            }
        }
        Loge("findTargetSnapPosition, target = $target")
        return target
    }

    /**
     * 一扔(快速滚动)
     *
     * @param velocityX X 轴滚动速率
     * @param velocityY Y 轴滚动速率
     * @return 是否消费该事件
     */
    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        val layoutManager = mRecyclerView!!.layoutManager ?: return false
        val adapter = mRecyclerView!!.adapter ?: return false
        val minFlingVelocity = PagerConfig.flingThreshold
        Loge("minFlingVelocity = $minFlingVelocity")
        return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity) && snapFromFling(layoutManager, velocityX, velocityY)
    }

    /**
     * 快速滚动的具体处理方案
     *
     * @param layoutManager 布局管理器
     * @param velocityX     X 轴滚动速率
     * @param velocityY     Y 轴滚动速率
     * @return 是否消费该事件
     */
    private fun snapFromFling(layoutManager: RecyclerView.LayoutManager, velocityX: Int,
                              velocityY: Int): Boolean {
        if (layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider) {
            return false
        }

        val smoothScroller = createSnapScroller(layoutManager) ?: return false

        val targetPosition = findTargetSnapPosition(layoutManager, velocityX, velocityY)
        if (targetPosition == RecyclerView.NO_POSITION) {
            return false
        }

        smoothScroller.targetPosition = targetPosition
        layoutManager.startSmoothScroll(smoothScroller)
        return true
    }

    /**
     * 通过自定义 LinearSmoothScroller 来控制速度
     *
     * @param layoutManager 布局故哪里去
     * @return 自定义 LinearSmoothScroller
     */
    override fun createSnapScroller(layoutManager: RecyclerView.LayoutManager?): LinearSmoothScroller? {
        return if (layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider) {
            null
        } else PagerGridSmoothScroller(mRecyclerView!!)
    }

    //--- 公开方法 ----------------------------------------------------------------------------------

    /**
     * 设置滚动阀值
     * @param threshold 滚动阀值
     */
    fun setFlingThreshold(threshold: Int) {
        PagerConfig.flingThreshold = threshold
    }
}