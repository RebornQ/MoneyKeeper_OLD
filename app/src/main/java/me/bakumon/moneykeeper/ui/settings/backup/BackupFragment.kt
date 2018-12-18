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
package me.bakumon.moneykeeper.ui.settings.backup

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.jakewharton.processphoenix.ProcessPhoenix
import me.bakumon.moneykeeper.CloudBackupService
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.Injection
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.api.ApiErrorResponse
import me.bakumon.moneykeeper.api.ApiSuccessResponse
import me.bakumon.moneykeeper.api.Network
import me.bakumon.moneykeeper.base.EmptyResource
import me.bakumon.moneykeeper.base.ErrorResource
import me.bakumon.moneykeeper.base.SuccessResource
import me.bakumon.moneykeeper.ui.common.BaseViewModel
import me.bakumon.moneykeeper.ui.home.HomeActivity
import me.bakumon.moneykeeper.utill.AndroidUtil
import me.bakumon.moneykeeper.utill.BackupUtil
import me.bakumon.moneykeeper.utill.PermissionUtil
import me.bakumon.moneykeeper.utill.ToastUtils
import okhttp3.ResponseBody

class BackupFragment : PreferenceFragmentCompat() {
    private lateinit var mViewModel: BackupViewModel
    /**
     * 实例化 BaseViewModel 子类
     */
    inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_backup, rootKey)
        mViewModel = getViewModel()
        PermissionUtil.requestPermission(context!!) { setupLocalBackupEnable(it) }
        setupPreferences()
        setupPreferencesCloud()
    }

    private fun setupLocalBackupEnable(isEnabled: Boolean) {
        findPreference("localBackupNow").isEnabled = isEnabled
        findPreference("localRestore").isEnabled = isEnabled
        findPreference("localAutoBackupShow").isEnabled = isEnabled
    }

    private fun setupPreferences() {
        val backupFolder = BackupUtil.backupFolder
        val localBackupNowPreference = findPreference("localBackupNow")
        val localRestorePreference = findPreference("localRestore")

        localBackupNowPreference.summary = getString(R.string.text_backup_save, backupFolder)
        localRestorePreference.summary = getString(R.string.text_restore_content, backupFolder)

        localBackupNowPreference.setOnPreferenceClickListener {
            backupDB()
            true
        }

        localRestorePreference.setOnPreferenceClickListener {
            restore()
            true
        }

        findPreference("cloudBackupNow").summary =
                getString(R.string.text_backup_save, getString(R.string.text_webdav) + BackupConstant.BACKUP_FILE)
        findPreference("cloudRestore").summary =
                getString(R.string.text_restore_content, getString(R.string.text_webdav) + BackupConstant.BACKUP_FILE)

        findPreference("webdavHelp").setOnPreferenceClickListener {
            AndroidUtil.openWeb(context!!, it.summary.toString())
            true
        }

    }

    private var isDialogShow = false
    private fun backupDB() {
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(context!!)
            .title(R.string.text_go_backup)
            .message(text = getString(R.string.text_backup_save, BackupUtil.userBackupPath))
            .negativeButton(R.string.text_cancel)
            .positiveButton(R.string.text_affirm) {
                mViewModel.backupDB().observe(this, Observer { resource ->
                    when (resource) {
                        is SuccessResource<Boolean> -> ToastUtils.show(R.string.toast_backup_success)
                        is ErrorResource<Boolean> -> ToastUtils.show(R.string.toast_backup_fail)
                    }
                })
            }
            .onDismiss { isDialogShow = false }
            .show()
    }

    private fun restore() {
        mViewModel.backupFiles().observe(this, Observer { resource ->
            when (resource) {
                is SuccessResource<List<BackupBean>> -> {
                    showBackupListDialog(resource)
                }
                is ErrorResource<List<BackupBean>> -> ToastUtils.show(R.string.toast_backup_list_fail)
            }
        })
    }

    private fun showBackupListDialog(resource: SuccessResource<List<BackupBean>>) {
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        val dialog = BackupFliesDialog(context!!, resource.body) { restoreDB(it.path) }
        dialog.getDialog().setOnDismissListener { isDialogShow = false }
        dialog.show()
    }

    private fun restoreDB(restoreFile: String) {
        mViewModel.restoreToDB(restoreFile).observe(this, Observer {
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
        ProcessPhoenix.triggerRebirth(context!!, Intent(context, HomeActivity::class.java))
    }

    private fun restartApp() {
        val dialog = MaterialDialog(context!!)
            .title(text = "\uD83D\uDC7A" + getString(R.string.text_error))
            .message(R.string.text_restore_fail_rollback)
            .positiveButton(R.string.text_affirm) { backHome() }
        dialog.setCancelable(false)
        dialog.show()
    }

    ////////////////////
    //////////云备份
    ////////////////////

    private fun setupPreferencesCloud() {
        // WebDAV 密码
        val pswPreference = findPreference("webdavPsw") as EditTextPreference
        pswPreference.setSummaryProvider {
            if (TextUtils.isEmpty(pswPreference.text)) {
                getString(R.string.text_no_setting)
            } else {
                "******"
            }
        }

        // WebDAV地址
        findPreference("webdavUrl").setOnPreferenceChangeListener { _, newValue ->
            // 更新网络配置
//            Network.updateDavServiceConfig()
            initDir(newValue as String, DefaultSPHelper.webdavUserName, DefaultSPHelper.webdavPsw)
            true
        }

        // WebDAV账户
        findPreference("webdavUserName").setOnPreferenceChangeListener { _, newValue ->
            initDir(DefaultSPHelper.webdavUrl, newValue as String, DefaultSPHelper.webdavPsw)
            true
        }

        pswPreference.setOnPreferenceChangeListener { _, newValue ->
            initDir(DefaultSPHelper.webdavUrl, DefaultSPHelper.webdavUserName, newValue as String)
            true
        }

        findPreference("cloudBackupNow").setOnPreferenceClickListener {
            showCloudBackupDialog()
            true
        }

        findPreference("cloudRestore").setOnPreferenceClickListener {
            showCloudRestoreDialog()
            true
        }

        initDir(DefaultSPHelper.webdavUrl, DefaultSPHelper.webdavUserName, DefaultSPHelper.webdavPsw)
    }

    private fun setCloudEnable(isEnabled: Boolean) {
        DefaultSPHelper.isCloudBackupEnable = isEnabled
        findPreference("cloudBackupNow").isEnabled = isEnabled
        findPreference("cloudRestore").isEnabled = isEnabled
        findPreference("cloudAutoBackupMode").isEnabled = isEnabled
    }

    private fun initDir(url: String?, userName: String?, pwd: String?) {
        val isEnabled = url.isNullOrBlank() || userName.isNullOrBlank() || pwd.isNullOrBlank()
        if (isEnabled) {
            setCloudEnable(false)
            return
        }
        // 更新网络配置
        Network.updateDavServiceConfig(url!!, userName!!, pwd!!)

        mViewModel.getListLiveData().observe(this, Observer {
            when (it) {
                is ApiErrorResponse<ResponseBody> -> {
                    if (it.code == 404) {
                        createDir()
                    } else {
                        setCloudEnable(false)
                        ToastUtils.show(it.errorMessage)
                    }
                }
                else -> {
                    setCloudEnable(true)
                }
            }
        })
    }

    private fun createDir() {
        mViewModel.createDirLiveData().observe(this, Observer {
            when (it) {
                is ApiErrorResponse<ResponseBody> -> {
                    setCloudEnable(false)
                    ToastUtils.show(it.errorMessage)
                }
                else -> {
                    setCloudEnable(true)
                }
            }
        })
    }

    private fun showCloudBackupDialog(){
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(context!!)
            .title(R.string.text_go_backup)
            .message(text = getString(R.string.text_backup_save, getString(R.string.text_webdav) + BackupConstant.BACKUP_FILE))
            .negativeButton(R.string.text_cancel)
            .positiveButton(R.string.text_affirm) { CloudBackupService.startBackup(context!!, true) }
            .onDismiss { isDialogShow = false }
            .show()
    }

    private fun showCloudRestoreDialog() {
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(context!!)
            .title(R.string.text_restore)
            .message(text = getString(R.string.text_restore_content, getString(R.string.text_webdav) + BackupConstant.BACKUP_FILE))
            .negativeButton(R.string.text_cancel)
            .positiveButton(R.string.text_affirm) { restoreCloud() }
            .onDismiss { isDialogShow = false }
            .show()
    }

    private fun restoreCloud() {
        mViewModel.restore().observe(this, Observer {
            when (it) {
                is ApiSuccessResponse<ResponseBody> -> {
                    restoreToDBCloud(it.body)
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

    private fun restoreToDBCloud(body: ResponseBody) {
        mViewModel.restoreToDBCloud(body).observe(this, Observer {
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

    companion object {
        fun newInstance(): BackupFragment {
            return BackupFragment()
        }
    }

}
