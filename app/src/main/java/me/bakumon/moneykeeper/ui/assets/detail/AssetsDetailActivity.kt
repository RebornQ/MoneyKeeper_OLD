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
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_assets_detail.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeper.ConfigManager
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.Router
import me.bakumon.moneykeeper.base.ErrorResource
import me.bakumon.moneykeeper.base.SuccessResource
import me.bakumon.moneykeeper.database.entity.Assets
import me.bakumon.moneykeeper.database.entity.AssetsModifyRecord
import me.bakumon.moneykeeper.ui.common.BaseActivity
import me.bakumon.moneykeeper.ui.common.Empty
import me.bakumon.moneykeeper.ui.common.EmptyViewBinder
import me.bakumon.moneykeeper.ui.setting.Category
import me.bakumon.moneykeeper.ui.setting.CategoryViewBinder
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.ResourcesUtil
import me.bakumon.moneykeeper.utill.ToastUtils
import me.drakeet.floo.Floo
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * AddAssetsActivity
 *
 * @author Bakumon https://bakumon.me
 */
class AssetsDetailActivity : BaseActivity() {

    private lateinit var mViewModel: AssetsDetailViewModel
    private lateinit var mAssets: Assets
    private lateinit var mAdapter: MultiTypeAdapter

    override val layoutId: Int
        get() = R.layout.activity_assets_detail

    override fun onInitView(savedInstanceState: Bundle?) {
        toolbarLayout.tvTitle.text = getString(R.string.text_assets_detail)
        setSupportActionBar(toolbarLayout as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        mAdapter = MultiTypeAdapter()
        mAdapter.register(Category::class, CategoryViewBinder())
        mAdapter.register(AssetsModifyRecord::class, AssetsRecordBinder())
        mAdapter.register(Empty::class, EmptyViewBinder())
        rvAssets.adapter = mAdapter

        val extra = intent.getSerializableExtra(Router.ExtraKey.KEY_ASSETS)
        if (extra == null) {
            finish()
        }
        mAssets = extra as Assets

        mViewModel = getViewModel()
        getAssetsData(mAssets.id!!)
        getAssetsRecord(mAssets.id!!)
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
        val text = if (ConfigManager.symbol.isEmpty()) "" else "(" + ConfigManager.symbol + ")"
        tvMoneyTitle.text = getText(R.string.text_assets_balance).toString() + text
        tvMoney.text = BigDecimalUtil.fen2Yuan(assets.money)
    }

    private fun getAssetsRecord(id: Int) {
        mViewModel.getAssetsRecordById(id).observe(this, Observer {
            if (it != null) {
                setItems(it)
            }
        })
    }

    private fun setItems(list: List<AssetsModifyRecord>) {
        val items = Items()
        if (list.isEmpty()) {
            items.add(Empty(getString(R.string.text_adjust_record_no), Gravity.CENTER))
        } else {
            items.add(Category(getString(R.string.text_adjust_record)))
            items.addAll(list)
        }
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_assets_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_edit -> Floo.navigation(this, Router.Url.URL_ADD_ASSETS)
                    .putExtra(Router.ExtraKey.KEY_ASSETS, mAssets)
                    .start()
            R.id.action_delete -> showDeleteDialog()
            android.R.id.home -> finish()
        }
        return true
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
}
