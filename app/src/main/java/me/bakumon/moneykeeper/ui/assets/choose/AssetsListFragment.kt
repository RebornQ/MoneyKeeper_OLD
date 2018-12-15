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

package me.bakumon.moneykeeper.ui.assets.choose

import android.os.Bundle
import androidx.annotation.IntDef
import androidx.recyclerview.widget.RecyclerView
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.ui.common.AbsListFragment
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * AssetsListFragment
 *
 * @author Bakumon https://bakumon.me
 */
class AssetsListFragment : AbsListFragment() {

    private var mType: Int? = TYPE_NORMAL

    override fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(AssetsType::class, AssetsListViewBinder())
    }

    override fun onItemsCreated(items: Items) {
        mType = arguments?.getInt(KEY_TYPE)
        when (mType) {
            TYPE_NORMAL -> {
                items.add(AssetsType(getString(R.string.text_assets_type_cash), "assets_wallet", 1))
                items.add(AssetsType(getString(R.string.text_assets_type_bank_card), "assets_card", 2))
                items.add(AssetsType(getString(R.string.text_assets_type_alipay), "assets_alipay", 3))
                items.add(AssetsType(getString(R.string.text_assets_type_wechat), "assets_wechat", 4))
                items.add(AssetsType(getString(R.string.text_assets_type_jd), "assets_jd", 5))
                items.add(AssetsType(getString(R.string.text_assets_type_rice_card), "assets_rice_card", 6))
                items.add(AssetsType(getString(R.string.text_assets_type_bus_card), "assets_bus_card", 7))
                items.add(AssetsType(getString(R.string.text_assets_type_other), "assets_other", 8))
            }
            TYPE_INVEST -> {
                items.add(AssetsType(getString(R.string.text_assets_type_monetary), "assets_monetary", 9))
                items.add(AssetsType(getString(R.string.text_assets_type_funding), "assets_funding", 10))
                items.add(AssetsType(getString(R.string.text_assets_type_stock), "assets_stock", 11))
                items.add(AssetsType(getString(R.string.text_assets_type_other_financial), "assets_other", 12))
            }
        }
    }

    override fun onParentInitDone(recyclerView: RecyclerView, savedInstanceState: Bundle?) {

    }

    override fun lazyInitData() {

    }

    companion object {
        const val KEY_TYPE = "type"
        const val TYPE_NORMAL = 0
        const val TYPE_INVEST = 1

        @IntDef(TYPE_NORMAL, TYPE_INVEST)
        @Retention(AnnotationRetention.SOURCE)
        annotation class AssetsTabType

        fun newInstance(@AssetsTabType type: Int): AssetsListFragment {
            val fragment = AssetsListFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }
    }
}
