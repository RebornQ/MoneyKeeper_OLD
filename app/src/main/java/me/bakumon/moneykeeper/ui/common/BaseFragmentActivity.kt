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
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeper.R

abstract class BaseFragmentActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.activity_content

    override fun onInitView(savedInstanceState: Bundle?) {
        toolbarLayout.tvTitle.text = setTitle()
        setSupportActionBar(toolbarLayout as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, setFragment())
            .commit()
    }

    /**
     * 设置 title
     */
    protected abstract fun setTitle(): String

    /**
     * 内嵌的 fragment
     */
    protected abstract fun setFragment(): Fragment

}
