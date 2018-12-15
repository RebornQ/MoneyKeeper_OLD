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
package me.bakumon.moneykeeper.ui.common

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import me.drakeet.multitype.MultiTypeAdapter
import java.util.*

class SortDragCallback constructor(
    private val adapter: MultiTypeAdapter,
    private val onMovedCallBack: (() -> Unit)? = null
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    ) {

        // 转换数据顺序
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition

        if (from < to) {
            for (i in from until to) {
                Collections.swap(adapter.items, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(adapter.items, i, i - 1)
            }
        }
        adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        onMovedCallBack?.invoke()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

}