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

import android.os.Bundle
import android.view.Gravity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.base.ErrorResource
import me.bakumon.moneykeeper.base.SuccessResource
import me.bakumon.moneykeeper.database.entity.AssetsTransferRecordWithAssets
import me.bakumon.moneykeeper.ui.common.AbsListFragment
import me.bakumon.moneykeeper.ui.common.Empty
import me.bakumon.moneykeeper.ui.common.EmptyViewBinder
import me.bakumon.moneykeeper.utill.ToastUtils
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * TransferListFragment
 *
 * @author Bakumon https://bakumon.me
 */
class TransferListFragment : AbsListFragment() {

    private lateinit var mViewModel: TransferRecordViewModel
    private var mAssetsId: Int? = 0

    override fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(AssetsTransferRecordWithAssets::class, TransferRecordBinder { deleteItem(it) })
        mAdapter.register(Empty::class, EmptyViewBinder())
    }

    override fun onItemsCreated(items: Items) {

    }

    override fun onParentInitDone(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        mAssetsId = arguments?.getInt(KEY_ASSETS_ID)

        mViewModel = getViewModel()
        initData()
    }

    override fun lazyInitData() {

    }

    private fun initData() {
        mViewModel.getTransferRecordById(mAssetsId!!).observe(this, Observer {
            if (it != null) {
                setItems(it)
            }
        })
    }

    private fun setItems(list: List<AssetsTransferRecordWithAssets>) {
        val items = Items()
        if (list.isEmpty()) {
            items.add(Empty(getString(R.string.text_transfer_record_no), Gravity.CENTER))
        } else {
            items.addAll(list)
        }
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }

    private fun deleteItem(item: AssetsTransferRecordWithAssets) {
        mViewModel.deleteTransferRecord(item).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                }
                is ErrorResource<Boolean> -> {
                    ToastUtils.show(if (it.errorMessage.isEmpty()) " " else it.errorMessage)
                }
            }
        })
    }

    companion object {
        private const val KEY_ASSETS_ID = "KEY_ASSETS_ID"
        fun newInstance(assetsId: Int): TransferListFragment {
            val fragment = TransferListFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_ASSETS_ID, assetsId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
