package me.bakumon.moneykeeperclone.ui.about

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
class CardWithActionViewBinder : ItemViewBinder<CardWithAction, CardWithActionViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_about_card_with_action, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: CardWithAction) {
        holder.tvContent.text = item.content
        holder.tvAction.text = item.actionText
        holder.tvAction.setOnClickListener { item.action.invoke() }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvContent: TextView = itemView.findViewById(R.id.content)
        var tvAction: TextView = itemView.findViewById(R.id.tvAction)
    }

}
