package me.bakumon.moneykeeperclone.ui.assets.detail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.database.entity.AssetsModifyRecord
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.bakumon.moneykeeperclone.utill.DateUtils
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class ModifyRecordBinder : ItemViewBinder<AssetsModifyRecord, ModifyRecordBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_assets_record, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: AssetsModifyRecord) {
        val content: String = BigDecimalUtil.fen2Yuan(item.moneyBefore) + " ➡ " + BigDecimalUtil.fen2Yuan(item.money)
        holder.ivTypeImg.setImageResource(ResourcesUtil.getTypeImgId(holder.ivTypeImg.context, "ic_balance"))
        holder.tvTypeName.text = holder.tvTypeName.context.resources.getString(R.string.text_assets_adjust)
        holder.tvRemark.text = content
        holder.tvMoney.visibility = View.GONE
        holder.tvTime.text = DateUtils.date2MonthDay(item.createTime)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTypeImg: ImageView = itemView.findViewById(R.id.ivTypeImg)
        val tvTypeName: TextView = itemView.findViewById(R.id.tvTypeName)
        val tvRemark: TextView = itemView.findViewById(R.id.tvRemark)
        val tvMoney: TextView = itemView.findViewById(R.id.tvMoney)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }
}
