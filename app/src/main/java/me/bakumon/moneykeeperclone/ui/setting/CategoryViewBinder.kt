package me.bakumon.moneykeeperclone.ui.setting

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import me.bakumon.moneykeeperclone.R
import me.drakeet.multitype.ItemViewBinder


/**
 * @author Bakumon https://bakumon.me
 */
class CategoryViewBinder : ItemViewBinder<Category, CategoryViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_setting_category, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, category: Category) {
        holder.tvCategory.text = category.name
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
    }

}
