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
package me.bakumon.moneykeeper.ui.assets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.Assets
import me.bakumon.moneykeeper.ui.assets.detail.AssetsDetailActivity
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.ResourcesUtil
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
        holder.llItemClick.setOnClickListener { AssetsDetailActivity.open(holder.llItemClick.context, item) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivType: ImageView = itemView.findViewById(R.id.iv_type)
        val tvTypeName: TextView = itemView.findViewById(R.id.tv_type_name)
        val tvRemark: TextView = itemView.findViewById(R.id.tv_remark)
        val tvMoney: TextView = itemView.findViewById(R.id.tv_money)
        val llItemClick: View = itemView.findViewById(R.id.ll_item_click)
    }
}
