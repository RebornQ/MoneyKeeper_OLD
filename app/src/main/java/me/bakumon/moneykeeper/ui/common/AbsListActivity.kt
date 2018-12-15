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

package me.bakumon.moneykeeper.ui.common

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeper.R
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * list activity 抽象父类，包含 标题 和 列表
 *
 * @author Bakumon https://bakumon.me
 */
abstract class AbsListActivity : BaseActivity() {

    protected val mAdapter: MultiTypeAdapter = MultiTypeAdapter()
    protected lateinit var mRecyclerView: RecyclerView

    override val layoutId: Int
        get() = R.layout.activity_list

    override fun onInitView(savedInstanceState: Bundle?) {
        mRecyclerView = recyclerView
        onSetupTitle(toolbarLayout.tvTitle)
        setSupportActionBar(toolbarLayout as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /**
     * 设置 title
     */
    protected abstract fun onSetupTitle(tvTitle: TextView)

    override fun onInit(savedInstanceState: Bundle?) {
        onAdapterCreated(mAdapter)

        val items = Items()
        onItemsCreated(items)
        mAdapter.items = items
        recyclerView.adapter = mAdapter

        onParentInitDone(recyclerView, savedInstanceState)
    }

    /**
     * 设置 adapter
     * 如，adapter.register()
     */
    protected abstract fun onAdapterCreated(adapter: MultiTypeAdapter)

    /**
     * 添加数据
     */
    protected abstract fun onItemsCreated(items: Items)

    /**
     * 子类开始自己的初始化
     * 如，配置 recyclerView（默认 recyclerView 垂直方向的线性布局）
     */
    protected abstract fun onParentInitDone(recyclerView: RecyclerView, savedInstanceState: Bundle?)
}
