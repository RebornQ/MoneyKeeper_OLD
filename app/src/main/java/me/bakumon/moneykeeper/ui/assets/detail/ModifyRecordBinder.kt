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
package me.bakumon.moneykeeper.ui.assets.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.AssetsModifyRecord
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.DateUtils
import me.bakumon.moneykeeper.utill.ResourcesUtil
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
