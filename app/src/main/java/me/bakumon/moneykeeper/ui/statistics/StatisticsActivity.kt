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
import android.view.MenuItem
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_two_tab.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.ui.common.AbsTwoTabActivity
import me.bakumon.moneykeeper.ui.review.ReviewActivity
import me.bakumon.moneykeeper.ui.statistics.bill.BillFragment
import me.bakumon.moneykeeper.ui.statistics.reports.ReportsFragment
import me.bakumon.moneykeeper.utill.DateUtils
import java.util.*

/**
 * 统计
 *
 * @author Bakumon https://bakumon
 */
class StatisticsActivity : AbsTwoTabActivity() {

    private lateinit var mBillFragment: BillFragment
    private lateinit var mReportsFragment: ReportsFragment
    private var mCurrentYear = DateUtils.getCurrentYear()
    private var mCurrentMonth = DateUtils.getCurrentMonth()

    override fun onSetupTitle(tvTitle: TextView) {
        toolbarLayout.tvTitle.text = DateUtils.getCurrentYearMonth()
        toolbarLayout.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
        toolbarLayout.tvTitle.compoundDrawablePadding = 10
        toolbarLayout.tvTitle.setOnClickListener { chooseMonth() }
    }

    override fun getTwoTabText(): ArrayList<String> {
        return arrayListOf(getString(R.string.text_order), getString(R.string.text_reports))
    }

    override fun getTwoFragments(): ArrayList<Fragment> {
        mBillFragment = BillFragment()
        mReportsFragment = ReportsFragment()

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

    private fun chooseMonth() {
        toolbarLayout.tvTitle.isEnabled = false
        val chooseMonthDialog = ChooseMonthDialog(this, mCurrentYear, mCurrentMonth)
        chooseMonthDialog.mOnDismissListener = {
            toolbarLayout.tvTitle.isEnabled = true
        }
        chooseMonthDialog.mOnChooseListener = { year, month ->
            mCurrentYear = year
            mCurrentMonth = month
            toolbarLayout.tvTitle.text = DateUtils.getYearMonthFormatString(year, month)
            mBillFragment.setYearMonth(year, month)
            mReportsFragment.setYearMonth(year, month)
        }
        chooseMonthDialog.show()
    }

    companion object {
        fun open(context: Context) {
            context.startActivity(Intent(context, StatisticsActivity::class.java))
        }
    }
}
