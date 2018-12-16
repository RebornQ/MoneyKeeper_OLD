package me.bakumon.moneykeeperclone.ui.review

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class ReviewViewBinder : ItemViewBinder<ReviewItemBean, ReviewViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_review, parent, false)
        return ViewHolder(root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: ReviewItemBean) {
        holder.tvMonth.text = item.month + holder.tvMonth.context.getString(R.string.text_month)
        holder.tvYear.text = item.year + holder.tvYear.context.getString(R.string.text_year)
        holder.tvOutlay.text = ConfigManager.symbol + BigDecimalUtil.fen2Yuan(item.outlay)
        holder.tvIncome.text = ConfigManager.symbol + BigDecimalUtil.fen2Yuan(item.income)
        holder.tvOverage.text = ConfigManager.symbol + BigDecimalUtil.fen2Yuan(item.overage)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvYear: TextView = itemView.findViewById(R.id.tvYear)
        val tvOutlay: TextView = itemView.findViewById(R.id.tvOutlay)
        val tvIncome: TextView = itemView.findViewById(R.id.tvIncome)
        val tvOverage: TextView = itemView.findViewById(R.id.tvOverage)
    }
}
