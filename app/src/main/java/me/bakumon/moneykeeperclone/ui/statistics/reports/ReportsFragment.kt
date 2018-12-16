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

package me.bakumon.moneykeeperclone.ui.statistics.reports

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.Gravity
import kotlinx.android.synthetic.main.fragment_reports.*
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.database.entity.TypeSumMoneyBean
import me.bakumon.moneykeeperclone.ui.common.BaseFragment
import me.bakumon.moneykeeperclone.ui.common.Empty
import me.bakumon.moneykeeperclone.ui.common.EmptyViewBinder
import me.bakumon.moneykeeperclone.ui.statistics.reports.piechart.PieColorsCreator
import me.bakumon.moneykeeperclone.ui.statistics.reports.piechart.PieEntryConverter
import me.bakumon.moneykeeperclone.utill.DateUtils
import me.drakeet.floo.Floo
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * 统计-报表
 *
 * @author Bakumon https://bakumon.me
 */
class ReportsFragment : BaseFragment() {
    private lateinit var mViewModel: ReportsViewModel
    private lateinit var adapter: MultiTypeAdapter

    private var mYear: Int = DateUtils.getCurrentYear()
    private var mMonth: Int = DateUtils.getCurrentMonth()
    private var mType: Int = RecordType.TYPE_OUTLAY

    override val layoutId: Int
        get() = R.layout.fragment_reports

    override fun onInit(savedInstanceState: Bundle?) {

        mViewModel = getViewModel()

        adapter = MultiTypeAdapter()

        adapter.register(Empty::class, EmptyViewBinder())
        rvReports.adapter = adapter

        pieChart.setOnValueClickListener { typeName, typeId -> navTypeRecords(typeName, typeId) }

        sumMoneyChooseView.setOnCheckedChangeListener {
            mType = it
            updateData()
        }
    }

    override fun lazyInitData() {
        sumMoneyChooseView.checkItem(mType)
    }

    private fun updateData() {
        getTypeSumMoney()
        getMonthSumMoney()
    }

    /**
     * 设置月份
     */
    fun setYearMonth(year: Int, month: Int) {
        if (year == mYear && month == mMonth) {
            return
        }
        mYear = year
        mMonth = month
        // 更新数据
        updateData()
    }

    private fun navTypeRecords(typeName: String, typeId: Int) {
        if (context != null) {
            Floo.navigation(context!!, Router.Url.URL_TYPE_RECORDS)
                    .putExtra(Router.ExtraKey.KEY_TYPE_NAME, typeName)
                    .putExtra(Router.ExtraKey.KEY_RECORD_TYPE, mType)
                    .putExtra(Router.ExtraKey.KEY_RECORD_TYPE_ID, typeId)
                    .putExtra(Router.ExtraKey.KEY_YEAR, mYear)
                    .putExtra(Router.ExtraKey.KEY_MONTH, mMonth)
                    .start()
        }
    }

    private fun getMonthSumMoney() {
        mViewModel.getMonthSumMoney(mYear, mMonth).observe(this, Observer {
            sumMoneyChooseView.setSumMoneyBean(it)
        })
    }

    private fun getTypeSumMoney() {
        mViewModel.getTypeSumMoney(mYear, mMonth, mType).observe(this, Observer {
            if (it != null) {
                pieChart.setChartData(it)
                setItems(it)
            }
        })
    }

    private fun setItems(beans: List<TypeSumMoneyBean>) {
        val viewBinder = ReportsViewBinder { navTypeRecords(it.typeName, it.typeId) }
        viewBinder.colors = PieColorsCreator.colors(context!!, beans.size)
        viewBinder.maxValue = PieEntryConverter.getMax(beans)
        adapter.register(TypeSumMoneyBean::class, viewBinder)
        val items = Items()
        if (beans.isEmpty()) {
            items.add(Empty(getString(R.string.text_empty_tip), Gravity.CENTER_HORIZONTAL))
        } else {
            items.addAll(beans)
        }
        adapter.items = items
        adapter.notifyDataSetChanged()
    }
}
