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
package me.bakumon.moneykeeper.ui.add

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.Assets
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.ResourcesUtil
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class AssetsChooseViewBinder constructor(private val onClickListener: ((Assets) -> Unit)) :
    ItemViewBinder<Assets, AssetsChooseViewBinder.ViewHolder>() {

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
        holder.tvMoney.text = DefaultSPHelper.symbol + BigDecimalUtil.fen2Yuan(item.money)
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
