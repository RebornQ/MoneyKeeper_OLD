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

package me.bakumon.moneykeeper

import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

/**
 * 默认 Preference
 * @author Bakumon https://bakumon.me
 */
object DefaultSPHelper {
    /**
     * 月预算
     */
    val budget: String
        get() {
            val temp = PreferenceManager.getDefaultSharedPreferences(App.instance).getString("budget", "")
            return if (temp.isNullOrBlank()) {
                ""
            } else {
                temp
            }
        }

    /**
     * 货币符号
     */
    val symbol: String
        get() {
            val temp = PreferenceManager.getDefaultSharedPreferences(App.instance).getString("symbol", "¥")
            return if (temp.isNullOrBlank()) {
                ""
            } else {
                temp
            }
        }

    /**
     * 初始化主题
     */
    fun initTheme() {
        val theme = PreferenceManager.getDefaultSharedPreferences(App.instance).getString("theme", "dark")
        updateTheme(theme)
    }

    /**
     * 更新主题
     */
    fun updateTheme(newTheme: String?) {
        if (!TextUtils.equals("dark", newTheme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    /**
     * 锁屏模式
     */
    val lockScreenMode: String?
        get() = PreferenceManager.getDefaultSharedPreferences(App.instance).getString("lockScreen", "off")

    /**
     * 关闭锁屏模式
     */
    fun turnOffLockScreen() {
        PreferenceManager.getDefaultSharedPreferences(App.instance).edit().putString("lockScreen", "off").apply()
    }

    /**
     * 是否快速记账
     */
    val isFast: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(App.instance).getBoolean("fast", false)

    /**
     * 是否本地自动备份
     */
    var isLocalAutoBackup: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(App.instance).getBoolean("localAutoBackup", true)
        set(value) = PreferenceManager.getDefaultSharedPreferences(App.instance).edit().putBoolean(
            "localAutoBackup",
            value
        ).apply()

    /**
     * 本地备份保存路径
     */
    val localBackupFilePath: String?
        get() = PreferenceManager.getDefaultSharedPreferences(App.instance).getString("localBackupFilePath", "")

    /**
     * 云备份是否可用
     */
    var isCloudBackupEnable: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(App.instance).getBoolean("webdavEnable", false)
        set(value) = PreferenceManager.getDefaultSharedPreferences(App.instance).edit().putBoolean(
            "webdavEnable",
            value
        ).apply()

    /**
     * WebDAV 地址
     */
    val webdavUrl: String
        get() {
            val url = PreferenceManager.getDefaultSharedPreferences(App.instance).getString("webdavUrl", "")
            return if (url.isNullOrBlank()) {
                ""
            } else {
                url
            }
        }

    /**
     * WebDAV 用户名
     */
    val webdavUserName: String
        get() {
            val userName = PreferenceManager.getDefaultSharedPreferences(App.instance).getString("webdavUserName", "")
            return if (userName.isNullOrBlank()) {
                ""
            } else {
                userName
            }
        }

    /**
     * WebDAV 密码
     */
    val webdavPsw: String
        get() {
            val psw = PreferenceManager.getDefaultSharedPreferences(App.instance).getString("webdavPsw", "")
            return if (psw.isNullOrBlank()) {
                ""
            } else {
                psw
            }
        }

    /**
     * 云备份模式
     */
    private val cloudBackupMode: String?
        get() = PreferenceManager.getDefaultSharedPreferences(App.instance).getString("cloudAutoBackupMode", "off")

    /**
     * 云备份模式
     * 打开 app 时备份
     */
    val isCloudBackupWhenOpenApp: Boolean
        get() = TextUtils.equals(cloudBackupMode, "openApp")

    /**
     * 云备份模式
     * 退出 app 时备份
     */
    val isCloudBackupWhenQuitApp: Boolean
        get() = TextUtils.equals(cloudBackupMode, "quitApp")
}