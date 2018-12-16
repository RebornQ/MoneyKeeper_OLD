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

package me.bakumon.moneykeeperclone.ui.setting.other

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.files.folderChooser
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.base.ErrorResource
import me.bakumon.moneykeeperclone.base.SuccessResource
import me.bakumon.moneykeeperclone.ui.common.AbsListActivity
import me.bakumon.moneykeeperclone.ui.setting.NormalItem
import me.bakumon.moneykeeperclone.ui.setting.NormalItemViewBinder
import me.bakumon.moneykeeperclone.utill.BackupUtil
import me.bakumon.moneykeeperclone.utill.ToastUtils
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.io.File

/**
 * 设置
 *
 * @author Bakumon https://bakumon.me
 */
class OtherSettingActivity : AbsListActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var mViewModel: OtherSettingViewModel

    override fun onSetupTitle(tvTitle: TextView) {
        tvTitle.text = getString(R.string.text_other_setting)
    }

    override fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(NormalItem::class, NormalItemViewBinder { normalItem: NormalItem, view: View -> onNormalItemClick(normalItem, view) })
    }

    override fun onItemsCreated(items: Items) {
        items.add(NormalItem(getString(R.string.text_local_backup_path), getBackupPathContent()))
    }

    override fun onParentInitDone(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        mViewModel = getViewModel()
    }

    private fun onNormalItemClick(item: NormalItem, view: View) {
        when (item.title) {
            getString(R.string.text_local_backup_path) -> chooseFolder()
        }
    }

    private fun getBackupPathContent(): String {
        return getString(R.string.text_local_backup_path_tip) + "\n" + BackupUtil.backupFolder
    }

    private fun chooseFolder() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showChooseFolderDialog()
        }
        EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, 11, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .setRationale(R.string.text_storage_content)
                        .setPositiveButtonText(R.string.text_affirm)
                        .setNegativeButtonText(R.string.text_cancel)
                        .build())
    }

    private var isDialogShow = false

    private fun showChooseFolderDialog() {
        if (isDialogShow) {
            return
        }
        isDialogShow = true
        MaterialDialog(this)
                .negativeButton(R.string.text_cancel)
                .positiveButton(R.string.text_choose)
                .folderChooser(emptyTextRes = R.string.text_folder_empty) { _, folder ->
                    onChooseFolder(folder)
                }
                .onDismiss { isDialogShow = false }
                .show()
    }

    private fun onChooseFolder(folder: File) {
        mViewModel.move(folder.absolutePath).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    ToastUtils.show(R.string.toast_move_backup_files_success)
                    ConfigManager.setBackupFolder(folder.absolutePath)
                    updateBackupPathItem()
                }
                is ErrorResource<Boolean> -> ToastUtils.show(R.string.toast_move_backup_files_fail)
            }
        })
    }

    private fun updateBackupPathItem() {
        val position = 0
        (mAdapter.items[position] as NormalItem).content = getBackupPathContent()
        mRecyclerView.itemAnimator.changeDuration = 250
        mAdapter.notifyItemChanged(position)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            11 -> showChooseFolderDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
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
}
