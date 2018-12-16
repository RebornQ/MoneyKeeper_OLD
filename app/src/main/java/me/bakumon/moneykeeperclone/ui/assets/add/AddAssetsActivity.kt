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

package me.bakumon.moneykeeperclone.ui.assets.add

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.list.customListAdapter
import kotlinx.android.synthetic.main.activity_add_assets.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.base.ErrorResource
import me.bakumon.moneykeeperclone.base.SuccessResource
import me.bakumon.moneykeeperclone.database.entity.Assets
import me.bakumon.moneykeeperclone.ui.assets.choose.AssetsType
import me.bakumon.moneykeeperclone.ui.common.BaseActivity
import me.bakumon.moneykeeperclone.utill.*
import me.bakumon.moneykeeperclone.view.KeyboardDialog
import me.drakeet.floo.Floo
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * AddAssetsActivity
 *
 * @author Bakumon https://bakumon.me
 */
class AddAssetsActivity : BaseActivity() {

    private lateinit var mViewModel: AddAssetsViewModel
    private var mAssetsType: AssetsType? = null
    private var mAssets: Assets? = null
    /**
     * 0：新增
     * 1：修改
     */
    private var mType: Int = 0

    private lateinit var mParamImgName: String

    private var mDialog: MaterialDialog? = null

    override val layoutId: Int
        get() = R.layout.activity_add_assets

    override fun onInitView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbarLayout as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        // 新建
        mAssetsType = intent.getSerializableExtra(Router.ExtraKey.KEY_ASSETS_TYPE) as? AssetsType
        // 修改
        mAssets = intent.getSerializableExtra(Router.ExtraKey.KEY_ASSETS) as? Assets
        mType = if (mAssets == null) 0 else 1

        setView()
        mViewModel = getViewModel()
    }

    private fun setView() {
        if (mAssetsType == null && mAssets == null) {
            finish()
        }
        val title: String
        val assetsName: String
        val type: Int
        val remark: String
        val money: String
        if (mType == 0) {
            // 新建
            title = getString(R.string.text_add_assets)
            mParamImgName = mAssetsType!!.imgName
            assetsName = mAssetsType!!.assetsName
            type = mAssetsType!!.type
            remark = ""
            money = ""
        } else {
            // 修改
            title = getString(R.string.text_edit_assets)
            mParamImgName = mAssets!!.imgName
            assetsName = mAssets!!.name
            type = mAssets!!.type
            remark = mAssets!!.remark
            money = BigDecimalUtil.fen2YuanNoSeparator(mAssets!!.money)
        }
        toolbarLayout.tvTitle.text = title
        ivType.setImageResource(ResourcesUtil.getTypeImgId(this, mParamImgName))
        edtTypeName.setText(assetsName)
        edtTypeName.setSelection(edtTypeName.text.length)
        if (type == 2) {
            // 银行卡
            ivTypeRight.visibility = View.VISIBLE
            llType.setOnClickListener { chooseBank() }
        }
        edtRemark.setText(remark)
        edtMoney.setText(money)

        val keyboardDialog = KeyboardDialog(this, edtMoney.text.toString()) {
            edtMoney.setText(it)
            edtMoney.setSelection(edtMoney.text.length)
            SoftInputUtils.hideSoftInput(edtMoney)
            if (!edtMoney.isFocused) {
                edtMoney.requestFocus()
            }
        }

        edtMoney.setOnTouchListener { _, _ ->
            SoftInputUtils.hideSoftInput(edtMoney)
            keyboardDialog.show()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_type, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_save -> saveAssets()
            android.R.id.home -> finish()
        }
        return true
    }

    private var isDialogShow = false

    private fun chooseBank() {
        val adapter = MultiTypeAdapter()
        adapter.register(Bank::class, BankViewBinder { bankItemClick(it) })
        val items = Items()
        BankItemsCreator.addAll(items)
        adapter.items = items
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        mDialog = MaterialDialog(this)
                .title(R.string.text_choose_bank)
                .customListAdapter(adapter)
                .positiveButton(res = R.string.text_cancel)
                .onDismiss { isDialogShow = false }
        mDialog?.show()
    }

    private fun bankItemClick(item: Bank) {
        mParamImgName = item.imgName
        ivType.setImageResource(ResourcesUtil.getTypeImgId(this, mParamImgName))
        edtTypeName.setText(item.name)
        edtTypeName.setSelection(edtTypeName.text.length)
        mDialog?.dismiss()
    }

    /**
     * 防止重复点击
     */
    private var isSaveEnable = true

    private fun saveAssets() {
        if (!isSaveEnable) {
            return
        }
        if (edtTypeName.text.isBlank()) {
            ViewUtil.startShake(edtTypeName)
            return
        }
        isSaveEnable = false
        SoftInputUtils.hideSoftInput(edtTypeName)

        val typeName = edtTypeName.text.toString().trim()
        val remark = edtRemark.text.toString().trim()
        val money = BigDecimalUtil.yuan2FenBD(edtMoney.text.toString())

        val liveData = if (mType == 0) {
            // 新增
            val assets = Assets(name = typeName, imgName = mParamImgName, type = mAssetsType!!.type, remark = remark, money = money, initMoney = money)
            mViewModel.addAssets(assets)
        } else {
            // 修改
            mAssets!!.name = typeName
            mAssets!!.imgName = mParamImgName
            mAssets!!.remark = remark
            val moneyBefore = mAssets!!.money
            mAssets!!.money = money
            mViewModel.updateAssets(moneyBefore, mAssets!!)
        }
        liveData.observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    if (mType == 0) {
                        Floo.stack(this).popCount(2).start()
                    } else {
                        finish()
                    }
                }
                is ErrorResource<Boolean> -> {
                    ToastUtils.show(R.string.toast_save_assets_fail)
                    isSaveEnable = true
                }
            }
        })
    }
}
