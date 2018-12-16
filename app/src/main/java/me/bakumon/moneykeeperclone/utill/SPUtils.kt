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

package me.bakumon.moneykeeperclone.utill

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import me.bakumon.moneykeeperclone.App

/**
 * SharedPreferences 工具类
 *
 * @author Bakumon https://bakumon.me
 */
class SPUtils private constructor(spName: String) {
    private val sp: SharedPreferences = App.instance.getSharedPreferences(spName, Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: SPUtils? = null

        fun getInstance(spName: String): SPUtils? {
            if (INSTANCE == null) {
                synchronized(SPUtils::class) {
                    if (INSTANCE == null) {
                        INSTANCE = SPUtils(if (TextUtils.isEmpty(spName)) "spUtils" else spName)
                    }
                }
            }
            return INSTANCE
        }
    }

    fun put(key: String, value: String): Boolean {
        return sp.edit().putString(key, value).commit()
    }

    @JvmOverloads
    fun getString(key: String, defaultValue: String = ""): String {
        return sp.getString(key, defaultValue)
    }

    fun put(key: String, value: Int): Boolean {
        return sp.edit().putInt(key, value).commit()
    }

    @JvmOverloads
    fun getInt(key: String, defaultValue: Int = -1): Int {
        return sp.getInt(key, defaultValue)
    }

    fun put(key: String, value: Boolean): Boolean {
        return sp.edit().putBoolean(key, value).commit()
    }

    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    fun put(key: String, value: Long): Boolean {
        return sp.edit().putLong(key, value).commit()
    }

    @JvmOverloads
    fun getLong(key: String, defaultValue: Long = -1L): Long {
        return sp.getLong(key, defaultValue)
    }

}