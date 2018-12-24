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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.AssetsTransferRecordWithAssets
import me.bakumon.moneykeeper.ui.add.AddRecordActivity
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.DateUtils
import me.bakumon.moneykeeper.utill.ResourcesUtil
import me.bakumon.moneykeeper.utill.ToastUtils
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class TransferRecordBinder constructor(private val onDeleteClickListener: ((AssetsTransferRecordWithAssets) -> Unit)) :
    ItemViewBinder<AssetsTransferRecordWithAssets, TransferRecordBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_assets_record, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: AssetsTransferRecordWithAssets) {
        val content: String = item.assetsNameFrom + " ➡ " + item.assetsNameTo
        holder.ivTypeImg.setImageResource(ResourcesUtil.getTypeImgId(holder.ivTypeImg.context, "ic_transform"))
        holder.tvTypeName.text = holder.tvTypeName.context.resources.getString(R.string.text_assets_transfer)
        holder.tvRemark.text = content
        holder.tvSubtitle.visibility = View.VISIBLE
        holder.tvSubtitle.text = item.remark
        holder.tvMoney.visibility = View.VISIBLE
        holder.tvMoney.text = BigDecimalUtil.fen2Yuan(item.money)
        holder.tvTime.text = DateUtils.date2MonthDay(item.time)

        holder.llItemClick.setOnClickListener {
            if (DefaultSPHelper.isLockRecord) {
                ToastUtils.show(R.string.toast_lock_record)
            } else {
                AddRecordActivity.open(holder.llItemClick.context, isTransfer = true, transferRecord = item)
            }
        }

        holder.llItemClick.setOnLongClickListener {
            if (DefaultSPHelper.isLockRecord) {
                ToastUtils.show(R.string.toast_lock_record)
            } else {
                showOperateDialog(holder.tvMoney.context, item)
            }
            true
        }
    }

    private fun showOperateDialog(context: Context, item: AssetsTransferRecordWithAssets) {
        val money = " (" + DefaultSPHelper.symbol + BigDecimalUtil.fen2Yuan(item.money) + ")"
        MaterialDialog(context)
            .title(text = context.getString(R.string.text_transfer) + money)
            .message(R.string.text_delete_record_note)
            .negativeButton(R.string.text_cancel)
            .positiveButton(R.string.text_affirm_delete) { onDeleteClickListener.invoke(item) }
            .show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llItemClick: LinearLayout = itemView.findViewById(R.id.llItemClick)
        val ivTypeImg: ImageView = itemView.findViewById(R.id.ivTypeImg)
        val tvTypeName: TextView = itemView.findViewById(R.id.tvTypeName)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvRemark: TextView = itemView.findViewById(R.id.tvRemark)
        val tvMoney: TextView = itemView.findViewById(R.id.tvMoney)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }
}
