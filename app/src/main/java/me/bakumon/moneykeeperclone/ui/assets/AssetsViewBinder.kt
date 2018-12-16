package me.bakumon.moneykeeperclone.ui.assets

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.database.entity.Assets
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.floo.Floo
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class AssetsViewBinder : ItemViewBinder<Assets, AssetsViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_assets, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Assets) {

        // 类型图标
        holder.ivType.setImageResource(ResourcesUtil.getTypeImgId(holder.ivType.context, item.imgName))
        holder.tvTypeName.text = item.name
        if (item.remark.isBlank()) {
            holder.tvRemark.visibility = View.GONE
            holder.tvRemark.text = ""
        } else {
            holder.tvRemark.visibility = View.VISIBLE
            holder.tvRemark.text = item.remark
        }

        holder.tvMoney.text = BigDecimalUtil.fen2Yuan(item.money)

        holder.llItemClick.setOnClickListener {
            Floo.navigation(holder.llItemClick.context, Router.Url.URL_ASSETS_DETAIL)
                    .putExtra(Router.ExtraKey.KEY_ASSETS, item)
                    .start()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivType: ImageView = itemView.findViewById(R.id.iv_type)
        val tvTypeName: TextView = itemView.findViewById(R.id.tv_type_name)
        val tvRemark: TextView = itemView.findViewById(R.id.tv_remark)
        val tvMoney: TextView = itemView.findViewById(R.id.tv_money)
        val llItemClick: View = itemView.findViewById(R.id.ll_item_click)
    }
}
