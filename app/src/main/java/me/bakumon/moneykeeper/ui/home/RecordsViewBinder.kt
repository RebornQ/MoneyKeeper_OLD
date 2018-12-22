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
package me.bakumon.moneykeeper.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.RecordForList
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.DateUtils
import me.bakumon.moneykeeper.utill.ResourcesUtil
import me.bakumon.moneykeeper.utill.ToastUtils
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class RecordsViewBinder constructor(private val onDeleteClickListener: ((RecordForList) -> Unit)) :
    ItemViewBinder<RecordForList, RecordsViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_record, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: RecordForList) {
        // 时间
        val isDataShow = getPosition(holder) == 0 || !DateUtils.isSameDay(
            item.time!!,
            (adapter.items[getPosition(holder) - 1] as RecordForList).time!!
        )
        holder.tvTime.visibility = if (isDataShow) View.VISIBLE else View.GONE
        holder.tvTime.text = DateUtils.date2MonthDay(item.time!!)

        // 类型图标
        holder.ivType.setImageResource(ResourcesUtil.getTypeImgId(holder.ivType.context, item.typeImgName))

        // 类型名称
        holder.tvTypeName.text = item.typeName

        // 资产名称
        holder.tvAssetName.text = item.assetsName
        holder.tvAssetName.visibility = if (item.assetsName.isNullOrEmpty()) View.GONE else View.VISIBLE

        // 备注
        holder.tvRemark.text = item.remark
        holder.tvRemark.visibility = if (item.remark.isNullOrEmpty()) View.GONE else View.VISIBLE

        // 费用
        val money = if (item.type == RecordType.TYPE_OUTLAY) {
            holder.tvMoney.setTextColor(ContextCompat.getColor(holder.tvMoney.context, R.color.colorOutlay))
            "-" + BigDecimalUtil.fen2Yuan(item.money)
        } else {
            holder.tvMoney.setTextColor(ContextCompat.getColor(holder.tvMoney.context, R.color.colorIncome))
            "+" + BigDecimalUtil.fen2Yuan(item.money)
        }
        holder.tvMoney.text = money

        holder.llItemClick.setOnClickListener {
            //                        AddRecordActivity.open(holder.llItemClick.context, record = item)
            // TODO 跳转
            ToastUtils.show("TODO 跳转")
        }

        holder.llItemClick.setOnLongClickListener {
            showOperateDialog(holder.tvMoney.context, item)
            true
        }
    }

    private fun showOperateDialog(context: Context, record: RecordForList) {
        val money = " (" + DefaultSPHelper.symbol + BigDecimalUtil.fen2Yuan(record.money) + ")"
        MaterialDialog(context)
            .title(text = record.typeName + money)
            .message(R.string.text_delete_record_note)
            .negativeButton(R.string.text_cancel)
            .positiveButton(R.string.text_affirm_delete) { onDeleteClickListener.invoke(record) }
            .show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val ivType: ImageView = itemView.findViewById(R.id.iv_type)
        val tvTypeName: TextView = itemView.findViewById(R.id.tv_type_name)
        val tvRemark: TextView = itemView.findViewById(R.id.tv_remark)
        val tvMoney: TextView = itemView.findViewById(R.id.tv_money)
        val tvAssetName: TextView = itemView.findViewById(R.id.tv_asset_name)
        val llItemClick: View = itemView.findViewById(R.id.ll_item_click)
    }
}
