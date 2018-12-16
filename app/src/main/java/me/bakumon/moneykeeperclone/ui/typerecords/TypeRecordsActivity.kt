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

package me.bakumon.moneykeeperclone.ui.typerecords

import android.support.v4.app.Fragment
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_two_tab.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.ui.common.AbsTwoTabActivity
import java.util.*

/**
 * 某一类型的记账记录
 *
 * @author Bakumon https://bakumon
 */
class TypeRecordsActivity : AbsTwoTabActivity() {

    override fun onSetupTitle(tvTitle: TextView) {
        toolbarLayout.tvTitle.text = intent.getStringExtra(Router.ExtraKey.KEY_TYPE_NAME)
    }

    override fun getTwoTabText(): ArrayList<String> {
        return arrayListOf(getString(R.string.text_sort_time), getString(R.string.text_sort_money))
    }

    override fun getTwoFragments(): ArrayList<Fragment> {
        val mRecordType = intent.getIntExtra(Router.ExtraKey.KEY_RECORD_TYPE, 0)
        val mRecordTypeId = intent.getIntExtra(Router.ExtraKey.KEY_RECORD_TYPE_ID, 0)
        val mYear = intent.getIntExtra(Router.ExtraKey.KEY_YEAR, 0)
        val mMonth = intent.getIntExtra(Router.ExtraKey.KEY_MONTH, 0)

        val timeSortFragment = TypeRecordsByTimeFragment.newInstance(mRecordType, mRecordTypeId, mYear, mMonth)
        val moneySortFragment = TypeRecordsByMoneyFragment.newInstance(mRecordType, mRecordTypeId, mYear, mMonth)

        return arrayListOf(timeSortFragment, moneySortFragment)
    }
}
