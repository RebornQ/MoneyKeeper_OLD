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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.MonthSumMoneyBean
import me.bakumon.moneykeeper.ui.common.BaseActivity
import me.bakumon.moneykeeper.ui.common.Empty
import me.bakumon.moneykeeper.ui.common.EmptyViewBinder
import me.bakumon.moneykeeper.utill.DateUtils
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * 回顾
 * 查看年账单或某几个月的数据
 *
 * @author Bakumon https://bakumon
 */
class ReviewActivity : BaseActivity() {

    private lateinit var mViewModel: ReviewViewModel
    private lateinit var mAdapter: MultiTypeAdapter

    private var mCurrentYear = DateUtils.getCurrentYear()

    override val layoutId: Int
        get() = R.layout.activity_review

    override fun onInitView(savedInstanceState: Bundle?) {
        toolbarLayout.tvTitle.text = mCurrentYear.toString()
        toolbarLayout.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
        toolbarLayout.tvTitle.compoundDrawablePadding = 10
        toolbarLayout.tvTitle.setOnClickListener { chooseYear() }
        setSupportActionBar(toolbarLayout as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sumMoneyView.setCheckNotEnable()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        mAdapter = MultiTypeAdapter()
        mAdapter.register(ReviewItemBean::class, ReviewViewBinder())
        mAdapter.register(Empty::class, EmptyViewBinder())
        rvReview.adapter = mAdapter

        mViewModel = getViewModel()
        updateData(mCurrentYear)
    }

    private fun chooseYear() {
        toolbarLayout.tvTitle.isEnabled = false
        val chooseYearDialog = ChooseYearDialog(this, mCurrentYear)
        chooseYearDialog.mOnDismissListener = {
            toolbarLayout.tvTitle.isEnabled = true
        }
        chooseYearDialog.mOnChooseListener = { year ->
            mCurrentYear = year
            toolbarLayout.tvTitle.text = mCurrentYear.toString()
            updateData(mCurrentYear)
        }
        chooseYearDialog.show()
    }

    private fun updateData(year: Int) {
        getYearSumMoney(year)
        getMonthOfYearSumMoney(year)
    }

    private fun getYearSumMoney(year: Int) {
        mViewModel.getYearSumMoney(year).observe(this, Observer {
            sumMoneyView.setSumMoneyBean(it)

        })
    }

    private fun getMonthOfYearSumMoney(year: Int) {
        mViewModel.getMonthOfYearSumMoney(year).observe(this, Observer {
            if (it != null) {
                lineChart.setLineChartData(it)
                setItems(it)
            }
        })
    }

    private fun setItems(beans: List<MonthSumMoneyBean>) {
        val items = Items()
        if (beans.isEmpty()) {
            items.add(Empty(getString(R.string.text_empty_tip), Gravity.CENTER_HORIZONTAL))
        } else {
            items.addAll(ReviewItemDataConverter.getBarEntryList(beans))
        }
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }

    companion object {
        fun open(context: Context) {
            context.startActivity(Intent(context, ReviewActivity::class.java))
        }
    }
}
