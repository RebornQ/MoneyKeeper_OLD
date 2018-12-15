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

package me.bakumon.moneykeeper.ui.add.transfer

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.list.customListAdapter
import kotlinx.android.synthetic.main.fragment_transfer_assets.*
import kotlinx.android.synthetic.main.layout_transfer_account.view.*
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.Assets
import me.bakumon.moneykeeper.database.entity.AssetsTransferRecordWithAssets
import me.bakumon.moneykeeper.ui.add.AssetsChooseViewBinder
import me.bakumon.moneykeeper.ui.common.BaseFragment
import me.bakumon.moneykeeper.utill.ResourcesUtil
import me.bakumon.moneykeeper.utill.ToastUtils
import me.bakumon.moneykeeper.utill.ViewUtil
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * 记一笔 界面转账 fragment，实现选择 转出 转入账户
 * 对外提供的功能：
 * 1.获取转出账户
 * 2.获取转入账户
 *
 * @author Bakumon https://bakumon.me
 */
class TransferAssetsFragment : BaseFragment() {

    private lateinit var mViewModel: TransferAssetsViewModel

    private var mTransfer: AssetsTransferRecordWithAssets? = null
    private lateinit var mCurrentType: String
    private var mOutAssets: Assets? = null
    private var mInAssets: Assets? = null

    private var mOldOutAssets: Assets? = null
    private var mOldInAssets: Assets? = null

    override val layoutId: Int
        get() = R.layout.fragment_transfer_assets

    override fun onInit(savedInstanceState: Bundle?) {
        mViewModel = getViewModel()
        mTransfer = arguments?.getSerializable(KEY_TRANSFER) as AssetsTransferRecordWithAssets?

        if (mTransfer == null) {
            outAccount.ivAccount.visibility = View.GONE
            outAccount.tvAccountName.text = getString(R.string.text_choose_out_account)
            outAccount.tvAccountName.setTextColor(ContextCompat.getColor(context!!, R.color.colorTextHint))
            outAccount.tvAccountRemark.visibility = View.GONE

            inAccount.ivAccount.visibility = View.GONE
            inAccount.tvAccountName.text = getString(R.string.text_choose_in_account)
            inAccount.tvAccountName.setTextColor(ContextCompat.getColor(context!!, R.color.colorTextHint))
            inAccount.tvAccountRemark.visibility = View.GONE
        } else {
            mViewModel.getAssetsById(mTransfer!!.assetsIdFrom).observe(this, Observer {
                if (it != null) {
                    mOldOutAssets = it
                    mOutAssets = it
                    setOutAssetsView(it)
                }
            })
            mViewModel.getAssetsById(mTransfer!!.assetsIdTo).observe(this, Observer {
                if (it != null) {
                    mOldInAssets = it
                    mInAssets = it
                    setInAssetsView(it)
                }
            })
        }


        outAccount.setOnClickListener { chooseAccount(TYPE_OUT_ACCOUNT) }
        inAccount.setOnClickListener { chooseAccount(TYPE_IN_ACCOUNT) }


    }

    override fun lazyInitData() {

    }

    private var assetsLiveData: LiveData<List<Assets>>? = null

    private fun chooseAccount(type: String) {
        mCurrentType = type
        assetsLiveData = mViewModel.getAssets()
        assetsLiveData!!.observe(this, Observer {
            if (it != null) {
                showListDialog(it)
            }
        })
    }

    private var mDialog: MaterialDialog? = null
    private var isDialogShow = false
    private fun showListDialog(list: List<Assets>) {
        val adapter = MultiTypeAdapter()
        adapter.register(Assets::class, AssetsChooseViewBinder { assetsItemClick(it) })
        val items = Items()
        items.addAll(list)
        adapter.items = items

        if (isDialogShow) {
            return
        }
        if (items.isEmpty()) {
            ToastUtils.show("还没有创建资产账户")
            return
        }
        isDialogShow = true
        mDialog = MaterialDialog(context!!)
                .title(R.string.text_choose_account)
                .customListAdapter(adapter)
                .positiveButton(res = R.string.text_cancel)
                .onDismiss {
                    isDialogShow = false
                    assetsLiveData?.removeObservers(this)
                }
        mDialog?.show()
    }

    private fun assetsItemClick(assets: Assets) {
        if (mCurrentType == TYPE_OUT_ACCOUNT) {
            mOutAssets = assets
            setOutAssetsView(assets)
        } else {
            mInAssets = assets
            setInAssetsView(assets)
        }
        mDialog?.dismiss()
    }

    private fun setOutAssetsView(assets: Assets) {
        outAccount.ivAccount.visibility = View.VISIBLE
        outAccount.ivAccount.setImageResource(ResourcesUtil.getTypeImgId(context!!, assets.imgName))
        outAccount.tvAccountName.text = assets.name
        outAccount.tvAccountName.setTextColor(ContextCompat.getColor(context!!, R.color.colorText))
        if (assets.remark.isBlank()) {
            outAccount.tvAccountRemark.visibility = View.GONE
        } else {
            outAccount.tvAccountRemark.visibility = View.VISIBLE
            outAccount.tvAccountRemark.text = assets.remark
        }
    }

    private fun setInAssetsView(assets: Assets) {
        inAccount.ivAccount.visibility = View.VISIBLE
        inAccount.ivAccount.setImageResource(ResourcesUtil.getTypeImgId(context!!, assets.imgName))
        inAccount.tvAccountName.text = assets.name
        inAccount.tvAccountName.setTextColor(ContextCompat.getColor(context!!, R.color.colorText))
        if (assets.remark.isBlank()) {
            inAccount.tvAccountRemark.visibility = View.GONE
        } else {
            inAccount.tvAccountRemark.visibility = View.VISIBLE
            inAccount.tvAccountRemark.text = assets.remark
        }
    }

    fun getOutAssets(): Assets? {
        if (mOutAssets == null) {
            ViewUtil.startShake(outAccount)
        }
        return mOutAssets
    }

    fun getInAssets(): Assets? {
        if (mInAssets == null) {
            ViewUtil.startShake(inAccount)
        }
        return mInAssets
    }

    fun getOldOutAssets(): Assets? {
        return mOldOutAssets
    }

    fun getOldInAssets(): Assets? {
        return mOldInAssets
    }

    companion object {
        private const val KEY_TRANSFER = "KEY_TRANSFER"
        private const val TYPE_OUT_ACCOUNT = "type_out_account"
        private const val TYPE_IN_ACCOUNT = "type_in_account"
        fun newInstance(transfer: AssetsTransferRecordWithAssets? = null): TransferAssetsFragment {
            val fragment = TransferAssetsFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_TRANSFER, transfer)
            fragment.arguments = bundle
            return fragment
        }
    }
}
