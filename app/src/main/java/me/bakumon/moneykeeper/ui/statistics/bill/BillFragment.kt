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

package me.bakumon.moneykeeper.ui.statistics.bill

import android.os.Bundle
import android.view.Gravity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_bill.*
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.base.ErrorResource
import me.bakumon.moneykeeper.base.SuccessResource
import me.bakumon.moneykeeper.database.entity.RecordForList
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.ui.common.BaseFragment
import me.bakumon.moneykeeper.ui.common.Empty
import me.bakumon.moneykeeper.ui.common.EmptyViewBinder
import me.bakumon.moneykeeper.ui.home.RecordsViewBinder
import me.bakumon.moneykeeper.utill.DateUtils
import me.bakumon.moneykeeper.utill.ToastUtils
import me.bakumon.moneykeeper.widget.WidgetProvider
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import java.util.*

/**
 * 统计-账单
 *
 * @author Bakumon https://bakumon.me
 */
class BillFragment : BaseFragment() {
    private lateinit var mViewModel: BillViewModel
    private lateinit var adapter: MultiTypeAdapter
    private var mYear: Int = DateUtils.getCurrentYear()
    private var mMonth: Int = DateUtils.getCurrentMonth()
    private var mType: Int = RecordType.TYPE_OUTLAY

    private lateinit var dateFrom: Date
    private lateinit var dateTo: Date

    override val layoutId: Int
        get() = R.layout.fragment_bill

    override fun onInit(savedInstanceState: Bundle?) {
        mViewModel = getViewModel()

        adapter = MultiTypeAdapter()
        adapter.register(RecordForList::class, RecordsViewBinder { deleteRecord(it) })
        adapter.register(Empty::class, EmptyViewBinder())
        rvRecordBill.adapter = adapter

        dateFrom = arguments?.getSerializable(KEY_DATE_FROM) as Date
        dateTo = arguments?.getSerializable(KEY_DATE_TO) as Date

        sumMoneyChooseView.setOnCheckedChangeListener {
            mType = it
            updateData()
        }
    }

    override fun lazyInitData() {
        sumMoneyChooseView.checkItem(mType)
    }

    private fun updateData() {
        getOrderData()
        getDaySumData()
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

    private fun deleteRecord(record: RecordForList) {
        mViewModel.deleteRecord(record).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    // 更新 widget
                    if (context != null) {
                        WidgetProvider.updateWidget(context!!)
                    }
                }
                is ErrorResource<Boolean> -> {
                    ToastUtils.show(R.string.toast_record_delete_fail)
                }
            }
        })
    }

    private fun getOrderData() {
        mViewModel.getRecordForListWithTypes(dateFrom, dateTo, mType).observe(this, Observer {
            if (it != null) {
                setItems(it)
            }
        })
    }

    private fun setItems(beans: List<RecordForList>) {
        val items = Items()
        if (beans.isEmpty()) {
            items.add(Empty(getString(R.string.text_empty_tip), Gravity.CENTER_HORIZONTAL))
        } else {
            items.addAll(beans)
        }
        adapter.items = items
        adapter.notifyDataSetChanged()
    }

    private fun getDaySumData() {
        mViewModel.getDaySumMoney(dateFrom, dateTo, mType).observe(this, Observer {
            // TODO 柱形图自定义月份
            barChart.setChartData(it, mYear, mMonth)
        })
    }

    private fun getMonthSumMoney() {
        mViewModel.getMonthSumMoney(dateFrom, dateTo).observe(this, Observer {
            sumMoneyChooseView.setSumMoneyBean(it)
        })
    }

    companion object {
        private const val KEY_DATE_FROM = "KEY_DATE_FROM"
        private const val KEY_DATE_TO = "KEY_DATE_TO"
        fun newInstance(dateFrom: Date, dateTo: Date): BillFragment {
            val fragment = BillFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_DATE_FROM, dateFrom)
            bundle.putSerializable(KEY_DATE_TO, dateTo)
            fragment.arguments = bundle
            return fragment
        }
    }
}
