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
package me.bakumon.moneykeeper.ui.statistics.reports

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.database.entity.TypeSumMoneyBean
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.ResourcesUtil
import me.drakeet.multitype.ItemViewBinder
import java.math.BigDecimal

/**
 * @author Bakumon https://bakumon.me
 */
class ReportsViewBinder constructor(private val onItemClickListener: ((TypeSumMoneyBean) -> Unit)) : ItemViewBinder<TypeSumMoneyBean, ReportsViewBinder.ViewHolder>() {

    var colors: List<Int> = arrayListOf()
    private var maxvalueFix: BigDecimal = BigDecimal(0)
    var maxValue: BigDecimal = BigDecimal(0)
        set(value) {
            field = value
            // 和 BarEntryConverter 补偿高度相同逻辑
            maxvalueFix = value.divide(BigDecimal(25), 0, BigDecimal.ROUND_HALF_DOWN)
        }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_report, parent, false)
        return ViewHolder(root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: TypeSumMoneyBean) {
        holder.ivTypeImg.setImageResource(ResourcesUtil.getTypeImgId(holder.ivTypeImg.context, item.imgName))
        holder.tvTypeName.text = item.typeName
        holder.tvSumMoney.text = DefaultSPHelper.symbol + BigDecimalUtil.fen2Yuan(item.typeSumMoney)
        val colorId = if (item.type == RecordType.TYPE_OUTLAY) {
            R.color.colorOutlay
        } else {
            R.color.colorIncome
        }
        holder.tvSumMoney.setTextColor(ContextCompat.getColor(holder.tvSumMoney.context, colorId))
        holder.tvCount.text = item.count.toString() + holder.tvCount.context.getString(R.string.text_unit_account)

        holder.viewLength.layoutParams = LinearLayout.LayoutParams(0, 10,
                item.typeSumMoney.add(maxvalueFix).toFloat())
        holder.viewLengthEnd.layoutParams = LinearLayout.LayoutParams(0, 10,
                maxValue.subtract(item.typeSumMoney.add(maxvalueFix)).toFloat())

        val gradient = holder.viewLength.background as GradientDrawable
        if (colors.size > holder.adapterPosition) {
            gradient.setColor(colors[holder.adapterPosition])
        } else {
            gradient.setColor(ContextCompat.getColor(App.instance, R.color.colorPieChart1))
        }

        holder.llItemReport.setOnClickListener { onItemClickListener.invoke(item) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTypeImg: ImageView = itemView.findViewById(R.id.ivTypeImg)
        val tvTypeName: TextView = itemView.findViewById(R.id.tvTypeName)
        val viewLength: View = itemView.findViewById(R.id.viewLength)
        val viewLengthEnd: View = itemView.findViewById(R.id.viewLengthEnd)
        val tvSumMoney: TextView = itemView.findViewById(R.id.tvSumMoney)
        val tvCount: TextView = itemView.findViewById(R.id.tvCount)
        val llItemReport: LinearLayout = itemView.findViewById(R.id.llItemReport)
    }
}
