package me.bakumon.moneykeeperclone.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import me.bakumon.moneykeeperclone.ConfigManager

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