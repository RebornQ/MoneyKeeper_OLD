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

package me.bakumon.moneykeeperclone.ui.addtype

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_add_type.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.base.ErrorResource
import me.bakumon.moneykeeperclone.base.SuccessResource
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.ui.common.BaseActivity
import me.bakumon.moneykeeperclone.utill.ToastUtils
import me.bakumon.moneykeeperclone.utill.ViewUtil
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * 添加或修改记账类型
 *
 * @author Bakumon https://bakumon.me
 */
class AddTypeActivity : BaseActivity() {

    private lateinit var mViewModel: AddTypeViewModel
    private lateinit var adapter: MultiTypeAdapter

    private var mType: Int = 0
    private var mRecordType: RecordType? = null
    private var mCurrentItem: TypeImgBean? = null

    override val layoutId: Int
        get() = R.layout.activity_add_type

    override fun onInitView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbarLayout as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    @SuppressLint("SetTextI18n")
    override fun onInit(savedInstanceState: Bundle?) {
        mType = intent.getIntExtra(Router.ExtraKey.KEY_TYPE, RecordType.TYPE_OUTLAY)
        mRecordType = intent.getSerializableExtra(Router.ExtraKey.KEY_TYPE_BEAN) as RecordType?

        val prefix = if (mRecordType == null) getString(R.string.text_add) else getString(R.string.text_modify)
        val type = if (mType == RecordType.TYPE_OUTLAY) getString(R.string.text_outlay_type) else getString(R.string.text_income_type)

        toolbarLayout.tvTitle.text = prefix + type

        edt_type_name.setText(mRecordType?.name)
        edt_type_name.setSelection(edt_type_name.text.length)

        adapter = MultiTypeAdapter()
        adapter.register(TypeImgBean::class, TypeImgViewBinder { checkedItem(it) })
        rv_type.adapter = adapter

        mViewModel = getViewModel()
        getAllTypeImg()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_type, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_save -> saveType()
            android.R.id.home -> finish()
        }
        return true
    }

    private fun checkedItem(position: Int) {
        for (i in 0 until adapter.items.size) {
            (adapter.items[i] as TypeImgBean).isChecked = i == position
        }
        mCurrentItem = adapter.items[position] as TypeImgBean
        val resId = resources.getIdentifier(mCurrentItem?.imgName, "mipmap", packageName)
        iv_type.setImageResource(resId)
        adapter.notifyDataSetChanged()
    }

    private fun getAllTypeImg() {
        mViewModel.getAllTypeImgBeans(mType).observe(this, Observer {
            if (it != null) {
                setItems(it)
            }
        })
    }

    private fun setItems(typeImgBeans: List<TypeImgBean>) {
        val items = Items()

        var checkedPosition = 0
        for (i in typeImgBeans.indices) {
            if (TextUtils.equals(mRecordType?.imgName, typeImgBeans[i].imgName)) {
                checkedPosition = i
                break
            }
        }
        items.addAll(typeImgBeans)
        adapter.items = items
        checkedItem(checkedPosition)
    }

    /**
     * 防止重复点击
     */
    private var isSaveEnable = true

    private fun saveType() {
        if (!isSaveEnable) {
            return
        }
        isSaveEnable = false
        val text = edt_type_name.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(text)) {
            ViewUtil.startShake(edt_type_name)
            isSaveEnable = true
            return
        }
        if (mCurrentItem == null) {
            return
        }
        mViewModel.saveRecordType(mRecordType, mType, mCurrentItem!!.imgName, text).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> finish()
                is ErrorResource<Boolean> -> {
                    isSaveEnable = true
                    ToastUtils.show(getString(R.string.toast_type_save_fail) + "\n" + it.errorMessage)
                }
            }
        })
    }
}
