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

package me.bakumon.moneykeeper.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_home.*
import me.bakumon.moneykeeper.CloudBackupService
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.base.ErrorResource
import me.bakumon.moneykeeper.base.SuccessResource
import me.bakumon.moneykeeper.database.entity.RecordForList
import me.bakumon.moneykeeper.ui.add.AddRecordActivity
import me.bakumon.moneykeeper.ui.common.BaseActivity
import me.bakumon.moneykeeper.ui.common.Empty
import me.bakumon.moneykeeper.ui.common.EmptyViewBinder
import me.bakumon.moneykeeper.ui.settings.SettingsActivity
import me.bakumon.moneykeeper.ui.statistics.StatisticsActivity
import me.bakumon.moneykeeper.utill.PermissionUtil
import me.bakumon.moneykeeper.utill.ShortcutUtil
import me.bakumon.moneykeeper.utill.ToastUtils
import me.bakumon.moneykeeper.widget.WidgetProvider
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register

/**
 * HomeActivity
 *
 * @author bakumon https://bakumon.me
 * @date 2018/4/9
 */
class HomeActivity : BaseActivity() {
    private lateinit var mViewModel: HomeViewModel
    private val mAdapter: MultiTypeAdapter = MultiTypeAdapter()

    override val layoutId: Int
        get() = R.layout.activity_home

    override fun isChangeStatusColor(): Boolean {
        // light（日间）模式下状态状态蓝文字颜色不改变，保持白色
        return false
    }

    override fun onInitView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btnAdd.setOnClickListener {
            AddRecordActivity.open(this)
        }
        btnAdd.setOnLongClickListener {
            AddRecordActivity.open(this, isSuccessive = true)
            true
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        Fabric.with(this, Crashlytics(), Answers())
        // 快速记账
        if (DefaultSPHelper.isFast) {
            AddRecordActivity.open(this)
        }
        PermissionUtil.requestStoragePermissionSimple(this)
        // 设置 MultiTypeAdapter
        mAdapter.register(RecordForList::class, RecordsViewBinder { deleteRecord(it) })
        mAdapter.register(String::class, FooterViewBinder())
        mAdapter.register(Empty::class, EmptyViewBinder())
        rvRecords.adapter = mAdapter

        initData()
        cloudBackup()
    }

    private fun initData() {
        mViewModel = getViewModel()
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

    override fun getMenuRes(): Int {
        return R.menu.menu_home
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_statistics -> StatisticsActivity.open(this)
            android.R.id.home -> SettingsActivity.open(this)
        }
        return true
    }

    private fun deleteRecord(record: RecordForList) {
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
        mViewModel.recentRecords.observe(this, Observer {
            if (it != null) {
                setItems(it)
            }
        })
    }

    private fun setItems(records: List<RecordForList>) {
        val items = Items()
        if (records.isEmpty()) {
            items.add(Empty(getString(R.string.text_current_month_empty_tip), Gravity.CENTER))
        } else {
            items.addAll(records)
            if (records.size > MAX_ITEM_TIP) {
                items.add(getString(R.string.text_home_footer_tip))
            }
        }
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }

    private fun cloudBackup() {
        // 自动云备份
        if (DefaultSPHelper.isCloudBackupEnable && DefaultSPHelper.isCloudBackupWhenOpenApp) {
            CloudBackupService.startBackup(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 自动云备份
        if (DefaultSPHelper.isCloudBackupEnable && DefaultSPHelper.isCloudBackupWhenQuitApp) {
            CloudBackupService.startBackup(this)
        }
    }

    companion object {
        private const val MAX_ITEM_TIP = 5
        fun open(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

}
