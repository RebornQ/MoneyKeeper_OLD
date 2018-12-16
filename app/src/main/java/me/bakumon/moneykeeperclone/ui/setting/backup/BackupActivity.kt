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

package me.bakumon.moneykeeperclone.ui.setting.backup

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.jakewharton.processphoenix.ProcessPhoenix
import me.bakumon.moneykeeperclone.CloudBackupService
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.Constant
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.api.ApiErrorResponse
import me.bakumon.moneykeeperclone.api.ApiSuccessResponse
import me.bakumon.moneykeeperclone.api.Network
import me.bakumon.moneykeeperclone.base.EmptyResource
import me.bakumon.moneykeeperclone.base.ErrorResource
import me.bakumon.moneykeeperclone.base.SuccessResource
import me.bakumon.moneykeeperclone.ui.common.AbsListActivity
import me.bakumon.moneykeeperclone.ui.home.HomeActivity
import me.bakumon.moneykeeperclone.ui.setting.Category
import me.bakumon.moneykeeperclone.ui.setting.CategoryViewBinder
import me.bakumon.moneykeeperclone.ui.setting.NormalItem
import me.bakumon.moneykeeperclone.ui.setting.NormalItemViewBinder
import me.bakumon.moneykeeperclone.utill.AndroidUtil
import me.bakumon.moneykeeperclone.utill.ToastUtils
import me.drakeet.floo.Floo
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import okhttp3.HttpUrl
import okhttp3.ResponseBody

/**
 * 云备份
 *
 * @author Bakumon https://bakumon.me
 */
class BackupActivity : AbsListActivity() {
    private lateinit var mViewModel: BackupViewModel

    override fun onSetupTitle(tvTitle: TextView) {
        tvTitle.text = getString(R.string.text_cloud_backup)
    }

