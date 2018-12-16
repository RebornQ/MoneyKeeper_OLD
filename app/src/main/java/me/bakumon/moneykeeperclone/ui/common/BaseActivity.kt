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

package me.bakumon.moneykeeperclone.ui.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.View
import android.view.ViewGroup
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.Injection
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.ui.UnlockActivity
import me.bakumon.moneykeeperclone.ui.add.AddRecordActivity
import me.bakumon.moneykeeperclone.utill.StatusBarUtil
import me.bakumon.moneykeeperclone.utill.ToastUtils


/**
 * 1.沉浸式状态栏
 * 2.mDisposable
 *
 * @author Bakumon
 * @date 18-1-17
 */
abstract class BaseActivity : AppCompatActivity() {

    init {
        if (ConfigManager.isThemeDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    /**
     * 子类必须实现，用于创建 view
     *
     * @return 布局文件 Id
     */
    @get:LayoutRes
    protected abstract val layoutId: Int

    /**
     * 是否已经设置了沉浸式状态栏
     */
    private var isSetupImmersive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        onInitView(savedInstanceState)
        lockScreen()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        // 一个不太好的方案
        // 解决 AddRecordActivity 长时间处于后台，被系统回收后，重新打开恢复 AddRecordActivity 后
        // RecordTypeFragment#getType() 方法 view typePage 为空的问题
        // 也就是 RecordTypeFragment getView 为空
    }

    /**
     * 初始化 view
     * 比 onInit 早执行
     *
     * @param savedInstanceState 保存的 Bundle
     */
    protected abstract fun onInitView(savedInstanceState: Bundle?)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        onInit(savedInstanceState)
    }

    /**
     * 初始化
     */
    protected abstract fun onInit(savedInstanceState: Bundle?)

    /**
     * 设置沉浸式状态栏
     */
    private fun setImmersiveStatus() {
        val views = setImmersiveView()
        if (views.isEmpty()) {
            return
        }
        StatusBarUtil.immersive(this)
        if (isChangeStatusColor() && AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            StatusBarUtil.darkMode(this)
        }
        for (view in views) {
            StatusBarUtil.setPaddingSmart(this, view)
        }
    }

    open fun isChangeStatusColor(): Boolean {
        return true
    }

    /**
     * 子类可以重写该方法设置沉浸式状态栏
     *
     * @return view[]大小为0,则不启用沉浸式
     */
    open fun setImmersiveView(): Array<View> {
        // 默认使用第一个子 View
        val contentView: ViewGroup = this.findViewById(android.R.id.content)
        val rootView = contentView.getChildAt(0) as ViewGroup
        return arrayOf(rootView.getChildAt(0))
    }

    override fun onResume() {
        super.onResume()
        if (!isSetupImmersive) {
            setImmersiveStatus()
            isSetupImmersive = true
        }
    }

    /**
     * 实例化 BaseViewModel 子类
     */
    inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    // 锁屏
    private fun lockScreen() {
        createCount++
        if (createCount == 1) {

            when (ConfigManager.lockScreenState) {
                0 -> {
                }
                1 -> {
                    // 系统解锁界面
                    if (!(this is AddRecordActivity && ConfigManager.lockAdd)) {
                        // 自定义指纹解锁
                        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
                        if (keyguardManager != null && keyguardManager.isKeyguardSecure) {
                            val intent = keyguardManager.createConfirmDeviceCredentialIntent(getString(R.string.text_unlock), getString(R.string.text_unlock_to_billing))
                            startActivityForResult(intent, REQUEST_CODE_KEYGUARD)
                        } else {
                            ConfigManager.setLockScreenState(0)
                            ToastUtils.show(R.string.text_unlock_close_system)
                        }
                    }
                }
                2 -> {
                    // 自定义指纹解锁
                    if (!(this is AddRecordActivity && ConfigManager.lockAdd)) {
                        val fingerprintIdentify = FingerprintIdentify(App.instance.applicationContext)
                        if (fingerprintIdentify.isFingerprintEnable) {
                            startActivityForResult(Intent(this, UnlockActivity::class.java), REQUEST_CODE_CUSTOMER)
                        } else {
                            ConfigManager.setLockScreenState(0)
                            ToastUtils.show(R.string.text_unlock_close_customer)
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 存在问题：因为在 finish 后，onDestroy 不会及时执行，导致关闭锁屏界面后，迅速打开app，会直接进入应用
        createCount--
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_KEYGUARD) {
            // 系统解锁界面，解锁结果
            if (resultCode != Activity.RESULT_OK) {
                // 解锁失败
                finish()
            }
        } else if (requestCode == REQUEST_CODE_CUSTOMER) {
            // 自定义指纹解锁
            if (resultCode != Activity.RESULT_OK) {
                // 解锁失败
                finish()
            }
        }
    }

    companion object {
        // activity 数量
        private var createCount = 0
        private const val REQUEST_CODE_KEYGUARD = 12
        private const val REQUEST_CODE_CUSTOMER = 13
    }
}