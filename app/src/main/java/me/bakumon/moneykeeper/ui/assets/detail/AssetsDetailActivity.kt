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

package me.bakumon.moneykeeper.ui.assets.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_assets_detail.*
import kotlinx.android.synthetic.main.layout_three_choose.view.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.base.ErrorResource
import me.bakumon.moneykeeper.base.SuccessResource
import me.bakumon.moneykeeper.database.entity.Assets
import me.bakumon.moneykeeper.ui.assets.add.AddAssetsActivity
import me.bakumon.moneykeeper.ui.common.BaseActivity
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.ResourcesUtil
import me.bakumon.moneykeeper.utill.ToastUtils

/**
 * AddAssetsActivity
 *
 * @author Bakumon https://bakumon.me
 */
class AssetsDetailActivity : BaseActivity() {

    private lateinit var mViewModel: AssetsDetailViewModel
    private lateinit var mAssets: Assets

    override val layoutId: Int
        get() = R.layout.activity_assets_detail

    override fun onInitView(savedInstanceState: Bundle?) {
        toolbarLayout.tvTitle.text = getString(R.string.text_assets_detail)
        setSupportActionBar(toolbarLayout as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        typeChoose.rbLeft.text = getString(R.string.text_transfer)
        typeChoose.rbMiddle.text = getString(R.string.text_order)
        typeChoose.rbRight.text = getString(R.string.text_modify)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        val extra = intent.getSerializableExtra(KEY_ASSETS)
        if (extra == null) {
            finish()
        }
        mAssets = extra as Assets

        val transferListFragment = TransferListFragment.newInstance(mAssets.id!!)
        val orderListFragment = OrderListFragment.newInstance(mAssets.id!!)
        val modifyListFragment = ModifyListFragment.newInstance(mAssets.id!!)
        val adapter = FragmentViewPagerAdapter(
            supportFragmentManager,
            arrayListOf(transferListFragment, orderListFragment, modifyListFragment)
        )

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3

        (typeChoose as RadioGroup).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbLeft -> viewPager.setCurrentItem(0, false)
                R.id.rbMiddle -> viewPager.setCurrentItem(1, false)
                R.id.rbRight -> viewPager.setCurrentItem(2, false)
            }
        }
        (typeChoose as RadioGroup).check(R.id.rbMiddle)

        mViewModel = getViewModel()
        getAssetsData(mAssets.id!!)
    }

    /**
     * 从数据库中获取资产数据
     * 不直接使用 mAssets？
     * 修改之后，数据库改变，会观察到，自动更新界面
     */
    private fun getAssetsData(id: Int) {
        mViewModel.getAssetsById(id).observe(this, Observer {
            if (it == null) {
                finish()
            } else {
                setView(it)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setView(assets: Assets) {
        mAssets = assets
        ivAssets.setImageResource(ResourcesUtil.getTypeImgId(this, assets.imgName))
        tvAssetsName.text = assets.name
        if (assets.remark.isBlank()) {
            vLine.visibility = View.GONE
            tvRemark.visibility = View.GONE
        } else {
            vLine.visibility = View.VISIBLE
            tvRemark.visibility = View.VISIBLE
            tvRemark.text = assets.remark
        }
        val text = if (DefaultSPHelper.symbol.isBlank()) "" else "(" + DefaultSPHelper.symbol + ")"
        tvMoneyTitle.text = getText(R.string.text_assets_balance).toString() + text
        tvMoney.text = BigDecimalUtil.fen2YuanWithText(assets.money)
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_assets_detail
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_edit -> AddAssetsActivity.open(this, assets = mAssets)
            R.id.action_delete -> showDeleteDialog()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun showDeleteDialog() {
        MaterialDialog(this)
            .title(R.string.text_delete)
            .message(R.string.text_tip_delete_assets)
            .negativeButton(R.string.text_cancel)
            .positiveButton(R.string.text_affirm_delete) { deleteAssets() }
            .show()
    }

    /**
     * 防止重复操作
     */
    private var isDeleteEnable = true

    private fun deleteAssets() {
        if (!isDeleteEnable) {
            return
        }
        isDeleteEnable = false
        mViewModel.deleteAssets(mAssets).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    finish()
                }
                is ErrorResource<Boolean> -> {
                    ToastUtils.show(R.string.toast_delete_fail)
                    isDeleteEnable = true
                }
            }
        })
    }

    inner class FragmentViewPagerAdapter(fm: FragmentManager, private val fragments: ArrayList<Fragment>) :
        FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

    }

    companion object {
        private const val KEY_ASSETS = "KEY_ASSETS"
        fun open(context: Context, assets: Assets) {
            val intent = Intent(context, AssetsDetailActivity::class.java)
            intent.putExtra(KEY_ASSETS, assets)
            context.startActivity(intent)
        }
    }
}
