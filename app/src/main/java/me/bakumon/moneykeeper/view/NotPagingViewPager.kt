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

package me.bakumon.moneykeeper.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * 可禁止滑动的 ViewPager
 *
 * @author Bakumon https://bakumon.me
 */
class NotPagingViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private var pagingEnabled: Boolean = false

    init {
        this.pagingEnabled = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.pagingEnabled) {
            super.onTouchEvent(event)
        } else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.pagingEnabled) {
            super.onInterceptTouchEvent(event)
        } else false

    }

    fun setPagingEnabled(enabled: Boolean) {
        this.pagingEnabled = enabled
    }
}
