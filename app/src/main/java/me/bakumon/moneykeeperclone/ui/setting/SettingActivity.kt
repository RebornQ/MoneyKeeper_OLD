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

package me.bakumon.moneykeeperclone.ui.setting

import android.Manifest
import android.app.KeyguardManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.jakewharton.processphoenix.ProcessPhoenix
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import me.bakumon.moneykeeperclone.*
import me.bakumon.moneykeeperclone.base.EmptyResource
import me.bakumon.moneykeeperclone.base.ErrorResource
import me.bakumon.moneykeeperclone.base.SuccessResource
import me.bakumon.moneykeeperclone.ui.common.AbsListActivity
import me.bakumon.moneykeeperclone.ui.home.HomeActivity
import me.bakumon.moneykeeperclone.utill.AndroidUtil
import me.bakumon.moneykeeperclone.utill.BackupUtil
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.bakumon.moneykeeperclone.utill.ToastUtils
import me.bakumon.moneykeeperclone.widget.WidgetProvider
import me.drakeet.floo.Floo
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * 设置
 *
 * @author Bakumon https://bakumon.me
 */
class SettingActivity : AbsListActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var mViewModel: SettingViewModel

    override fun onSetupTitle(tvTitle: TextView) {
        tvTitle.text = getString(R.string.text_setting)
    }

    override fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(Category::class, CategoryViewBinder())
        adapter.register(NormalItem::class, NormalItemViewBinder { normalItem: NormalItem, view: View -> onNormalItemClick(normalItem, view) })
        adapter.register(CheckItem::class, CheckItemViewBinder { item, isCheck -> onCheckItemCheckChange(item, isCheck) })
        adapter.register(ImgItem::class, ImgItemViewBinder { onImgItemClick(it) })
    }

    override fun onItemsCreated(items: Items) {
        items.add(Category(getString(R.string.text_money)))
        items.add(NormalItem(getString(R.string.text_monty_budget), getBudgetStr()))
        items.add(NormalItem(getString(R.string.text_assets_manager), getString(R.string.text_assets_manager_content)))
        items.add(NormalItem(getString(R.string.text_title_symbol), getString(R.string.text_content_symbol)))
        items.add(NormalItem(getString(R.string.text_setting_type_manage), getString(R.string.text_setting_type_manage_content)))
        items.add(CheckItem(getString(R.string.text_fast_accounting), getString(R.string.text_fast_tip), ConfigManager.isFast))
        items.add(NormalItem(getString(R.string.text_successive_record), getString(R.string.text_successive_record_tip)))

        items.add(Category(getString(R.string.text_backup)))
        val backupFolder = BackupUtil.backupFolder
        items.add(NormalItem(getString(R.string.text_go_backup), getString(R.string.text_backup_save, backupFolder)))
        items.add(NormalItem(getString(R.string.text_restore), getString(R.string.text_restore_content, backupFolder)))
        items.add(CheckItem(getString(R.string.text_auto_backup), getString(R.string.text_auto_backup_content), ConfigManager.isAutoBackup))

        items.add(Category(getString(R.string.text_cloud_backup)))
        items.add(ImgItem(getString(R.string.text_cloud_backup_title), getString(R.string.text_cloud_backup_content), R.drawable.ic_cloud))

        items.add(Category(getString(R.string.text_display)))
        items.add(NormalItem(getString(R.string.text_theme), getThemeStr()))
        items.add(Category(getString(R.string.text_luck)))
        items.add(NormalItem(getString(R.string.text_luck_screen), getLockScreenState()))
        items.add(CheckItem(getString(R.string.text_luck_screen_add), getString(R.string.text_luck_screen_add_tip), ConfigManager.lockAdd))

        items.add(Category(getString(R.string.text_about_and_more)))
        items.add(NormalItem(getString(R.string.text_about), getString(R.string.text_about_content)))
        items.add(NormalItem(getString(R.string.text_feedback), getString(R.string.text_feedback_help)))
        items.add(NormalItem(getString(R.string.text_other_setting), ""))
        items.add(NormalItem("", getString(R.string.text_privacy_policy)))
    }

    override fun onResume() {
        super.onResume()
        updateBackupItem()
        updateRestoreItem()
    }

    private fun updateBackupItem() {
        val position = 8
        (mAdapter.items[position] as NormalItem).content = getString(R.string.text_backup_save, BackupUtil.backupFolder)
        mRecyclerView.itemAnimator.changeDuration = 250
        mAdapter.notifyItemChanged(position)
    }

    private fun updateRestoreItem() {
        val position = 9
        (mAdapter.items[position] as NormalItem).content = getString(R.string.text_restore_content, BackupUtil.backupFolder)
        mRecyclerView.itemAnimator.changeDuration = 250
        mAdapter.notifyItemChanged(position)
    }

    override fun onParentInitDone(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        mViewModel = getViewModel()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun onNormalItemClick(item: NormalItem, view: View) {
        when (item.title) {
            getString(R.string.text_monty_budget) -> setBudget()
            getString(R.string.text_assets_manager) -> Floo.navigation(this, Router.Url.URL_ASSETS).start()
            getString(R.string.text_title_symbol) -> setSymbol()
            getString(R.string.text_setting_type_manage) -> Floo.navigation(this, Router.Url.URL_TYPE_MANAGE).start()
            getString(R.string.text_go_backup) -> showBackupDialog()
            getString(R.string.text_restore) -> showRestoreDialog()
            getString(R.string.text_theme) -> showChooseThemeDialog()
            getString(R.string.text_other_setting) -> Floo.navigation(this, Router.Url.URL_OTHER_SETTING).start()
            getString(R.string.text_about) -> Floo.navigation(this, Router.Url.URL_ABOUT).start()
            getString(R.string.text_feedback) -> AndroidUtil.openWeb(this, Constant.getUrlTucao())
            getString(R.string.text_luck_screen) -> chooseLockScreen(view)
            "" -> AndroidUtil.openWeb(this, Constant.URL_PRIVACY)
        }
    }

    private fun onCheckItemCheckChange(item: CheckItem, isCheck: Boolean) {
        when (item.title) {
            getString(R.string.text_fast_accounting) -> ConfigManager.setIsFast(isCheck)
            getString(R.string.text_auto_backup) -> switchAutoBackup(isCheck)
            getString(R.string.text_luck_screen_add) -> ConfigManager.setLockAdd(isCheck)
        }
    }

    private fun onImgItemClick(item: ImgItem) {
        when (item.title) {
            getString(R.string.text_cloud_backup_title) -> Floo.navigation(this, Router.Url.URL_BACKUP).start()
        }
    }



    private fun getLockScreenState(): String {
        return when (ConfigManager.lockScreenState) {
            0 -> getString(R.string.text_luck_screen_off)
            1 -> {
                val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
                if (keyguardManager != null && keyguardManager.isKeyguardSecure) {
                    getString(R.string.text_luck_screen_system)
                } else {
                    ConfigManager.setLockScreenState(0)
                    ToastUtils.show(R.string.text_unlock_close_system)
                    getString(R.string.text_luck_screen_off)
                }
            }
            2 -> {
                val fingerprintIdentify = FingerprintIdentify(App.instance.applicationContext)
                if (fingerprintIdentify.isFingerprintEnable) {
                    getString(R.string.text_luck_screen_custom)
                } else {
                    ConfigManager.setLockScreenState(0)
                    ToastUtils.show(R.string.text_unlock_close_customer)
                    getString(R.string.text_luck_screen_off)
                }
            }
            else -> getString(R.string.text_luck_screen_off)
        }
    }

    private fun chooseLockScreen(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.add(getString(R.string.text_luck_screen_off))
        popupMenu.menu.add(getString(R.string.text_luck_screen_system))
        popupMenu.menu.add(getString(R.string.text_luck_screen_custom))
        popupMenu.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.text_luck_screen_off) -> {
                    ConfigManager.setLockScreenState(0)
                    updateLockItem(it.title)
                }
                getString(R.string.text_luck_screen_system) -> {
                    val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
                    if (keyguardManager != null && keyguardManager.isKeyguardSecure) {
                        ConfigManager.setLockScreenState(1)
                        updateLockItem(it.title)
                    } else {
                        ToastUtils.show(R.string.text_unlock_tip)
                    }
                }
                getString(R.string.text_luck_screen_custom) -> {
                    val fingerprintIdentify = FingerprintIdentify(App.instance.applicationContext)
                    if (fingerprintIdentify.isFingerprintEnable) {
                        ConfigManager.setLockScreenState(2)
                        updateLockItem(it.title)
                    } else {
                        ToastUtils.show(R.string.text_unlock_finger_print)
                    }
                }
            }
            false
        }
        popupMenu.setOnDismissListener {
            isDialogShow = false
        }
        popupMenu.show()
    }

    private fun updateLockItem(lockTitle: CharSequence) {
        val position = 16
        (mAdapter.items[position] as NormalItem).content = lockTitle.toString()
        mRecyclerView.itemAnimator.changeDuration = 250
        mAdapter.notifyItemChanged(position)
    }

    private fun getBudgetStr(): String {
        return if (ConfigManager.budget == 0)
            getString(R.string.text_no_setting)
        else
            ConfigManager.symbol + BigDecimalUtil.formatNum(ConfigManager.budget.toString())
    }

    private var isDialogShow = false

    private fun setBudget() {
        val oldBudget = if (ConfigManager.budget == 0)
            null
        else
            ConfigManager.budget.toString().replace(",", "")
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .title(R.string.text_set_budget)
                .negativeButton(R.string.text_cancel)
                .positiveButton(R.string.text_affirm)
                .input(prefill = oldBudget, maxLength = 8, hint = getString(R.string.hint_enter_budget), inputType = InputType.TYPE_CLASS_NUMBER) { _, input ->
                    if (!input.isEmpty()) {
                        if (input.length > 8) {
                            ConfigManager.setBudget(Integer.parseInt(input.substring(0, 8)))
                        } else {
                            ConfigManager.setBudget(Integer.parseInt(input.toString()))
                        }
                    } else {
                        ConfigManager.setBudget(0)
                    }
                    updateBudgetItem()
                    // 更新 widget
                    WidgetProvider.updateWidget(this)
                }
                .onDismiss { isDialogShow = false }
                .show()
    }

    private fun updateBudgetItem() {
        val position = 1
        (mAdapter.items[position] as NormalItem).content = getBudgetStr()
        mRecyclerView.itemAnimator.changeDuration = 250
        mAdapter.notifyItemChanged(position)
    }

    private fun setSymbol() {
        var index = 0
        val symbolList = resources.getStringArray(R.array.simple_symbol)
        val savedSymbol = ConfigManager.symbol

        symbolList.forEachIndexed { i, symbol ->
            if (TextUtils.equals(symbol, savedSymbol)) {
                index = i
            }
        }
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .title(R.string.text_set_symbol)
                .positiveButton(R.string.text_affirm)
                .listItemsSingleChoice(R.array.symbol, initialSelection = index) { _, which, _ ->
                    val simpleSymbol = resources.getStringArray(R.array.simple_symbol)[which]
                    ConfigManager.setSymbol(simpleSymbol)
                    // 更新预算和资产符号
                    updateBudgetItem()
                    // 更新 widget
                    WidgetProvider.updateWidget(this)
                }
                .onDismiss { isDialogShow = false }
                .show()
    }

    private fun showChooseThemeDialog() {
        val index = if (ConfigManager.isThemeDark) 0 else 1
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .title(R.string.text_theme)
                .positiveButton(R.string.text_affirm)
                .listItemsSingleChoice(initialSelection = index, items = arrayListOf(getString(R.string.text_theme_dark), getString(R.string.text_theme_light))) { _, which, _ ->
                    val theme = which == 0
                    if (theme) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    ConfigManager.setIsThemeDark(theme)
                    finish()
                }
                .onDismiss { isDialogShow = false }
                .show()
    }

    private fun getThemeStr(): String {
        return if (ConfigManager.isThemeDark) getString(R.string.text_theme_dark) else getString(R.string.text_theme_light)
    }

    private fun switchAutoBackup(isCheck: Boolean) {
        if (!isCheck) {
            ConfigManager.setIsAutoBackup(false)
        } else {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ConfigManager.setIsAutoBackup(true)
                return
            }
            EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(this, 11, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .setRationale(R.string.text_storage_content)
                            .setPositiveButtonText(R.string.text_affirm)
                            .setNegativeButtonText(R.string.text_cancel)
                            .build())
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            11 -> {
                ConfigManager.setIsAutoBackup(true)
            }
            12 -> backupDB()
            13 -> restore()
            else -> {
            }
        }
    }

    private fun updateAutoBackupItem(isCheck: Boolean) {
        val position = 10
        (mAdapter.items[position] as CheckItem).isCheck = isCheck
        mRecyclerView.itemAnimator.changeDuration = 250
        mAdapter.notifyItemChanged(position)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (requestCode == 11) {
            // 更新自动备份 item
            updateAutoBackupItem(false)
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setRationale(R.string.text_storage_permission_tip)
                    .setTitle(R.string.text_storage)
                    .setPositiveButton(R.string.text_affirm)
                    .setNegativeButton(R.string.text_cancel)
                    .build()
                    .show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            onInit(null)
        }
    }

    private fun showBackupDialog() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            backupDB()
            return
        }
        EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, 12, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .setRationale(R.string.text_storage_content)
                        .setPositiveButtonText(R.string.text_affirm)
                        .setNegativeButtonText(R.string.text_cancel)
                        .build())
    }

    private fun backupDB() {
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
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

    private fun showRestoreDialog() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            restore()
            return
        }
        EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, 13, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .setRationale(R.string.text_storage_content)
                        .setPositiveButtonText(R.string.text_affirm)
                        .setNegativeButtonText(R.string.text_cancel)
                        .build())
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
        val dialog = BackupFliesDialog(this, resource.body) { restoreDB(it.path) }
        dialog.getDialog().setOnDismissListener { isDialogShow = false }
        dialog.show()
    }

    private fun restoreDB(restoreFile: String) {
        mViewModel.restoreToDB(restoreFile).observe(this, android.arch.lifecycle.Observer {
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
//                .target(Router.IndexKey.INDEX_KEY_HOME)
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
}
