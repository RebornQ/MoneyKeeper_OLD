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

package me.bakumon.moneykeeper.ui.add.option

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.list.customListAdapter
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_option.*
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.Assets
import me.bakumon.moneykeeper.database.entity.Label
import me.bakumon.moneykeeper.database.entity.RecordTypeWithAsset
import me.bakumon.moneykeeper.ui.add.AssetsChooseViewBinder
import me.bakumon.moneykeeper.ui.assets.choose.ChooseAssetsActivity
import me.bakumon.moneykeeper.ui.common.BaseFragment
import me.bakumon.moneykeeper.utill.DateUtils
import me.bakumon.moneykeeper.utill.ResourcesUtil
import me.bakumon.moneykeeper.utill.SoftInputUtils
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import java.util.*

/**
 * 记一笔界面 的 "选择日期、备注、选择账户" 抽离成单独的 Fragment
 * 对外提供的功能：
 * 1.回显要修改的数据
 * 2.获取当前选择的日期
 * 3.获取备注、清除备注
 * 4.获取新选的账户、老账户
 * 5.控制可选择账户
 * @author Bakumon https://bakumon.me
 */
class OptionFragment : BaseFragment() {

    private lateinit var mViewModel: OptionViewModel

    private var mCurrentChooseDate: Date? = DateUtils.getTodayDate()
    private val mCurrentChooseCalendar = Calendar.getInstance()

    /**
     * 是否是修改
     */
    private var mIsModify = false

    private var mOnEditDoneListener: (() -> Unit)? = null

    private var mAssets: Assets? = null
    private var mOldAssets: Assets? = null

    override val layoutId: Int
        get() = R.layout.fragment_option

    override fun lazyInitData() {}

    override fun onInit(savedInstanceState: Bundle?) {
        mViewModel = getViewModel()
        // 选择日期
        tvDate.setOnClickListener { showDatePickerDialog() }
        mIsModify = arguments?.getBoolean(KEY_IS_MODIFY)!!
        val assetsId = arguments?.getInt(KEY_ASSETS_ID, -1)
        val remark = arguments?.getString(KEY_REMARK)
        val date = arguments?.getSerializable(KEY_DATE) as Date

        val isShowChooseAssets = arguments?.getBoolean(KEY_IS_SHOW_CHOOSE_ASSETS)!!
        llRecordAccount.visibility = if (isShowChooseAssets) View.VISIBLE else View.GONE


        edtRemark.setOnEditorActionListener { _, _, _ ->
            SoftInputUtils.hideSoftInput(edtRemark)
            mOnEditDoneListener?.invoke()
            false
        }

        if (!mIsModify) {
            if (DefaultSPHelper.isLastAssetOnlyOne) {
                val savedAssetsId = DefaultSPHelper.assetsId
                if (savedAssetsId == -1) {
                    updateAccountView(name = getString(R.string.text_no_choose_account))
                } else {
                    getAssetsAccount(savedAssetsId)
                }
            } else {
                updateAccountView(name = getString(R.string.text_no_choose_account))
            }
        } else {
            if (isShowChooseAssets) {
                if (assetsId == null || assetsId == -1) {
                    updateAccountView(name = getString(R.string.text_no_choose_account))
                } else {
                    getAssetsAccount(assetsId)
                }
            }
            edtRemark.setText(remark)
            mCurrentChooseDate = date
            mCurrentChooseCalendar.time = mCurrentChooseDate
            tvDate.text = DateUtils.getWordTime(mCurrentChooseDate!!)
        }

        llRecordAccount.setOnClickListener { chooseAccount() }
        ivRemark.setOnClickListener { showPopup() }
    }

    private var labelsLiveData: LiveData<List<Label>>? = null
    private fun showPopup() {
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        labelsLiveData = mViewModel.getLabels()
        labelsLiveData!!.observe(this, Observer { list ->
            if (list == null || list.isEmpty()) {
                showLabelTip()
            } else {
                showPopupMenu(list)
            }
        })
    }

    private fun showLabelTip() {
        MaterialDialog(context!!)
            .message(R.string.text_remark_tip)
            .positiveButton(R.string.text_know)
            .onDismiss {
                isDialogShow = false
                labelsLiveData?.removeObservers(this)
            }
            .show()
    }

    private fun showPopupMenu(list: List<Label>) {
        val popupMenu = PopupMenu(context!!, edtRemark)
        list.forEach {
            popupMenu.menu.add(it.name)
        }
        popupMenu.setOnMenuItemClickListener {
            edtRemark.setText(it.title)
            edtRemark.setSelection(edtRemark.text.length)
            SoftInputUtils.hideSoftInput(edtRemark)
            mOnEditDoneListener?.invoke()
            false
        }
        popupMenu.setOnDismissListener {
            labelsLiveData?.removeObservers(this)
            isDialogShow = false
        }
        popupMenu.show()
    }

    private var isDialogShow = false