    override fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(Category::class, CategoryViewBinder())
        adapter.register(NormalItem::class, NormalItemViewBinder { normalItem: NormalItem, view: View -> onNormalItemClick(normalItem, view) })
    }

    override fun onItemsCreated(items: Items) {
        items.add(Category(getString(R.string.text_webdav)))
        items.add(NormalItem(getString(R.string.text_webdav_url), ConfigManager.webDavUrl))
        items.add(NormalItem(getString(R.string.text_webdav_account), ConfigManager.webDavAccount))
        items.add(NormalItem(getString(R.string.text_webdav_password), getItemDisplayPsw()))
        items.add(NormalItem(getString(R.string.text_go_backup), getString(R.string.text_backup_save, getString(R.string.text_webdav) + BackupConstant.BACKUP_FILE)))
        items.add(NormalItem(getString(R.string.text_restore), getString(R.string.text_backup_save, getString(R.string.text_restore_content, getString(R.string.text_webdav) + BackupConstant.BACKUP_FILE))))
        items.add(NormalItem(getString(R.string.text_auto_backup_mode_title), getBackupModeStr(), ConfigManager.cloudEnable))
        items.add(NormalItem(getString(R.string.text_webdav_help), Constant.NUTSTORE_HELP_URL))
    }

    override fun onParentInitDone(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        mViewModel = getViewModel()
        initDir()
    }

    private fun onNormalItemClick(item: NormalItem, view: View) {
        when (item.title) {
            getString(R.string.text_webdav_url) -> setUrl()
            getString(R.string.text_webdav_account) -> setAccount()
            getString(R.string.text_webdav_password) -> setPsw()
            getString(R.string.text_go_backup) -> showBackupDialog()
            getString(R.string.text_restore) -> showRestoreDialog()
            getString(R.string.text_auto_backup_mode_title) -> if (ConfigManager.cloudEnable) {
                chooseAutoBackupMode()
            }
            getString(R.string.text_webdav_help) -> AndroidUtil.openWeb(this, Constant.NUTSTORE_HELP_URL)
        }
    }

    private fun getItemDisplayPsw(): String {
        return if (ConfigManager.webDAVPsw.isEmpty()) "" else "******"
    }

    private var isDialogShow = false

    private fun setUrl() {
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .title(R.string.text_webdav_url)
                .negativeButton(R.string.text_cancel)
                .positiveButton(R.string.text_affirm)
                .input(prefill = ConfigManager.webDavUrl) { _, input ->
                    val url = input.toString().trim()
                    when {
                        url.isEmpty() -> {
                            updateUrlItem(url)
                        }
                        HttpUrl.parse(url) == null -> ToastUtils.show(R.string.text_url_illegal)
                        else -> {
                            updateUrlItem(url)
                            // 更新网络配置
                            Network.updateDavServiceConfig()
                            initDir()
                        }
                    }
                }
                .onDismiss { isDialogShow = false }
                .show()
    }

    private fun updateUrlItem(url: String) {
        ConfigManager.setWevDavUrl(url)
        val position = 1
        (mAdapter.items[position] as NormalItem).content = url
        mRecyclerView.itemAnimator.changeDuration = 250
        mAdapter.notifyItemChanged(position)
    }

    private fun setAccount() {
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .title(R.string.text_webdav_account)
                .negativeButton(R.string.text_cancel)
                .positiveButton(R.string.text_affirm)
                .input(prefill = ConfigManager.webDavAccount) { _, input ->
                    updateAccountItem(input.toString().trim())
                    // 更新网络配置
                    Network.updateDavServiceConfig()
                    initDir()
                }
                .onDismiss { isDialogShow = false }
                .show()
    }

    private fun updateAccountItem(account: String) {
        ConfigManager.setWevDavAccount(account)
        val position = 2
        (mAdapter.items[position] as NormalItem).content = account
        mRecyclerView.itemAnimator.changeDuration = 250
        mAdapter.notifyItemChanged(position)
    }

    private var isSaving = false

    private fun setPsw() {
        if (isSaving) {
            ToastUtils.show(R.string.text_saving_psw)
            return
        }
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .title(R.string.text_webdav_password)
                .negativeButton(R.string.text_cancel)
                .positiveButton(R.string.text_affirm)
                .input(prefill = ConfigManager.webDAVPsw) { _, input ->
                    savePsw(input.toString())
                }
                .onDismiss { isDialogShow = false }
                .show()
    }

    private fun savePsw(input: String) {
        isSaving = true

        updatePswItem(input)

        // 更新网络配置
        Network.updateDavServiceConfig()
        initDir()
        mViewModel.savePsw(input).observe(this, Observer {
            isSaving = false
            when (it) {
                is ErrorResource<Boolean> -> ToastUtils.show(it.errorMessage)
            }
        })
    }

    private fun updatePswItem(input: String) {
        ConfigManager.webDAVPsw = input
        val position = 3
        (mAdapter.items[position] as NormalItem).content = getItemDisplayPsw()
        mRecyclerView.itemAnimator.changeDuration = 250
        mAdapter.notifyItemChanged(position)
    }

    private fun updateBackupEnable(enable: Boolean) {
        ConfigManager.setCloudEnable(enable)
        updateCloudBackupItem()
    }

    private fun initDir() {
        if (ConfigManager.webDavUrl.isEmpty() || ConfigManager.webDavAccount.isEmpty() || ConfigManager.webDAVPsw.isEmpty()) {
            updateBackupEnable(false)
            return
        }
        mViewModel.getListLiveData().observe(this, Observer {
            when (it) {
                is ApiErrorResponse<ResponseBody> -> {
                    if (it.code == 404) {
                        createDir()
                    } else {
                        updateBackupEnable(false)
                        ToastUtils.show(it.errorMessage)
                    }
                }
                else -> {
                    updateBackupEnable(true)
                }
            }
        })
    }

    private fun createDir() {
        mViewModel.createDirLiveData().observe(this, Observer {
            when (it) {
                is ApiErrorResponse<ResponseBody> -> {
                    updateBackupEnable(false)
                    ToastUtils.show(it.errorMessage)
                }
                else -> {
                    updateBackupEnable(true)
                }
            }
        })
    }

    private fun showBackupDialog() {
        if (ConfigManager.webDavUrl.isEmpty() || ConfigManager.webDavAccount.isEmpty() || ConfigManager.webDAVPsw.isEmpty()) {
            ToastUtils.show(R.string.text_config_webdav)
            return
        }
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .title(R.string.text_go_backup)
                .message(text = getString(R.string.text_backup_save, getString(R.string.text_webdav) + BackupConstant.BACKUP_FILE))
                .negativeButton(R.string.text_cancel)
                .positiveButton(R.string.text_affirm) { CloudBackupService.startBackup(this, true) }
                .onDismiss { isDialogShow = false }
                .show()
    }

    private fun showRestoreDialog() {
        if (ConfigManager.webDavUrl.isEmpty() || ConfigManager.webDavAccount.isEmpty() || ConfigManager.webDAVPsw.isEmpty()) {
            ToastUtils.show(R.string.text_config_webdav)
            return
        }
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .title(R.string.text_restore)
                .message(text = getString(R.string.text_restore_content, getString(R.string.text_webdav) + BackupConstant.BACKUP_FILE))
                .negativeButton(R.string.text_cancel)
                .positiveButton(R.string.text_affirm) { restore() }
                .onDismiss { isDialogShow = false }
                .show()
    }

    private fun restore() {
        mViewModel.restore().observe(this, Observer {
            when (it) {
                is ApiSuccessResponse<ResponseBody> -> {
                    restoreToDB(it.body)
                }
                is ApiErrorResponse<ResponseBody> -> {
                    if (it.code == 404) {
                        ToastUtils.show(R.string.text_backup_file_not_exist)
                    } else {
                        ToastUtils.show(it.errorMessage)
                    }
                }
            }
        })
    }

    private fun restoreToDB(body: ResponseBody) {
        mViewModel.restoreToDB(body).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    if (it.body) {
                        ToastUtils.show(R.string.toast_restore_success)
                        backHome()
                    } else {
                        ToastUtils.show(R.string.toast_restore_fail)
                    }
                }
                is EmptyResource -> {
                    restartApp()
                }
                is ErrorResource<Boolean> -> ToastUtils.show(getString(R.string.toast_restore_fail) + "\n" + it.errorMessage)
            }
        })
    }

    private fun backHome() {
//        Floo.stack(this)
//                .popCount(2)
//                .result("refresh")
//                .start()
        ProcessPhoenix.triggerRebirth(this, Intent(this, HomeActivity::class.java))
    }

    private fun restartApp() {
        val dialog = MaterialDialog(this)
                .title(text = "\uD83D\uDC7A" + getString(R.string.text_error))
                .message(R.string.text_restore_fail_rollback)
                .positiveButton(R.string.text_affirm) { ProcessPhoenix.triggerRebirth(this, Intent(this, HomeActivity::class.java)) }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun chooseAutoBackupMode() {
        val initialSelection = when (ConfigManager.cloudBackupMode) {
            ConfigManager.MODE_NO -> 0
            ConfigManager.MODE_LAUNCHER_APP -> 1
            ConfigManager.MODE_EXIT_APP -> 2
            else -> 0
        }
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .title(R.string.text_auto_backup_mode)
                .listItemsSingleChoice(res = R.array.text_cloud_auto_backup_mode, initialSelection = initialSelection) { _, index, text ->
                    when (index) {
                        0 -> ConfigManager.setCloudBackupMode(ConfigManager.MODE_NO)
                        1 -> ConfigManager.setCloudBackupMode(ConfigManager.MODE_LAUNCHER_APP)
                        2 -> ConfigManager.setCloudBackupMode(ConfigManager.MODE_EXIT_APP)
                    }
                    updateCloudBackupItem()
                }
                .positiveButton(R.string.text_affirm)
                .onDismiss { isDialogShow = false }
                .show()
    }

    /**
     * 更新备份模式 item 内容
     */
    private fun updateCloudBackupItem() {
        val position = 6
        (mAdapter.items[position] as NormalItem).content = getBackupModeStr()
        (mAdapter.items[position] as NormalItem).clickEnable = ConfigManager.cloudEnable
        mRecyclerView.itemAnimator.changeDuration = 0
        mAdapter.notifyItemChanged(position)
    }

    private fun getBackupModeStr(): String {
        return when (ConfigManager.cloudBackupMode) {
            ConfigManager.MODE_NO -> resources.getStringArray(R.array.text_cloud_auto_backup_mode)[0]
            ConfigManager.MODE_LAUNCHER_APP -> resources.getStringArray(R.array.text_cloud_auto_backup_mode)[1]
            ConfigManager.MODE_EXIT_APP -> resources.getStringArray(R.array.text_cloud_auto_backup_mode)[2]
            else -> resources.getStringArray(R.array.text_cloud_auto_backup_mode)[0]
        }
    }

    override fun onDestroy() {
        if (isSaving) {
            ToastUtils.show(R.string.text_saving_psw)
        } else {
            super.onDestroy()
        }
    }

}
