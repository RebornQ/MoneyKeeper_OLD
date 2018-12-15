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
package me.bakumon.moneykeeper.ui.settings

import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.Constant
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.utill.AndroidUtil
import me.bakumon.moneykeeper.utill.ToastUtils
import me.bakumon.moneykeeper.widget.WidgetProvider

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey)
        setupPreferences()
    }

    private fun setupPreferences() {
        // 反馈
        findPreference("feedback").setOnPreferenceClickListener {
            AndroidUtil.openWeb(context!!, Constant.getUrlTucao())
            true
        }
        // 隐私政策
        findPreference("privacy").setOnPreferenceClickListener {
            AndroidUtil.openWeb(context!!, Constant.URL_PRIVACY)
            true
        }

        findPreference("theme").setOnPreferenceChangeListener { _, newValue ->
            val theme = newValue as String
            DefaultSPHelper.updateTheme(theme)
            activity?.finish()
            true
        }

        findPreference("budget").setOnPreferenceClickListener {
            // 更新 widget
            WidgetProvider.updateWidget(context!!)
            true
        }

        findPreference("symbol").setOnPreferenceClickListener {
            // 更新 widget
            WidgetProvider.updateWidget(context!!)
            true
        }

        findPreference("lockScreen").setOnPreferenceChangeListener { preference, newValue ->
            when (newValue.toString()) {
                "system" -> {
                    // 系统解锁
                    val keyguardManager = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
                    if (keyguardManager != null && keyguardManager.isKeyguardSecure) {
                        true
                    } else {
                        ToastUtils.show(R.string.text_unlock_tip)
                        false
                    }
                }
                "custom" -> {
                    // 自定义指纹解锁界面
                    val fingerprintIdentify = FingerprintIdentify(App.instance.applicationContext)
                    fingerprintIdentify.init()
                    if (fingerprintIdentify.isFingerprintEnable) {
                        true
                    } else {
                        ToastUtils.show(R.string.text_unlock_finger_print)
                        false
                    }
                }
                else -> {
                    true
                }
            }
        }
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}
