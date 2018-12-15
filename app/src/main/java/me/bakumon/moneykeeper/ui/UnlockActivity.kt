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

package me.bakumon.moneykeeper.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint
import kotlinx.android.synthetic.main.activity_unlock.*
import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.utill.StatusBarUtil
import me.bakumon.moneykeeper.utill.ToastUtils

/**
 * 指纹解锁
 *
 * @author Bakumon https://bakumon.me
 */
class UnlockActivity : AppCompatActivity() {

    private lateinit var mFingerprintIdentify: FingerprintIdentify

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock)
        StatusBarUtil.immersive(this)
    }

    override fun onResume() {
        super.onResume()
        // 构造对象
        mFingerprintIdentify = FingerprintIdentify(App.instance.applicationContext)
        mFingerprintIdentify.init()

        // 指纹硬件可用并已经录入指纹 mFingerprintIdentify.isFingerprintEnable
        // 指纹硬件是否可用 mFingerprintIdentify.isHardwareEnable
        // 是否已经录入指纹 mFingerprintIdentify.isRegisteredFingerprint
        if (!mFingerprintIdentify.isFingerprintEnable) {
            setResult(Activity.RESULT_OK)
            DefaultSPHelper.turnOffLockScreen()
            ToastUtils.show(R.string.text_unlock_close_customer)
            finish()
            return
        }
        mFingerprintIdentify.startIdentify(5, object : BaseFingerprint.IdentifyListener {
            override fun onSucceed() {
                // 验证成功，自动结束指纹识别
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onNotMatch(availableTimes: Int) {
                // 指纹不匹配，并返回可用剩余次数并自动继续验证
                tvTip.text = getString(R.string.text_unlock_notMatch, availableTimes)
            }

            override fun onFailed(isDeviceLocked: Boolean) {
                // 错误次数达到上限或者API报错停止了验证，自动结束指纹识别
                // isDeviceLocked 表示指纹硬件是否被暂时锁定
                tvTip.text = getString(R.string.text_unlock_failed)
            }

            override fun onStartFailedByDeviceLocked() {
                // 第一次调用startIdentify失败，因为设备被暂时锁定
                tvTip.text = getString(R.string.text_unlock_failed_device_locked)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭指纹识别
        mFingerprintIdentify.cancelIdentify()
    }
}
