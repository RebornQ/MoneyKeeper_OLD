package me.bakumon.moneykeeperclone.ui.common

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import me.drakeet.multitype.MultiTypeAdapter
import java.util.*

class SortDragCallback constructor(private val adapter: MultiTypeAdapter, private val onMovedCallBack: (() -> Unit)? = null) : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {

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

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {

    }

    override fun canDropOver(recyclerView: RecyclerView?, current: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return true
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

}