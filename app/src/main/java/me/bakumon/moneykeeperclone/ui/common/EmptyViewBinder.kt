package me.bakumon.moneykeeperclone.ui.common

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import me.bakumon.moneykeeperclone.R
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class EmptyViewBinder : ItemViewBinder<Empty, EmptyViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.layout_empty, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Empty) {
        holder.llEmpty.gravity = item.gravity
        holder.tvEmpty.text = item.tipText
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llEmpty: LinearLayout = itemView.findViewById(R.id.ll_empty)
        val tvEmpty: TextView = itemView.findViewById(R.id.tv_empty)
    }
}
