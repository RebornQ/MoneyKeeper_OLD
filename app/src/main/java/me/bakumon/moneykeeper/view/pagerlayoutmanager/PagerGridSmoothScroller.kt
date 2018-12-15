/*
 * Copyright 2017 GcsSloop
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
 * limitations under the License.
 *
 * Last modified 2017-09-20 16:32:43
 *
 * GitHub: https://github.com/GcsSloop
 * WeiBo: http://weibo.com/GcsSloop
 * WebSite: http://www.gcssloop.com
 */

package me.bakumon.moneykeeper.view.pagerlayoutmanager

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

import me.bakumon.moneykeeper.view.pagerlayoutmanager.PagerConfig.Logi

/**
 * 作用：用于处理平滑滚动
 * 作者：GcsSloop
 * 摘要：用于用户手指抬起后页面对齐或者 Fling 事件。
 *
 * 修改说明：将java代码转换为了kotlin，并将 support 依赖改成了 androidx
 */
class PagerGridSmoothScroller(private val mRecyclerView: RecyclerView) : LinearSmoothScroller(mRecyclerView.context) {

    override fun onTargetFound(targetView: View, state: RecyclerView.State, action: RecyclerView.SmoothScroller.Action) {
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