    private fun showDatePickerDialog() {
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        val dpd = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                mCurrentChooseDate = DateUtils.getDate(year, monthOfYear + 1, dayOfMonth)
                mCurrentChooseCalendar.time = mCurrentChooseDate
                tvDate.text = DateUtils.getWordTime(mCurrentChooseDate!!)
            }, mCurrentChooseCalendar
        )
        dpd.maxDate = Calendar.getInstance()
        dpd.isThemeDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        @Suppress("DEPRECATION")
        dpd.show(
            activity!!.supportFragmentManager,
            TAG_PICKER_DIALOG
        )

        dpd.setOnDismissListener { isDialogShow = false }
    }

    private var assetsLiveData: LiveData<Assets>? = null

    private fun getAssetsAccount(id: Int) {
        assetsLiveData = mViewModel.getAssetsById(id)
        assetsLiveData!!.observe(this, Observer {
            if (it == null) {
                updateAccountView(name = getString(R.string.text_no_choose_account))
            } else {
                mAssets = it
                if (mIsModify) {
                    // 修改
                    mOldAssets = it
                }
                updateAccountView(it.imgName, it.name)
            }
        })
    }

    private fun updateAccountView(imgName: String = "", name: String) {
        if (imgName.isBlank()) {
            ivRecordAccount.visibility = View.GONE
        } else {
            ivRecordAccount.visibility = View.VISIBLE
            ivRecordAccount.setImageResource(ResourcesUtil.getTypeImgId(context!!, imgName))
        }
        tvRecordAccount.text = name

        // 防止数据库改变 assetsLiveData 自动 observe，导致 账户显示 oldAssets 的 name
        assetsLiveData?.removeObservers(this)
    }

    private var assetsListLiveData: LiveData<List<Assets>>? = null

    private fun chooseAccount() {
        assetsListLiveData = mViewModel.getAssets()
        assetsListLiveData!!.observe(this, Observer {
            if (it != null) {
                showListDialog(it)
            }
        })
    }

    private var mDialog: MaterialDialog? = null
    private fun showListDialog(list: List<Assets>) {
        val adapter = MultiTypeAdapter()
        adapter.register(Assets::class, AssetsChooseViewBinder { assetsItemClick(it) })
        adapter.register(
            NoAccount::class,
            AssetsNoChooseViewBinder { noAssetsItemClick(it) })
        val items = Items()
        items.addAll(list)
        items.add(
            NoAccount(
                0,
                getString(R.string.text_no_choose_account),
                getString(R.string.text_no_choose_account_tip)
            )
        )
        items.add(NoAccount(1, getString(R.string.text_new_choose_account)))
        adapter.items = items

        if (isDialogShow) {
            return
        }
        isDialogShow = true
        mDialog = MaterialDialog(context!!)
            .title(R.string.text_choose_account)
            .customListAdapter(adapter)
            .positiveButton(res = R.string.text_cancel)
            .onDismiss {
                isDialogShow = false
                // 防止数据库改变 assetsListLiveData 自动 observe，导致 dialog 重复弹起
                assetsListLiveData?.removeObservers(this)
            }
        mDialog?.show()
    }

    private fun assetsItemClick(assets: Assets) {
        mAssets = assets
        updateAccountView(assets.imgName, assets.name)
        // 更新上次使用的 assets id
        DefaultSPHelper.assetsId = assets.id!!
        mDialog?.dismiss()
    }

    private fun noAssetsItemClick(noAccount: NoAccount) {
        if (noAccount.type == 0) {
            // 不选择账户
            mAssets = null
            updateAccountView(name = getString(R.string.text_no_choose_account))
            // 更新上次使用的 assets id
            DefaultSPHelper.assetsId = -1
        } else {
            // 添加账户
            ChooseAssetsActivity.open(context!!)
        }
        mDialog?.dismiss()
    }

    fun setEditDoneListener(onEditDoneListener: (() -> Unit)) {
        mOnEditDoneListener = onEditDoneListener
    }

    fun setAssetsVisibility(visibility: Boolean) {
        if (llRecordAccount != null) {
            llRecordAccount.visibility = if (visibility) View.VISIBLE else View.GONE
        }
    }

    fun getOldAssets(): Assets? {
        return mOldAssets
    }

    fun getNewAssets(): Assets? {
        return mAssets
    }

    fun getRemark(): String {
        return edtRemark.text.toString().trim()
    }

    fun clearRemark() {
        edtRemark.setText("")
    }

    fun getDate(): Date? {
        return mCurrentChooseDate
    }

    fun onTypeChecked(type: RecordTypeWithAsset?) {
        if (!DefaultSPHelper.isLastAssetByType) {
            return
        }
        if (type?.mAssets?.singleOrNull() != null) {
            mAssets = type.mAssets?.get(0)!!
            updateAccountView(mAssets!!.imgName, mAssets!!.name)
        } else {
            mAssets = null
            updateAccountView(name = getString(R.string.text_no_choose_account))
        }
    }

    companion object {
        private const val TAG_PICKER_DIALOG = "Datepickerdialog"
        private const val KEY_IS_SHOW_CHOOSE_ASSETS = "KEY_IS_SHOW_CHOOSE_ASSETS"
        private const val KEY_IS_MODIFY = "KEY_IS_MODIFY"
        private const val KEY_ASSETS_ID = "KEY_ASSETS_ID"
        private const val KEY_DATE = "KEY_DATE"
        private const val KEY_REMARK = "KEY_REMARK"
        fun newInstance(
            isShowChooseAssets: Boolean = true,
            isModify: Boolean = false,
            assetsId: Int = -1,
            date: Date? = DateUtils.getTodayDate(),
            remark: String = ""
        ): OptionFragment {
            val fragment = OptionFragment()
            val bundle = Bundle()
            bundle.putBoolean(KEY_IS_SHOW_CHOOSE_ASSETS, isShowChooseAssets)
            bundle.putBoolean(KEY_IS_MODIFY, isModify)
            bundle.putInt(KEY_ASSETS_ID, assetsId)
            bundle.putSerializable(KEY_DATE, date)
            bundle.putString(KEY_REMARK, remark)
            fragment.arguments = bundle
            return fragment
        }
    }
}
