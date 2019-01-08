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

package me.bakumon.moneykeeper.ui.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_add_record.*
import kotlinx.android.synthetic.main.layout_three_choose.view.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.base.ErrorResource
import me.bakumon.moneykeeper.base.SuccessResource
import me.bakumon.moneykeeper.database.entity.*
import me.bakumon.moneykeeper.ui.add.option.OptionFragment
import me.bakumon.moneykeeper.ui.add.recordtype.RecordTypeFragment
import me.bakumon.moneykeeper.ui.add.transfer.TransferAssetsFragment
import me.bakumon.moneykeeper.ui.common.BaseActivity
import me.bakumon.moneykeeper.ui.common.FragmentViewPagerAdapter
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.utill.DateUtils
import me.bakumon.moneykeeper.utill.ToastUtils
import me.bakumon.moneykeeper.widget.WidgetProvider
import java.util.*

/**
 * 记一笔
 *
 * @author bakumon https://bakumon.me
 * @date 2018/4/9
 */
class AddRecordActivity : BaseActivity() {
    private lateinit var mViewModel: AddRecordViewModel

    private lateinit var mOutlayTypeFragment: RecordTypeFragment
    private lateinit var mTransferAssetsFragment: TransferAssetsFragment
    private lateinit var mIncomeTypeFragment: RecordTypeFragment
    private lateinit var mOptionFragment: OptionFragment

    private var mCurrentType: Int = RecordType.TYPE_OUTLAY

    private var mRecord: RecordForList? = null
    private var mTransfer: AssetsTransferRecordWithAssets? = null
    /**
     * 连续记账
     */
    private var mIsSuccessive: Boolean = false
    /**
     * 只转账
     */
    private var mIsTransfer: Boolean = false

    override val layoutId: Int
        get() = R.layout.activity_add_record

    override fun onInitView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbarLayout as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        typeChoose.rbLeft.setText(R.string.text_outlay)
        typeChoose.rbMiddle.setText(R.string.text_transfer)
        typeChoose.rbRight.setText(R.string.text_income)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        mViewModel = getViewModel()

        mIsSuccessive = intent.getBooleanExtra(KEY_IS_SUCCESSIVE, false)
        mRecord = intent.getSerializableExtra(KEY_RECORD_BEAN) as RecordForList?
        mTransfer = intent.getSerializableExtra(KEY_TRANSFER) as AssetsTransferRecordWithAssets?


        mIsTransfer = intent.getBooleanExtra(KEY_IS_TRANSFER, false)


        val isModify: Boolean
        val assetsId: Int
        val date: Date?
        val remark: String

        when {
            mRecord != null -> {
                isModify = true
                assetsId = if (mRecord!!.assetsId == null) -1 else mRecord!!.assetsId!!
                date = mRecord!!.time
                remark = mRecord!!.remark!!
            }
            mTransfer != null -> {
                isModify = true
                assetsId = -1
                date = mTransfer!!.time
                remark = mTransfer!!.remark
            }
            else -> {
                isModify = false
                assetsId = -1
                date = DateUtils.getTodayDate()
                remark = ""
            }
        }

        mOptionFragment = OptionFragment.newInstance(
            isShowChooseAssets = !mIsTransfer,
            isModify = isModify,
            assetsId = assetsId,
            date = date,
            remark = remark
        )
        mOptionFragment.setEditDoneListener { keyboard.setEditTextFocus() }
        supportFragmentManager.beginTransaction()
            .add(R.id.flOptions, mOptionFragment)
            .commit()

        if (mRecord == null) {
            // 新增
            // 设置标题
            toolbarLayout.tvTitle.text =
                    getString(if (mIsSuccessive) R.string.text_add_record_successive else R.string.text_add_record)
            // 设置 fragment
            mOutlayTypeFragment = RecordTypeFragment.newInstance(RecordType.TYPE_OUTLAY)
            mIncomeTypeFragment = RecordTypeFragment.newInstance(RecordType.TYPE_INCOME)
            if (mTransfer != null) {
                mTransferAssetsFragment = TransferAssetsFragment.newInstance(mTransfer)
                // 回显数据
                keyboard.setText(BigDecimalUtil.fen2YuanNoSeparator(mTransfer!!.money))
            } else {
                mTransferAssetsFragment = TransferAssetsFragment.newInstance()
            }
            val adapter = FragmentViewPagerAdapter(
                supportFragmentManager,
                arrayListOf(mOutlayTypeFragment, mTransferAssetsFragment, mIncomeTypeFragment)
            )
            viewPager.adapter = adapter
            viewPager.offscreenPageLimit = 3
            // 设置【转账】是否显示
            typeChoose.rbMiddle.visibility = View.VISIBLE
            // 选中【支出】
            (typeChoose as RadioGroup).check(R.id.rbLeft)
        } else {
            // 修改
            // 设置标题
            toolbarLayout.tvTitle.text = getString(R.string.text_modify_record)
            // 设置 fragment
            mOutlayTypeFragment = RecordTypeFragment.newInstance(RecordType.TYPE_OUTLAY, mRecord)
            mIncomeTypeFragment = RecordTypeFragment.newInstance(RecordType.TYPE_INCOME, mRecord)
            val adapter =
                FragmentViewPagerAdapter(supportFragmentManager, arrayListOf(mOutlayTypeFragment, mIncomeTypeFragment))
            viewPager.adapter = adapter
            viewPager.offscreenPageLimit = 2
            // 设置【转账】是否显示
            typeChoose.rbMiddle.visibility = View.GONE
            // 选中【支出】或【收入】
            (typeChoose as RadioGroup).check(if (mRecord!!.type == RecordType.TYPE_OUTLAY) R.id.rbLeft else R.id.rbRight)
            // 回显数据
            keyboard.setText(BigDecimalUtil.fen2YuanNoSeparator(mRecord!!.money))
        }
        mOutlayTypeFragment.setOnCheckTypeListener {
            mOptionFragment.onTypeChecked(it)
        }
        mIncomeTypeFragment.setOnCheckTypeListener {
            mOptionFragment.onTypeChecked(it)
        }

