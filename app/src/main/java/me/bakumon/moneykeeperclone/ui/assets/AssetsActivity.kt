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

package me.bakumon.moneykeeperclone.ui.assets

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_assets.*
import kotlinx.android.synthetic.main.layout_sort_tip.view.*
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.base.ErrorResource
import me.bakumon.moneykeeperclone.base.SuccessResource
import me.bakumon.moneykeeperclone.database.entity.Assets
import me.bakumon.moneykeeperclone.database.entity.AssetsMoneyBean
import me.bakumon.moneykeeperclone.ui.common.BaseActivity
import me.bakumon.moneykeeperclone.ui.common.Empty
import me.bakumon.moneykeeperclone.ui.common.EmptyViewBinder
import me.bakumon.moneykeeperclone.ui.common.SortDragCallback
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.bakumon.moneykeeperclone.utill.ToastUtils
import me.drakeet.floo.Floo
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * AssetsActivity
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
        btnAdd.setOnClickListener {
            Floo.navigation(this, Router.Url.URL_ADD_RECORD)
                    .putExtra(Router.ExtraKey.KEY_IS_TRANSFER, true)
                    .start()
        }

        val text = if (ConfigManager.symbol.isEmpty()) "" else "(" + ConfigManager.symbol + ")"
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
            ConfigManager.setIsShowSortTip(false)
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
            btnAdd.visibility = View.GONE
            sortTip.visibility = View.GONE
        } else {
            btnAdd.visibility = View.VISIBLE
            // 排序提示在用户点击或用户手动排序一次后隐藏，并且将不会显示
            if (ConfigManager.isShowSortTip) {
                sortTip.visibility = View.VISIBLE
                sortTip.ivClear.setOnClickListener {
                    sortTip.visibility = View.GONE
                    ConfigManager.setIsShowSortTip(false)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_assets, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_add -> Floo.navigation(this, Router.Url.URL_CHOOSE_ASSETS).start()
            android.R.id.home -> finish()
        }
        return true
    }

}
