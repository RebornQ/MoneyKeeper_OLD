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

package me.bakumon.moneykeeperclone.ui.review

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.utill.DateUtils
import me.bakumon.moneykeeperclone.view.PickerLayoutManager
import java.util.*

/**
 * 选择年份
 *
 * @author Bakumon https://bakumon
 */
class ChooseYearDialog {

    private var mContext: Context
    private lateinit var mYearAdapter: PickerAdapter

    private lateinit var mBuilder: AlertDialog.Builder

    /**
     * 选择完成月份后，点击确定按钮监听
     * Int 1选择的年份
     * Int 2选择的月份
     */
    var mOnChooseListener: ((Int) -> Unit)? = null
    /**
     * dismiss 监听
     */
    var mOnDismissListener: ((Unit) -> Unit)? = null

    private var mYear = DateUtils.getCurrentYear()

    constructor(context: Context) {
        mContext = context
        setupDialog()
    }

    constructor(context: Context, year: Int) {
        mContext = context
        mYear = year
        setupDialog()
    }

    @SuppressLint("InflateParams")
    private fun setupDialog() {
        val layoutInflater = LayoutInflater.from(mContext)
        val contentView = layoutInflater.inflate(R.layout.dialog_choose_month, null, false)
        val rvYear = contentView.findViewById<RecyclerView>(R.id.rv_year)

        // 设置 pickerLayoutManage
        val lmYear = PickerLayoutManager(mContext, rvYear, LinearLayoutManager.VERTICAL, false, 3, 0.4f, true)
        rvYear.layoutManager = lmYear

        mYearAdapter = PickerAdapter()
        rvYear.adapter = mYearAdapter

        setYearAdapter()

        lmYear.OnSelectedViewListener(object : PickerLayoutManager.OnSelectedViewListener {
            override fun onSelectedView(view: View, position: Int) {
                mYear = mYearAdapter.data[position]
            }
        })
        // 选中对于年
        for (i in mYearAdapter.data.size - 1 downTo 0) {
            if (mYearAdapter.data[i] == mYear) {
                rvYear.scrollToPosition(i)
                break
            }
        }

        mBuilder = AlertDialog.Builder(mContext)
                .setTitle(R.string.text_choose_year)
                .setView(contentView)
                .setNegativeButton(R.string.text_cancel, null)
                .setPositiveButton(R.string.text_affirm) { _, _ ->
                    mOnChooseListener?.invoke(mYear)
                }
        mBuilder.setOnDismissListener { mOnDismissListener?.invoke(Unit) }
    }

    private fun setYearAdapter() {
        val yearList = ArrayList<Int>()
        for (i in MIN_YEAR..MAX_YEAR) {
            yearList.add(i)
        }
        mYearAdapter.setNewData(yearList)
    }

    fun show() {
        mBuilder.create().show()
    }

    internal inner class PickerAdapter : RecyclerView.Adapter<PickerAdapter.ViewHolder>() {
        val data = arrayListOf<Int>()
        fun setNewData(data: List<Int>) {
            this.data.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.item_picker, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = data[position].toString()
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.tv_text)
        }
    }

    companion object {

        /**
         * 为什么是 1900
         * 添加记账记录时，选时间 dialog 最小可选的年份是 1900
         */
        private const val MIN_YEAR = 1900
        private val MAX_YEAR = DateUtils.getCurrentYear()
    }
}
