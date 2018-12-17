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

package me.bakumon.moneykeeper.ui.assets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.android.synthetic.main.activity_assets.*
import kotlinx.android.synthetic.main.layout_sort_tip.view.*
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.base.ErrorResource
import me.bakumon.moneykeeper.base.SuccessResource
import me.bakumon.moneykeeper.database.entity.Assets
import me.bakumon.moneykeeper.database.entity.AssetsMoneyBean
import me.bakumon.moneykeeper.ui.add.AddRecordActivity
import me.bakumon.moneykeeper.ui.assets.choose.ChooseAssetsActivity
import me.bakumon.moneykeeper.ui.common.BaseActivity
import me.bakumon.moneykeeper.ui.common.Empty
import me.bakumon.moneykeeper.ui.common.EmptyViewBinder
import me.bakumon.moneykeeper.ui.common.SortDragCallback
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.ToastUtils
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * 资产首页（资产列表）
 *
 * @author bakumon https://bakumon.me
 * @date 2018/8/28
 */
class AssetsActivity : BaseActivity() {

    private lateinit var mViewModel: AssetsViewModel
    private lateinit var mAdapter: MultiTypeAdapter

    override val layoutId: Int
        get() = R.layout.activity_assets

    override fun isChangeStatusColor(): Boolean {
        return false
    }

    @SuppressLint("SetTextI18n")
    override fun onInitView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btnAdd.setOnClickListener { AddRecordActivity.open(this, isTransfer = true) }

        val text = if (DefaultSPHelper.symbol.isBlank()) "" else "(" + DefaultSPHelper.symbol + ")"
        tvNetAssetsTitle.text = getString(R.string.text_assets) + text

        category.text = getString(R.string.text_assets_account)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        mAdapter = MultiTypeAdapter()
        mAdapter.register(Assets::class, AssetsViewBinder())
        mAdapter.register(Empty::class, EmptyViewBinder())
        rvAssets.adapter = mAdapter

        val callback = SortDragCallback(mAdapter) {
            tvSave.visibility = View.VISIBLE
            sortTip.visibility = View.GONE
            DefaultSPHelper.isShowSortTip = false
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvAssets)

        tvSave.setOnClickListener { saveSort() }

        mViewModel = getViewModel()
        getAssetsMoney()
        getAssetsList()
    }

    private fun getAssetsMoney() {
        mViewModel.getAssetsMoney().observe(this, Observer {
            setAssetsMoney(it)
        })
    }

    private fun setAssetsMoney(it: AssetsMoneyBean?) {
        tvNetAssetsMoney.text = BigDecimalUtil.fen2YuanWithText(it?.netAssets)
        tvAllAssetsMoney.text = BigDecimalUtil.fen2YuanWithText(it?.allAssets)
        tvLiabilitiesMoney.text = BigDecimalUtil.fen2YuanWithText(it?.liabilityAssets)
    }

    private fun getAssetsList() {
        mViewModel.getAssets().observe(this, Observer {
            if (it != null) {
                setItems(it)
            }
        })
    }

    private fun setItems(list: List<Assets>) {
        if (list.size < 2) {
            btnAdd.hide()
            sortTip.visibility = View.GONE
        } else {
            btnAdd.show()
            // 排序提示在用户点击或用户手动排序一次后隐藏，并且将不会显示
            if (DefaultSPHelper.isShowSortTip) {
                sortTip.visibility = View.VISIBLE
                sortTip.ivClear.setOnClickListener {
                    sortTip.visibility = View.GONE
                    DefaultSPHelper.isShowSortTip = false
                }
            } else {
                sortTip.visibility = View.GONE
            }
        }
        val items = Items()
        if (list.isEmpty()) {
            items.add(Empty(getString(R.string.text_assets_no_account), Gravity.CENTER))
        } else {
            items.addAll(list)
        }
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }

    /**
     * 防止重复提交
     */
    private var isSaving = false

    private fun saveSort() {
        if (isSaving) {
            return
        }
        isSaving = true

        mViewModel.sortAssets(mAdapter.items as List<Assets>).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    tvSave.visibility = View.GONE
                    isSaving = false
                }
                is ErrorResource<Boolean> -> {
                    ToastUtils.show(R.string.toast_sort_fail)
                    isSaving = false
                }
            }
        })
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_assets
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_add -> ChooseAssetsActivity.open(this)
        }
        return super.onOptionsItemSelected(menuItem)
    }

    companion object {
        fun open(context: Context) {
            context.startActivity(Intent(context, AssetsActivity::class.java))
        }
    }
}
