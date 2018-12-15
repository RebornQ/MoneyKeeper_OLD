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

package me.bakumon.moneykeeper.ui.common

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import kotlinx.android.synthetic.main.layout_type_choose.view.*
import me.bakumon.moneykeeper.R
import java.util.*

/**
 * 两个tab fragment
 *
 * @author Bakumon https://bakumon
 */
abstract class AbsTwoTabActivity : BaseActivity() {

    private lateinit var typeChoose: RadioGroup
    private lateinit var viewPager: ViewPager

    override val layoutId: Int
        get() = R.layout.activity_two_tab

    protected abstract fun onSetupTitle(tvTitle: TextView)

    protected abstract fun getTwoTabText(): ArrayList<String>

    override fun onInitView(savedInstanceState: Bundle?) {
        val toolbar: Toolbar = findViewById(R.id.toolbarLayout)
        onSetupTitle(toolbar.tvTitle)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        typeChoose = findViewById(R.id.typeChoose)
        typeChoose.rbLeft.text = getTwoTabText()[0]
        typeChoose.rbRight.text = getTwoTabText()[1]
    }

    protected abstract fun getTwoFragments(): ArrayList<Fragment>

    override fun onInit(savedInstanceState: Bundle?) {
        val adapter = FragmentViewPagerAdapter(supportFragmentManager, getTwoFragments())
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

        typeChoose.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbLeft) {
                viewPager.setCurrentItem(0, false)
            } else {
                viewPager.setCurrentItem(1, false)
            }
        }
        typeChoose.check(R.id.rbLeft)
        onParentInitDone()
    }

    open fun onParentInitDone() {

    }

    fun setCurrentItem(index: Int) {
        if (index == 0) {
            typeChoose.check(R.id.rbLeft)
        } else {
            typeChoose.check(R.id.rbRight)
        }
    }

    protected fun getTabCurrentIndex(): Int {
        return viewPager.currentItem
    }

    inner class FragmentViewPagerAdapter(fm: FragmentManager, private val fragments: ArrayList<Fragment>) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

    }
}