        if (mIsTransfer) {
            checkMiddle()
            typeChoose.visibility = View.GONE
            toolbarLayout.tvTitle.text = getString(R.string.text_transfer)
        }

        // 切换【支出】【转账】【收入】
        (typeChoose as RadioGroup).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbLeft -> {
                    mCurrentType = RecordType.TYPE_OUTLAY
                    mOptionFragment.setAssetsVisibility(true)
                    viewPager.setCurrentItem(0, false)
                    mOptionFragment.onTypeChecked(mOutlayTypeFragment.getType())
                }
                R.id.rbMiddle -> {
                    checkMiddle()
                }
                R.id.rbRight -> {
                    mCurrentType = RecordType.TYPE_INCOME
                    mOptionFragment.setAssetsVisibility(true)
                    viewPager.setCurrentItem(2, false)
                    mOptionFragment.onTypeChecked(mIncomeTypeFragment.getType())
                }
            }
        }
        // 提交
        keyboard.mOnAffirmClickListener = {
            if (mRecord == null && mTransfer == null) {
                if (mCurrentType == TYPE_TRANSFER) {
                    insertTransfer(it)
                } else {
                    insertRecord(it)
                }
            } else {
                if (mRecord != null) {
                    modifyRecord(it)
                } else if (mTransfer != null) {
                    modifyTransfer(it)
                }
            }
        }
    }

    private fun checkMiddle(){
        mCurrentType = TYPE_TRANSFER
        mOptionFragment.setAssetsVisibility(false)
        viewPager.setCurrentItem(1, false)
    }

    /**
     * 正在提交
     */
    private var isSubmitting = false

    private fun insertRecord(input: String) {
        // 防止重复提交
        keyboard.setAffirmEnable(false)
        isSubmitting = true
        val record = Record()
        record.money = BigDecimalUtil.yuan2FenBD(input)
        record.remark = mOptionFragment.getRemark()
        record.time = mOptionFragment.getDate()
        record.createTime = Date()

        val recordType = if (mCurrentType == RecordType.TYPE_OUTLAY)
            mOutlayTypeFragment.getType()!!
        else
            mIncomeTypeFragment.getType()!!

        record.recordTypeId = recordType.id

        mViewModel.insertRecord(recordType, mOptionFragment.getNewAssets(), record).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> insertRecordDone()
                is ErrorResource<Boolean> -> {
                    keyboard.setAffirmEnable(true)
                    isSubmitting = false
                    ToastUtils.show(R.string.toast_add_record_fail)
                }
            }
        })
    }

    /**
     * 新增记账记录完成
     */
    private fun insertRecordDone() {
        isSubmitting = false
        // 更新 widget
        WidgetProvider.updateWidget(this)
        if (mIsSuccessive) {
            // 继续记账，清空输入
            keyboard.setText("")
            mOptionFragment.clearRemark()
            keyboard.setAffirmEnable(true)
            ToastUtils.show(R.string.toast_success_record)
        } else {
            finish()
        }
    }

    private fun insertTransfer(input: String) {
        if (mTransferAssetsFragment.getOutAssets() == null) {
            return
        }
        if (mTransferAssetsFragment.getInAssets() == null) {
            return
        }
        if (mTransferAssetsFragment.getOutAssets()!!.id!! == mTransferAssetsFragment.getInAssets()!!.id!!) {
            ToastUtils.show(R.string.toast_choose_account)
            return
        }
        // 防止重复提交
        keyboard.setAffirmEnable(false)
        isSubmitting = true
        val transferRecord = AssetsTransferRecord(
            mTransferAssetsFragment.getOutAssets()!!.id!!,
            mTransferAssetsFragment.getInAssets()!!.id!!,
            BigDecimalUtil.yuan2FenBD(input), mOptionFragment.getDate()!!, mOptionFragment.getRemark()
        )
        mViewModel.addTransferRecord(
            mTransferAssetsFragment.getOutAssets()!!,
            mTransferAssetsFragment.getInAssets()!!,
            transferRecord
        ).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    addTransferDone()
                }
                is ErrorResource<Boolean> -> {
                    isSubmitting = false
                    keyboard.setAffirmEnable(true)
                    ToastUtils.show(R.string.toast_save_assets_fail)
                }
            }
        })
    }

    /**
     * 新增转账记录完成
     */
    private fun addTransferDone() {
        isSubmitting = false
        if (mIsSuccessive) {
            // 继续记账，清空输入
            keyboard.setText("")
            mOptionFragment.clearRemark()
            keyboard.setAffirmEnable(true)
            ToastUtils.show(R.string.toast_success_record)
        } else {
            finish()
        }
    }

    private fun modifyTransfer(text: String) {
        if (mTransferAssetsFragment.getOutAssets() == null) {
            return
        }
        if (mTransferAssetsFragment.getInAssets() == null) {
            return
        }
        if (mTransferAssetsFragment.getOldOutAssets() == null) {
            ToastUtils.show(R.string.toast_out_assets_null)
            return
        }
        if (mTransferAssetsFragment.getOldInAssets() == null) {
            ToastUtils.show(R.string.toast_in_assets_null)
            return
        }
        if (mTransferAssetsFragment.getOutAssets()!!.id!! == mTransferAssetsFragment.getInAssets()!!.id!!) {
            ToastUtils.show(R.string.toast_choose_account)
            return
        }
        if (mOptionFragment.getDate() == null) {
            ToastUtils.show(R.string.toast_date_null)
            return
        }
        isSubmitting = true
        keyboard.setAffirmEnable(false)
        val oldMoney = mTransfer!!.money
        mTransfer!!.time = mOptionFragment.getDate()!!
        mTransfer!!.assetsIdFrom = mTransferAssetsFragment.getOutAssets()!!.id!!
        mTransfer!!.assetsIdTo = mTransferAssetsFragment.getInAssets()!!.id!!
        mTransfer!!.money = BigDecimalUtil.yuan2FenBD(text)
        mTransfer!!.remark = mOptionFragment.getRemark()

        mViewModel.updateTransferRecord(
            oldMoney = oldMoney,
            oldOutAssets = mTransferAssetsFragment.getOldOutAssets()!!,
            oldInAssets = mTransferAssetsFragment.getOldInAssets()!!,
            outAssets = mTransferAssetsFragment.getOutAssets()!!,
            inAssets = mTransferAssetsFragment.getInAssets()!!,
            transferRecord = mTransfer!!
        ).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    isSubmitting = false
                    finish()
                }
                is ErrorResource<Boolean> -> {
                    isSubmitting = false
                    keyboard.setAffirmEnable(true)
                    ToastUtils.show(R.string.toast_move_backup_files_fail)
                }
            }
        })
    }

    private fun modifyRecord(text: String) {
        // 防止重复提交
        keyboard.setAffirmEnable(false)
        isSubmitting = true
        val oldType = mRecord!!.type
        val oldMoney = mRecord!!.money!!
        mRecord!!.money = BigDecimalUtil.yuan2FenBD(text)
        mRecord!!.remark = mOptionFragment.getRemark()
        mRecord!!.time = mOptionFragment.getDate()

        val recordType = if (mCurrentType == RecordType.TYPE_OUTLAY)
            mOutlayTypeFragment.getType()!!
        else
            mIncomeTypeFragment.getType()!!
        mRecord!!.recordTypeId = recordType.id

        mViewModel.updateRecord(
            oldMoney,
            oldType,
            mCurrentType,
            mOptionFragment.getOldAssets(),
            mOptionFragment.getNewAssets(),
            mRecord!!,
            recordType
        ).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    // 更新 widget
                    WidgetProvider.updateWidget(this)
                    isSubmitting = false
                    finish()
                }
                is ErrorResource<Boolean> -> {
                    keyboard.setAffirmEnable(true)
                    isSubmitting = false
                    ToastUtils.show(R.string.toast_modify_record_fail)
                }
            }
        })
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (isSubmitting) {
            ToastUtils.show(R.string.toast_submit_now)
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TYPE_TRANSFER = 2

        private const val KEY_IS_SUCCESSIVE = "KEY_IS_SUCCESSIVE"
        private const val KEY_RECORD_BEAN = "KEY_RECORD_BEAN"
        private const val KEY_TRANSFER = "KEY_TRANSFER"
        private const val KEY_IS_TRANSFER = "KEY_IS_TRANSFER"

        fun open(
            context: Context,
            isSuccessive: Boolean = false,
            record: RecordForList? = null,
            transferRecord: AssetsTransferRecordWithAssets? = null,
            isTransfer: Boolean = false
        ) {
            val intent = Intent(context, AddRecordActivity::class.java)
            intent.putExtra(KEY_IS_SUCCESSIVE, isSuccessive)
            intent.putExtra(KEY_RECORD_BEAN, record)
            intent.putExtra(KEY_TRANSFER, transferRecord)
            intent.putExtra(KEY_IS_TRANSFER, isTransfer)
            context.startActivity(intent)
        }
    }
}
