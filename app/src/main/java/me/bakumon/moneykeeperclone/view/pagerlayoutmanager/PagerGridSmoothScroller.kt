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
import android.util.DisplayMetrics
import android.view.View

import me.bakumon.moneykeeperclone.view.pagerlayoutmanager.PagerConfig.Logi

/**
 * 作用：用于处理平滑滚动
 * 作者：GcsSloop
 * 摘要：用于用户手指抬起后页面对齐或者 Fling 事件。
 */
class PagerGridSmoothScroller(private val mRecyclerView: RecyclerView) : LinearSmoothScroller(mRecyclerView.context) {

    override fun onTargetFound(targetView: View, state: RecyclerView.State?, action: RecyclerView.SmoothScroller.Action) {
        val manager = mRecyclerView.layoutManager ?: return
        if (manager is PagerGridLayoutManager) {
            val pos = mRecyclerView.getChildAdapterPosition(targetView)
            val snapDistances = manager.getSnapOffset(pos)
            val dx = snapDistances[0]
            val dy = snapDistances[1]
            Logi("dx = $dx")
            Logi("dy = $dy")
            val time = calculateTimeForScrolling(Math.max(Math.abs(dx), Math.abs(dy)))
            if (time > 0) {
                action.update(dx, dy, time, mDecelerateInterpolator)
            }
        }
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return PagerConfig.millisecondsPreInch / displayMetrics.densityDpi
    }
}
