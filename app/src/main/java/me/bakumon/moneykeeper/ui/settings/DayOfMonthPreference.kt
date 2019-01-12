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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.Preference
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getRecyclerView
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.ui.common.BaseTextPreference
import me.bakumon.moneykeeper.utill.AndroidUtil
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

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
        adapter.register(DayForList::class, FooterViewBinder())

        val list = ArrayList<DayForList>()
        for (i in 1..28) {
            list.add(DayForList(i.toString(), i.toString() == text))
        }
        adapter.items = list
        dialog!!.show {
            title(R.string.text_start_day_of_month)
            customListAdapter(adapter)
            negativeButton { }
            positiveButton {
                adapter.items.forEach { item ->
                    if (item is DayForList && item.isSelected) {
                        text = item.day
                        return@positiveButton
                    }
                }
            }
        }
        val recyclerView = dialog!!.getRecyclerView()
        recyclerView?.layoutManager = GridLayoutManager(context, 7)
        recyclerView?.clipToPadding = false
        recyclerView?.setPadding(40, 0, 20, 0)
    }

    class SimpleSummaryProvider private constructor() : Preference.SummaryProvider<DayOfMonthPreference> {

        override fun provideSummary(preference: DayOfMonthPreference): CharSequence {
            return if (TextUtils.isEmpty(preference.text)) {
                preference.context.getString(R.string.text_no_setting)
            } else {
                getDisplayDay(preference)
            }
        }

        private fun getDisplayDay(preference: DayOfMonthPreference): String {
            return if (AndroidUtil.isCN()) {
                preference.text + preference.context.getString(R.string.text_day)
            } else {
                if ("1" == preference.text || "21" == preference.text) {
                    preference.text + "st"
                } else if ("2" == preference.text || "22" == preference.text) {
                    preference.text + "nd"
                } else if ("3" == preference.text || "23" == preference.text) {
                    preference.text + "rd"
                } else {
                    preference.text + "th"
                }
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

    class FooterViewBinder : ItemViewBinder<DayForList, FooterViewBinder.ViewHolder>() {

        override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
            val root = inflater.inflate(R.layout.layout_day, parent, false)
            return ViewHolder(root)
        }

        override fun onBindViewHolder(holder: ViewHolder, item: DayForList) {
            holder.tvDay.text = item.day
            holder.tvDay.isSelected = item.isSelected
            holder.tvDay.setOnClickListener { setItemSelected(holder.adapterPosition) }
        }

        private fun setItemSelected(position: Int) {
            adapter.items.forEachIndexed { index, item ->
                if (item is DayForList) {
                    item.isSelected = index == position
                }
            }
            adapter.notifyDataSetChanged()
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvDay: TextView = itemView.findViewById(R.id.tv_day)
        }
    }

    data class DayForList(val day: String, var isSelected: Boolean)
}
