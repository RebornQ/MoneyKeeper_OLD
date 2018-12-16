package me.bakumon.moneykeeperclone.ui.assets.add

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class BankViewBinder constructor(private val onClickListener: ((Bank) -> Unit)) : ItemViewBinder<Bank, BankViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_bank, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Bank) {

        holder.ivBank.setImageResource(ResourcesUtil.getTypeImgId(holder.ivBank.context, item.imgName))
        holder.ivBankName.text = item.name

        holder.llItemClick.setOnClickListener {
            onClickListener.invoke(item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBank: ImageView = itemView.findViewById(R.id.ivBank)
        val ivBankName: TextView = itemView.findViewById(R.id.ivBankName)
        val llItemClick: View = itemView.findViewById(R.id.ll_item_click)
    }
}
