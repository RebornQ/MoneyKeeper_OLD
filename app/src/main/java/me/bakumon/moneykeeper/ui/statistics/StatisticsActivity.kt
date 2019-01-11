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

package me.bakumon.moneykeeper.ui.statistics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_statistics.*
import kotlinx.android.synthetic.main.layout_tool_bar_with_arrow_sides.view.*
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.ui.common.AbsTwoTabActivity
import me.bakumon.moneykeeper.ui.review.ReviewActivity
import me.bakumon.moneykeeper.ui.statistics.bill.BillFragment
import me.bakumon.moneykeeper.ui.statistics.reports.ReportsFragment
import me.bakumon.moneykeeper.utill.DateTimeUtil
import java.util.*

/**
 * 统计
 *
 * @author Bakumon https://bakumon
 */
class StatisticsActivity : AbsTwoTabActivity() {

    private lateinit var mBillFragment: BillFragment
    private lateinit var mReportsFragment: ReportsFragment

    /**
     * 自定义月份偏移
     */
    private var beforeOffset: Long = 0

    override val layoutId: Int
        get() = R.layout.activity_statistics

    override fun onInitView(savedInstanceState: Bundle?) {
        val toolbar: Toolbar = findViewById(R.id.toolbarLayout)
        onSetupTitle(toolbar.tvTitle)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbarLayout.ivRight.isEnabled = false
        toolbar.ivLeft.setOnClickListener {
            beforeOffset += 1
            onChooseCustomMonth()
        }
        toolbar.ivRight.setOnClickListener {
            beforeOffset -= 1
            onChooseCustomMonth()
        }
    }

    override fun onSetupTitle(tvTitle: TextView) {
        beforeOffset = 0
        val (_, _, displayDate: String) = DateTimeUtil.getCustomMonth(beforeOffset)
        tvTitle.text = displayDate
    }

    override fun getTwoTabText(): ArrayList<String> {
        return arrayListOf(getString(R.string.text_order), getString(R.string.text_reports))
    }

    override fun getTwoFragments(): ArrayList<Fragment> {
        val (startDate: Date, endDate: Date, _) = DateTimeUtil.getCustomMonth()
        mBillFragment = BillFragment.newInstance(startDate, endDate)
        mReportsFragment = ReportsFragment.newInstance(startDate, endDate)
        return arrayListOf(mBillFragment, mReportsFragment)
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_statistics
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_review -> ReviewActivity.open(this)
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun onChooseCustomMonth() {
        toolbarLayout.ivRight.isEnabled = beforeOffset != 0L
        val (startDate: Date, endDate: Date, displayDate: String) = DateTimeUtil.getCustomMonth(beforeOffset)
        toolbarLayout.tvTitle.text = displayDate
        mBillFragment.setMonthRange(startDate, endDate)
        mReportsFragment.setMonthRange(startDate, endDate)
    }

    companion object {
        fun open(context: Context) {
            context.startActivity(Intent(context, StatisticsActivity::class.java))
        }
    }
}
