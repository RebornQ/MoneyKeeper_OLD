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

package me.bakumon.moneykeeperclone.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_head_page.view.*
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.database.entity.AssetsMoneyBean
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.database.entity.SumMoneyBean
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.drakeet.floo.Floo
import java.math.BigDecimal

/**
 * 首页 Head
 *
 * @author Bakumon https://bakumon.me
 */
class HeadPageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private val inflater = LayoutInflater.from(context)

    private var pagerView: View? = null
    private var pagerView1: View? = null

    private var mAdapter: HeadPagerAdapter

    init {
        orientation = LinearLayout.VERTICAL
        inflater.inflate(R.layout.layout_head_page, this, true)

        mAdapter = HeadPagerAdapter(arrayListOf())
        viewPager.adapter = mAdapter

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                indicator.setTotal(viewPager.childCount, position)
            }
        })
    }

    fun setSumMoneyBeanList(beanList: List<SumMoneyBean>?, assetsMontyBean: AssetsMoneyBean?) {
        if (beanList == null || assetsMontyBean == null) {
            return
        }
        val viewList = listOf(getPagerView(beanList), getPagerView1(beanList, assetsMontyBean))
        mAdapter.mViews = viewList
        mAdapter.notifyDataSetChanged()
        indicator.setTotal(mAdapter.count, viewPager.currentItem)
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun getPagerView(sumMoneyBean: List<SumMoneyBean>): View {
        if (pagerView == null) {
            pagerView = inflater.inflate(R.layout.layout_head_content, null)
        }
        val tvLeftTitle: TextView = pagerView!!.findViewById(R.id.tvLeftTitle)
        val tvLeftContent: TextView = pagerView!!.findViewById(R.id.tvLeftContent)
        val tvRightTitle: TextView = pagerView!!.findViewById(R.id.tvRightTitle)
        val tvRightContent: TextView = pagerView!!.findViewById(R.id.tvRightContent)
        val llRightContent: LinearLayout = pagerView!!.findViewById(R.id.llRightContent)

        val text = if (ConfigManager.symbol.isEmpty()) "" else "(" + ConfigManager.symbol + ")"
        tvLeftTitle.text = context.getText(R.string.text_month_outlay).toString() + text
        tvRightTitle.text = context.getText(R.string.text_month_remaining_budget).toString() + text

        var outlayStr = "0"
        if (sumMoneyBean.isNotEmpty()) {
            for ((type, sumMoney) in sumMoneyBean) {
                if (type == RecordType.TYPE_OUTLAY) {
                    outlayStr = BigDecimalUtil.fen2YuanWithText(sumMoney)
                }
            }
        }
        tvLeftContent.text = outlayStr


        var outlay = BigDecimal(0)
        if (sumMoneyBean.isNotEmpty()) {
            for ((type, sumMoney) in sumMoneyBean) {
                if (type == RecordType.TYPE_OUTLAY) {
                    outlay = sumMoney
                }
            }
        }
        val budget = ConfigManager.budget
        if (budget > 0) {
            val budgetStr = BigDecimalUtil.fen2YuanWithText(BigDecimal(ConfigManager.budget).multiply(BigDecimal(100)).subtract(outlay))
            tvRightContent.text = budgetStr
            tvRightContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34f)
        } else {
            tvRightContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            tvRightContent.text = tvRightContent.context.getString(R.string.text_set_budget)
        }
        llRightContent.setOnClickListener {
            Floo.navigation(llRightContent.context, Router.Url.URL_SETTING)
                    .start()
        }
        return pagerView!!
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun getPagerView1(sumMoneyBean: List<SumMoneyBean>, assetsMontyBean: AssetsMoneyBean): View {
        if (pagerView1 == null) {
            pagerView1 = inflater.inflate(R.layout.layout_head_content, null)
        }
        val tvLeftTitle: TextView = pagerView1!!.findViewById(R.id.tvLeftTitle)
        val tvLeftContent: TextView = pagerView1!!.findViewById(R.id.tvLeftContent)
        val tvRightTitle: TextView = pagerView1!!.findViewById(R.id.tvRightTitle)
        val tvRightContent: TextView = pagerView1!!.findViewById(R.id.tvRightContent)
        val llRightContent: LinearLayout = pagerView1!!.findViewById(R.id.llRightContent)

        val text = if (ConfigManager.symbol.isEmpty()) "" else "(" + ConfigManager.symbol + ")"
        tvLeftTitle.text = context.getText(R.string.text_month_income).toString() + text
        tvRightTitle.text = context.getText(R.string.text_assets).toString() + text

        var incomeStr = "0"
        if (sumMoneyBean.isNotEmpty()) {
            for ((type, sumMoney) in sumMoneyBean) {
                if (type == RecordType.TYPE_INCOME) {
                    incomeStr = BigDecimalUtil.fen2YuanWithText(sumMoney)
                }
            }
        }
        tvLeftContent.text = incomeStr

        tvRightContent.text = BigDecimalUtil.fen2YuanWithText(assetsMontyBean.netAssets)
        llRightContent.setOnClickListener {
            Floo.navigation(llRightContent.context, Router.Url.URL_ASSETS)
                    .start()
        }
        return pagerView1!!
    }

    internal inner class HeadPagerAdapter(var mViews: List<View>) : PagerAdapter() {

        override fun getCount(): Int {
            return mViews.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(mViews[position])
            return mViews[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(mViews[position])
        }
    }
}