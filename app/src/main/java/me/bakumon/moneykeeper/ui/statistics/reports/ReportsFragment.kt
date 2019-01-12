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

import android.os.Bundle
import android.view.Gravity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_reports.*
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.database.entity.TypeSumMoneyBean
import me.bakumon.moneykeeper.ui.common.BaseFragment
import me.bakumon.moneykeeper.ui.common.Empty
import me.bakumon.moneykeeper.ui.common.EmptyViewBinder
import me.bakumon.moneykeeper.ui.statistics.reports.piechart.PieColorsCreator
import me.bakumon.moneykeeper.ui.statistics.reports.piechart.PieEntryConverter
import me.bakumon.moneykeeper.ui.typerecords.TypeRecordsActivity
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import java.util.*

/**
 * 统计-报表
 *
 * @author Bakumon https://bakumon.me
 */
class ReportsFragment : BaseFragment() {
    private lateinit var mViewModel: ReportsViewModel
    private lateinit var adapter: MultiTypeAdapter

    private var mType: Int = RecordType.TYPE_OUTLAY
    private lateinit var dateFrom: Date
    private lateinit var dateTo: Date

    override val layoutId: Int
        get() = R.layout.fragment_reports

    override fun onInit(savedInstanceState: Bundle?) {

        mViewModel = getViewModel()

        adapter = MultiTypeAdapter()
        adapter.register(Empty::class, EmptyViewBinder())
        rvReports.adapter = adapter

        dateFrom = arguments?.getSerializable(KEY_DATE_FROM) as Date
        dateTo = arguments?.getSerializable(KEY_DATE_TO) as Date

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
     * 设置日期范围
     * 父 activity 调用
     */
    fun setMonthRange(dateFrom: Date, dateTo: Date) {
        this.dateFrom = dateFrom
        this.dateTo = dateTo
        // 更新数据
        updateData()
    }

    private fun navTypeRecords(typeName: String, typeId: Int) {
        if (context != null) {
            TypeRecordsActivity.open(
                context!!,
                name = typeName,
                recordType = mType,
                recordTypeId = typeId,
                dateFrom = dateFrom,
                dateTo = dateTo
            )
        }
    }

    private fun getMonthSumMoney() {
        mViewModel.getMonthSumMoney(dateFrom, dateTo).observe(this, Observer {
            sumMoneyChooseView.setSumMoneyBean(it)
        })
    }

    private fun getTypeSumMoney() {
        mViewModel.getTypeSumMoney(dateFrom, dateTo, mType).observe(this, Observer {
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

    companion object {
        private const val KEY_DATE_FROM = "KEY_DATE_FROM"
        private const val KEY_DATE_TO = "KEY_DATE_TO"
        fun newInstance(dateFrom: Date, dateTo: Date): ReportsFragment {
            val fragment = ReportsFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_DATE_FROM, dateFrom)
            bundle.putSerializable(KEY_DATE_TO, dateTo)
            fragment.arguments = bundle
            return fragment
        }
    }
}
