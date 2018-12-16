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

package me.bakumon.moneykeeperclone.ui.statistics

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.layout_sum_money_choose.view.*
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.database.entity.SumMoneyBean
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import java.math.BigDecimal

/**
 * 汇总数据
 * 支出 收入 结余
 *
 * @author Bakumon https://bakumon.me
 */
class SumMoneyChooseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private val inflater = LayoutInflater.from(context)

    private var onCheckedChangeListener: ((Int) -> Unit)? = null

    init {
        orientation = LinearLayout.HORIZONTAL
        inflater.inflate(R.layout.layout_sum_money_choose, this, true)
        rgType.setOnCheckedChangeListener { _, checkedId ->
            val type = if (checkedId == R.id.rbOutlay) {
                RecordType.TYPE_OUTLAY
            } else {
                RecordType.TYPE_INCOME
            }
            onCheckedChangeListener?.invoke(type)
        }
    }

    fun setOnCheckedChangeListener(listener: ((Int) -> Unit)) {
        onCheckedChangeListener = listener
    }

    fun checkItem(type: Int) {
        if (type == RecordType.TYPE_OUTLAY) {
            rgType.check(R.id.rbOutlay)
        } else {
            rgType.check(R.id.rbIncome)
        }
    }

    fun setCheckNotEnable() {
        rbOutlay.isClickable = false
        rbOutlay.buttonDrawable = null
        rbOutlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bg_dot_red, 0, 0, 0)
        rbOutlay.compoundDrawablePadding = 10

        rbIncome.isClickable = false
        rbIncome.buttonDrawable = null
        rbIncome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bg_dot_green, 0, 0, 0)
        rbIncome.compoundDrawablePadding = 10
    }

    @SuppressLint("SetTextI18n")
    fun setSumMoneyBean(sumMoneyBean: List<SumMoneyBean>?) {
        if (sumMoneyBean == null) {
            return
        }
        var outlay = BigDecimal(0)
        var income = BigDecimal(0)
        if (sumMoneyBean.isNotEmpty()) {
            for ((type, sumMoney) in sumMoneyBean) {
                if (type == RecordType.TYPE_OUTLAY) {
                    outlay = sumMoney
                } else {
                    income = sumMoney
                }
            }
        }
        rbOutlay.text = context.getString(R.string.text_outlay) + " " + ConfigManager.symbol + BigDecimalUtil.fen2YuanWithText(outlay)
        rbIncome.text = context.getString(R.string.text_income) + " " + ConfigManager.symbol + BigDecimalUtil.fen2YuanWithText(income)

        if (income > BigDecimal(0)) {
            tvOverage.visibility = View.VISIBLE
            val prefix2 = tvOverage.context.getString(R.string.text_overage) + " " + ConfigManager.symbol
            val overage = prefix2 + BigDecimalUtil.fen2YuanWithText(income.subtract(outlay))
            tvOverage.text = overage
        } else {
            tvOverage.visibility = View.GONE
        }
    }
}
