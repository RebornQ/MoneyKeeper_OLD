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

package me.bakumon.moneykeeperclone.ui.typesort

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.base.ErrorResource
import me.bakumon.moneykeeperclone.base.SuccessResource
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.ui.common.AbsListActivity
import me.bakumon.moneykeeperclone.ui.common.SortDragCallback
import me.bakumon.moneykeeperclone.utill.ToastUtils
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * 类型排序
 *
 * @author bakumon https://bakumon.me
 */
class TypeSortActivity : AbsListActivity() {

    private lateinit var mViewModel: TypeSortViewModel
    private var mType: Int = RecordType.TYPE_OUTLAY

    override fun onSetupTitle(tvTitle: TextView) {
        tvTitle.text = getString(R.string.text_drag_sort)
    }

    override fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(RecordType::class, TypeSortViewBinder())
    }

    override fun onItemsCreated(items: Items) {

    }

    override fun onParentInitDone(recyclerView: RecyclerView, savedInstanceState: Bundle?) {

        recyclerView.layoutManager = GridLayoutManager(this, 4)


        val callback = SortDragCallback(mAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        mType = intent.getIntExtra(Router.ExtraKey.KEY_TYPE, RecordType.TYPE_OUTLAY)
        mViewModel = getViewModel()
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sort, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_done -> sortRecordTypes()
            android.R.id.home -> finish()
        }
        return true
    }

    private var isSaving = false

    private fun sortRecordTypes() {
        if (isSaving) {
            return
        }
        isSaving = true
        mViewModel.sortRecordTypes(mAdapter.items as List<RecordType>).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> finish()
                is ErrorResource<Boolean> -> {
                    isSaving = false
                    ToastUtils.show(R.string.toast_sort_fail)
                }
            }
        })
    }

    private fun initData() {
        mViewModel.getRecordTypes(mType).observe(this, Observer {
            if (it != null) {
                setItems(it)
            }
        })
    }

    private fun setItems(data: List<RecordType>) {
        val items = Items()
        items.addAll(data)
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }
}
