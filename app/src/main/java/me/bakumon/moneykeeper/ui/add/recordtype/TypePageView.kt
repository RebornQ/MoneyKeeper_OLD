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

package me.bakumon.moneykeeper.ui.add.recordtype

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.layout_type_page.view.*
import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.RecordForList
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.view.pagerlayoutmanager.PagerGridLayoutManager
import me.bakumon.moneykeeper.view.pagerlayoutmanager.PagerGridSnapHelper
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import me.drakeet.multitype.withKClassLinker

/**
 * 翻页的 recyclerView + 指示器
 *
 * @author Bakumon https://bakumon.me
 */
class TypePageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private val inflater = LayoutInflater.from(context)
    private var adapter: MultiTypeAdapter
    private var mLayoutManager: PagerGridLayoutManager
    private var mCurrentTypeIndex = -1
    private var mCurrentTypeId = -1

    val currentItem: RecordType?
        get() = adapter.items[mCurrentTypeIndex] as RecordType

    init {
        orientation = LinearLayout.VERTICAL
        inflater.inflate(R.layout.layout_type_page, this, true)

        // 1.水平分页布局管理器
        mLayoutManager = PagerGridLayoutManager(
            ROW,
            COLUMN, PagerGridLayoutManager.HORIZONTAL)
        recyclerType.layoutManager = mLayoutManager

        // 2.设置滚动辅助工具
        val pageSnapHelper = PagerGridSnapHelper()
        pageSnapHelper.attachToRecyclerView(recyclerType)

        adapter = MultiTypeAdapter()
        adapter.register(RecordType::class).to(TypeViewBinder { recordType, position ->
            mCurrentTypeId = recordType.id
            mCurrentTypeIndex = position
            // 更新所有 item 的选中状态
            adapter.items.forEachIndexed { index, any ->
                val item = any as RecordType
                item.isChecked = index == position
            }
            adapter.notifyDataSetChanged()
        }, TypeSettingViewBinder())
                .withKClassLinker { _, data ->
                    if (data.isSetting) {
                        TypeSettingViewBinder::class
                    } else {
                        TypeViewBinder::class
                    }
                }
        recyclerType.adapter = adapter

        mLayoutManager.setPageListener(object : PagerGridLayoutManager.PageListener {
            var currentPageIndex: Int = 0
            var pageSize: Int = 0

            override fun onPageSizeChanged(pageSize: Int) {
                this.pageSize = pageSize
                setIndicator()
            }

            override fun onPageSelect(pageIndex: Int) {
                currentPageIndex = pageIndex
                setIndicator()
            }

            private fun setIndicator() {
                if (pageSize > 1) {
                    indicator.visibility = View.VISIBLE
                    indicator.setTotal(pageSize, currentPageIndex)
                } else {
                    indicator.visibility = View.INVISIBLE
                }
            }
        })
    }

    fun setItems(data: List<RecordType>?, type: Int, record: RecordForList? = null) {
        if (data == null) {
            return
        }
        val items = Items()
        if (data.isNotEmpty()) {
            for (i in data.indices) {
                if (data[i].type == type) {
                    items.add(data[i])
                }
            }
            // 增加设置 item
            val settingItem = RecordType(App.instance.getString(R.string.text_setting), type, true)
            items.add(settingItem)
        }

        adapter.items = items
        adapter.notifyDataSetChanged()

        initCheckItem(type, record)
    }

    /**
     * 初始化选中某个 item
     * record 是空，选中第一个，否则选中对应的 item
     */
    private fun initCheckItem(type: Int, record: RecordForList?) {
        if (mCurrentTypeIndex == -1) {
            mCurrentTypeIndex = 0
            var isTypeExist = 0
            if (visibility == View.VISIBLE && record != null && adapter.items.isNotEmpty()) {
                for (i in 0 until adapter.items.size) {
                    if (record.recordTypeId == (adapter.items[i] as RecordType).id) {
                        mCurrentTypeIndex = i
                        isTypeExist++
                        break
                    }
                }
                if (isTypeExist != 0) {
                    // 选中对应的页
                    val pageIndex = mCurrentTypeIndex / (ROW * COLUMN)
                    post { mLayoutManager.smoothScrollToPage(pageIndex) }
                } else {
                    showTypeNotExistTip(type, record)
                }
            }
            // 选中某一个
            (adapter.items[mCurrentTypeIndex] as RecordType).isChecked = true
            adapter.notifyItemChanged(mCurrentTypeIndex)
        } else {
            // 找出上次选中的 item
            val position = getPosition(mCurrentTypeId)
            mCurrentTypeIndex = if (position == -1) {
                0
            } else {
                position
            }
            (adapter.items[mCurrentTypeIndex] as RecordType).isChecked = true
            adapter.notifyItemChanged(mCurrentTypeIndex)
        }
    }

    /**
     * 查找 id 对应的 index
     */
    private fun getPosition(id: Int): Int {
        var index = -1
        for (i in 0 until adapter.items.size) {
            if (id == (adapter.items[i] as RecordType).id) {
                index = i
                break
            }
        }
        return index
    }

    /**
     * 提示用户该记录的类型已经被删除
     */
    private fun showTypeNotExistTip(type: Int, record: RecordForList) {
        if (type == record.type) {
            MaterialDialog(context)
                    .message(text = "\uD83D\uDC7A" + context.resources.getString(R.string.text_tip_type_delete))
                    .positiveButton(R.string.text_know)
                    .show()
        }
    }

    companion object {
        private const val ROW = 2
        private const val COLUMN = 4
    }

}
