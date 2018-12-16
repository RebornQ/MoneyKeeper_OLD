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

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.ui.add.AddRecordActivity
import me.bakumon.moneykeeperclone.ui.statistics.StatisticsActivity
import java.util.*

/**
 * Shortcut 工具类
 *
 * @author Bakumon https://bakumon.me
 */
object ShortcutUtil {
    @TargetApi(Build.VERSION_CODES.N_MR1)
    fun addRecordShortcut(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            return
        }
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
        val intentAdd = Intent(context, AddRecordActivity::class.java)
        intentAdd.action = "LOCATION_SHORTCUT"
        val shortcutAdd = ShortcutInfo.Builder(context, "add")
                .setShortLabel(context.getString(R.string.shortcuts_add_record))
                .setLongLabel(context.getString(R.string.shortcuts_add_record))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcuts_add))
                .setIntent(intentAdd)
                .build()

        val intentStatistics = Intent(context, StatisticsActivity::class.java)
        intentStatistics.action = "LOCATION_SHORTCUT"
        val shortcutStatistics = ShortcutInfo.Builder(context, "statistics")
                .setShortLabel(context.getString(R.string.shortcuts_statistics))
                .setLongLabel(context.getString(R.string.shortcuts_statistics))
                .setIcon(Icon.createWithResource(context, R.drawable.shortcuts_statistics))
                .setIntent(intentStatistics)
                .build()
        if (shortcutManager != null) {
            shortcutManager.dynamicShortcuts = Arrays.asList(shortcutAdd, shortcutStatistics)
        }
    }
}
