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

import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.fragment.app.Fragment
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.ui.common.AbsTwoTabActivity
import java.util.*

/**
 * ChooseAssetsActivity
 *
 * @author Bakumon https://bakumon
 */
class ChooseAssetsActivity : AbsTwoTabActivity() {
    override fun onSetupTitle(tvTitle: TextView) {
        tvTitle.text = getString(R.string.text_new_assets)
    }

    override fun getTwoTabText(): ArrayList<String> {
        return arrayListOf(getString(R.string.text_account), getString(R.string.text_invest))
    }

    override fun getTwoFragments(): ArrayList<Fragment> {
        val outlayFragment = AssetsListFragment.newInstance(AssetsListFragment.TYPE_NORMAL)
        val incomeFragment = AssetsListFragment.newInstance(AssetsListFragment.TYPE_INVEST)
        return arrayListOf(outlayFragment, incomeFragment)
    }

    companion object {
        fun open(context: Context) {
            context.startActivity(Intent(context, ChooseAssetsActivity::class.java))
        }
    }
}
