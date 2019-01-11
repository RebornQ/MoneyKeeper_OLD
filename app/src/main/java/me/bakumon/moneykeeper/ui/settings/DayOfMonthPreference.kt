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
package me.bakumon.moneykeeper.ui.settings

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getRecyclerView
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.ui.common.BaseTextPreference
import me.bakumon.moneykeeper.ui.home.FooterViewBinder
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import java.util.ArrayList

class DayOfMonthPreference : BaseTextPreference {

    private var dialog: MaterialDialog? = null

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun init() {
        summaryProvider = SimpleSummaryProvider.instance
    }

    override fun onClick() {
        if (dialog == null) {
            dialog = MaterialDialog(context)
        }
        val adapter = MultiTypeAdapter()
        adapter.register(String::class, FooterViewBinder())
        adapter.items = arrayListOf(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28"
        )
        dialog!!.show {
            title(text = "月起始日")
            customListAdapter(adapter)
            positiveButton {  }
            negativeButton {  }
        }
        val recyclerView = dialog!!.getRecyclerView()
        recyclerView?.layoutManager = GridLayoutManager(context, 7)
    }

    class SimpleSummaryProvider private constructor() : Preference.SummaryProvider<DayOfMonthPreference> {

        override fun provideSummary(preference: DayOfMonthPreference): CharSequence {
            return if (TextUtils.isEmpty(preference.text)) {
                preference.context.getString(R.string.text_no_setting)
            } else {
                BigDecimalUtil.formatNum(preference.text)
            }
        }

        companion object {

            private var sSimpleSummaryProvider: SimpleSummaryProvider? = null

            val instance: SimpleSummaryProvider
                get() {
                    if (sSimpleSummaryProvider == null) {
                        sSimpleSummaryProvider = SimpleSummaryProvider()
                    }
                    return sSimpleSummaryProvider!!
                }
        }
    }
}
