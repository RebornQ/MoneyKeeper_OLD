/*
 * Copyright 2018 Bakumon. https://github.com/Bakumon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.bakumon.moneykeeper.ui.review

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.utill.BigDecimalUtil
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
        holder.tvOutlay.text = DefaultSPHelper.symbol + BigDecimalUtil.fen2Yuan(item.outlay)
        holder.tvIncome.text = DefaultSPHelper.symbol + BigDecimalUtil.fen2Yuan(item.income)
        holder.tvOverage.text = DefaultSPHelper.symbol + BigDecimalUtil.fen2Yuan(item.overage)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvYear: TextView = itemView.findViewById(R.id.tvYear)
        val tvOutlay: TextView = itemView.findViewById(R.id.tvOutlay)
        val tvIncome: TextView = itemView.findViewById(R.id.tvIncome)
        val tvOverage: TextView = itemView.findViewById(R.id.tvOverage)
    }
}
