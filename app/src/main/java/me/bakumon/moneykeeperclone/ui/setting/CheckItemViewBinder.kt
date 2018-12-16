package me.bakumon.moneykeeperclone.ui.setting

import android.support.v7.widget.AppCompatCheckBox
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
class CheckItemViewBinder constructor(private val onCheckItemCheckChange: (CheckItem, Boolean) -> Unit) : ItemViewBinder<CheckItem, CheckItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_setting_check, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: CheckItem) {
        holder.tvTitle.text = item.title
        holder.tvContent.text = item.content
        holder.checkBox.isChecked = item.isCheck
        holder.checkBox.setOnCheckedChangeListener { _, b -> onCheckItemCheckChange.invoke(item, b) }
        holder.llItemSettingNormal.setOnClickListener { holder.checkBox.isChecked = !holder.checkBox.isChecked }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvContent: TextView = itemView.findViewById(R.id.tvContent)
        var checkBox: AppCompatCheckBox = itemView.findViewById(R.id.checkBox)
        var llItemSettingNormal: LinearLayout = itemView.findViewById(R.id.llItemSettingCheck)
    }

}
