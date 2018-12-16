package me.bakumon.moneykeeperclone.ui.setting

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.R
import me.drakeet.multitype.ItemViewBinder


/**
 * @author Bakumon https://bakumon.me
 */
class NormalItemViewBinder constructor(private val onNormalItemClickListener: (NormalItem, View) -> Unit) : ItemViewBinder<NormalItem, NormalItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_setting_normal, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NormalItem) {
        holder.tvTitle.text = item.title
        holder.tvTitle.visibility = if (item.title.isEmpty()) View.GONE else View.VISIBLE
        holder.tvContent.text = item.content
        holder.tvContent.visibility = if (item.content.isEmpty()) View.GONE else View.VISIBLE
        holder.llItemSettingNormal.setOnClickListener { onNormalItemClickListener.invoke(item, holder.tvTitle) }
        if (item.clickEnable) {
            holder.llItemSettingNormal.isClickable = true
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.tvTitle.context, R.color.colorTextSettingItemTitle))
            holder.tvContent.setTextColor(ContextCompat.getColor(holder.tvContent.context, R.color.colorTextSettingItemContent))
        } else {
            holder.llItemSettingNormal.isClickable = false
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.tvTitle.context, R.color.colorTextDisable))
            holder.tvContent.setTextColor(ContextCompat.getColor(holder.tvContent.context, R.color.colorTextDisable))
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvContent: TextView = itemView.findViewById(R.id.tvContent)
        var llItemSettingNormal: LinearLayout = itemView.findViewById(R.id.llItemSettingNormal)
    }

}
