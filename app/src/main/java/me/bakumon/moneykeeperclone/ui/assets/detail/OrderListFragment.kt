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

package me.bakumon.moneykeeperclone.ui.assets.detail

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.base.ErrorResource
import me.bakumon.moneykeeperclone.base.SuccessResource
import me.bakumon.moneykeeperclone.database.entity.RecordWithType
import me.bakumon.moneykeeperclone.ui.common.AbsListFragment
import me.bakumon.moneykeeperclone.ui.common.Empty
import me.bakumon.moneykeeperclone.ui.common.EmptyViewBinder
import me.bakumon.moneykeeperclone.ui.typerecords.RecordByMoneyViewBinder
import me.bakumon.moneykeeperclone.utill.ToastUtils
import me.bakumon.moneykeeperclone.widget.WidgetProvider
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * OrderListFragment
 *
 * @author Bakumon https://bakumon.me
 */
class OrderListFragment : AbsListFragment() {

    private lateinit var mViewModel: OrderListViewModel
    private var mAssetsId: Int? = 0

    override fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(RecordWithType::class, RecordByMoneyViewBinder { deleteRecord(it) })
        mAdapter.register(Empty::class, EmptyViewBinder())
    }

    private fun deleteRecord(record: RecordWithType) {
        mViewModel.deleteRecord(record).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    // 更新 widget
                    if (context != null) {
                        WidgetProvider.updateWidget(context!!)
                    }
                }
                is ErrorResource<Boolean> -> ToastUtils.show(R.string.toast_record_delete_fail)
            }
        })
    }

    override fun onItemsCreated(items: Items) {

    }

    override fun onParentInitDone(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        mAssetsId = arguments?.getInt(Router.ExtraKey.KEY_ASSETS_ID)

        mViewModel = getViewModel()
        initData()
    }

    override fun lazyInitData() {

    }

    private fun initData() {
        mViewModel.getRecordWithTypesByAssetsId(mAssetsId!!).observe(this, Observer {
            if (it != null) {
                setItems(it)
            }
        })
    }

    private fun setItems(list: List<RecordWithType>) {
        val items = Items()
        if (list.isEmpty()) {
            items.add(Empty(getString(R.string.text_order_record_no), Gravity.CENTER))
        } else {
            items.addAll(list)
        }
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(assetsId: Int): OrderListFragment {
            val fragment = OrderListFragment()
            val bundle = Bundle()
            bundle.putInt(Router.ExtraKey.KEY_ASSETS_ID, assetsId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
