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
package me.bakumon.moneykeeper.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import me.bakumon.moneykeeper.ConfigManager

/**
 * 桌面小部件
 *
 * @author Bakumon https://bakumon.me
 */
class WidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        UpdateWidgetService.updateWidget(context)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        ConfigManager.setIsWidgetEnable(true)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        ConfigManager.setIsWidgetEnable(false)
    }

    companion object {
        fun updateWidget(context: Context) {
            if (ConfigManager.isWidgetEnable) {
                val intent = Intent()
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                context.sendBroadcast(intent)
            }
        }
    }
}