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

package me.bakumon.moneykeeperclone.ui.home

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_home.*
import me.bakumon.moneykeeperclone.CloudBackupService
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.base.ErrorResource
import me.bakumon.moneykeeperclone.base.SuccessResource
import me.bakumon.moneykeeperclone.database.entity.RecordWithType
import me.bakumon.moneykeeperclone.ui.common.BaseActivity
import me.bakumon.moneykeeperclone.ui.common.Empty
import me.bakumon.moneykeeperclone.ui.common.EmptyViewBinder
import me.bakumon.moneykeeperclone.utill.ShortcutUtil
import me.bakumon.moneykeeperclone.utill.ToastUtils
import me.bakumon.moneykeeperclone.widget.WidgetProvider
import me.drakeet.floo.Floo
import me.drakeet.floo.StackCallback
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * HomeActivity
 *
 * @author bakumon https://bakumon.me
 * @date 2018/4/9
 */
class HomeActivity : BaseActivity(), StackCallback, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private lateinit var mViewModel: HomeViewModel
    private lateinit var mAdapter: MultiTypeAdapter
    private var isUserFirst: Boolean = false

    override val layoutId: Int
        get() = R.layout.activity_home

    override fun isChangeStatusColor(): Boolean {
        return false
    }

    override fun onInitView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btnAdd.setOnClickListener { Floo.navigation(this, Router.Url.URL_ADD_RECORD).start() }
        btnAdd.setOnLongClickListener {
            Floo.navigation(this, Router.Url.URL_ADD_RECORD)
                    .putExtra(Router.ExtraKey.KEY_IS_SUCCESSIVE, true)
                    .start()
            false
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        Fabric.with(this, Crashlytics(), Answers())
        // 快速记账
        if (ConfigManager.isFast) {
            Floo.navigation(this, Router.Url.URL_ADD_RECORD).start()
        }
        // 设置 MultiTypeAdapter
        mAdapter = MultiTypeAdapter()
        mAdapter.register(RecordWithType::class, RecordViewBinder { deleteRecord(it) })
        mAdapter.register(String::class, FooterViewBinder())
        mAdapter.register(Empty::class, EmptyViewBinder())
        rvRecords.adapter = mAdapter

        checkPermissionForBackup()

        mViewModel = getViewModel()
        initData()
        getOldPsw()
    }

    private fun initData() {
        initRecordTypes()
        getCurrentMonthRecords()
    }

    override fun onResume() {
        super.onResume()
        // 设置了预算，返回首页需要更新
        getCurrentMoneySumMonty()
        // 更新 widget
        WidgetProvider.updateWidget(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_statistics -> Floo.navigation(this, Router.Url.URL_STATISTICS).start()
            android.R.id.home -> Floo.navigation(this, Router.Url.URL_SETTING).start()
        }
        return true
    }

    private fun getOldPsw() {
        mViewModel.getPsw().observe(this, Observer {
            when (it) {
                is SuccessResource<String> -> {
                    ConfigManager.webDAVPsw = it.body
                    // 自动云备份
                    if (ConfigManager.cloudEnable && ConfigManager.cloudBackupMode == ConfigManager.MODE_LAUNCHER_APP) {
                        CloudBackupService.startBackup(this)
                    }
                }
                is ErrorResource<String> -> ToastUtils.show(it.errorMessage)
            }
        })
    }

    private fun deleteRecord(record: RecordWithType) {
        mViewModel.deleteRecord(record).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> {
                    // 更新 widget
                    WidgetProvider.updateWidget(this)
                }
                is ErrorResource<Boolean> -> {
                    ToastUtils.show(R.string.toast_record_delete_fail)
                }
            }
        })
    }

    private fun initRecordTypes() {
        mViewModel.initRecordTypes().observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> ShortcutUtil.addRecordShortcut(this)
                is ErrorResource<Boolean> -> ToastUtils.show(R.string.toast_init_types_fail)
            }
        })
    }

    private fun getCurrentMoneySumMonty() {
        mViewModel.currentMonthSumMoney.observe(this, Observer { sumMoneyBeans ->
            mViewModel.getAssetsMoney().observe(this, Observer { assetsMontyBean ->
                headPageView.setSumMoneyBeanList(sumMoneyBeans, assetsMontyBean)
            })
        })
    }

    private fun getCurrentMonthRecords() {
        mViewModel.currentMonthRecordWithTypes.observe(this, Observer {
            if (it != null) {
                setItems(it)
            }
        })
    }

    private fun setItems(recordWithTypes: List<RecordWithType>) {
        val items = Items()
        if (recordWithTypes.isEmpty()) {
            items.add(Empty(getString(R.string.text_current_month_empty_tip), Gravity.CENTER))
        } else {
            items.addAll(recordWithTypes)
            if (recordWithTypes.size > MAX_ITEM_TIP) {
                items.add(getString(R.string.text_home_footer_tip))
            }
        }
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 自动云备份
        if (ConfigManager.cloudEnable && ConfigManager.cloudBackupMode == ConfigManager.MODE_EXIT_APP) {
            CloudBackupService.startBackup(this)
        }
    }

    override fun indexKeyForStackTarget(): String? {
        return Router.IndexKey.INDEX_KEY_HOME
    }

    override fun onReceivedResult(result: Any?) {
        initData()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun checkPermissionForBackup() {
        if (!ConfigManager.isAutoBackup) {
            return
        }
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return
        }
        // 当自动备份打开，并且没有存储权限，提示用户需要申请权限
        EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, REQUEST_CODE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .setRationale(R.string.text_storage_content)
                        .setPositiveButtonText(R.string.text_affirm)
                        .setNegativeButtonText(R.string.text_cancel)
                        .build())
    }

    private fun updateConfig(isAutoBackup: Boolean) {
        if (isAutoBackup) {
            ConfigManager.setIsAutoBackup(true)
        } else {
            if (ConfigManager.setIsAutoBackup(false)) {
                ToastUtils.show(R.string.toast_open_auto_backup)
            }
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        isUserFirst = true
    }

    override fun onRationaleDenied(requestCode: Int) {
        isUserFirst = true
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        updateConfig(true)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (!isUserFirst) {
                AppSettingsDialog.Builder(this)
                        .setRationale(R.string.text_storage_permission_tip)
                        .setTitle(R.string.text_storage)
                        .setPositiveButton(R.string.text_affirm)
                        .setNegativeButton(R.string.text_cancel)
                        .build()
                        .show()
            }
        } else {
            updateConfig(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                updateConfig(true)
            } else {
                updateConfig(false)
            }
        }
    }

    companion object {

        private const val MAX_ITEM_TIP = 5

        ///////////////////////////////
        //// 自动备份打开时，检查是否有权限
        ///////////////////////////////

        private const val REQUEST_CODE_STORAGE = 11
    }

}
