package me.bakumon.moneykeeperclone.ui.add

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.database.entity.Assets
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class AssetsChooseViewBinder constructor(private val onClickListener: ((Assets) -> Unit)) : ItemViewBinder<Assets, AssetsChooseViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_assets_choose, parent, false)
        return ViewHolder(root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: Assets) {

        holder.ivAccount.setImageResource(ResourcesUtil.getTypeImgId(holder.ivAccount.context, item.imgName))
        holder.tvAccountName.text = item.name
        if (item.remark.isBlank()) {
            holder.tvAccountRemark.text = ""
            holder.tvAccountRemark.visibility = View.GONE
        } else {
            holder.tvAccountRemark.text = item.remark
            holder.tvAccountRemark.visibility = View.VISIBLE
        }
        holder.tvMoney.text = ConfigManager.symbol + BigDecimalUtil.fen2Yuan(item.money)
        holder.llAccount.setOnClickListener {
            onClickListener.invoke(item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAccount: ImageView = itemView.findViewById(R.id.ivAccount)
        val tvAccountName: TextView = itemView.findViewById(R.id.tvAccountName)
        val tvAccountRemark: TextView = itemView.findViewById(R.id.tvAccountRemark)
        val tvMoney: TextView = itemView.findViewById(R.id.tvMoney)
        val llAccount: View = itemView.findViewById(R.id.llAccount)
    }
}
