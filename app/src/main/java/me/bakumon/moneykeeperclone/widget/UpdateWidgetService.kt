package me.bakumon.moneykeeperclone.widget

import android.app.IntentService
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.Injection
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.ui.home.HomeActivity
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import java.math.BigDecimal

/**
 * 更新 Widget
 * @author Bakumon https://bakumon.me
 */
class UpdateWidgetService : IntentService("UpdateWidgetService") {

    override fun onHandleIntent(intent: Intent?) {
        val pendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, HomeActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)

        val views = RemoteViews(packageName, R.layout.layout_widget)
        val text = if (ConfigManager.symbol.isEmpty()) "" else "(" + ConfigManager.symbol + ")"

        views.setTextViewText(R.id.tv_today_outlay_text, applicationContext.getText(R.string.text_widget_today_outlay).toString() + text)
        views.setTextViewText(R.id.tv_month_outlay_text, applicationContext.getText(R.string.text_widget_month_outlay).toString() + text)
        views.setTextViewText(R.id.tv_budget_text, applicationContext.getText(R.string.text_widget_month_budget).toString() + text)
        views.setOnClickPendingIntent(R.id.ll_root, pendingIntent)

        setData(views)

        val componentName = ComponentName(this, WidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        appWidgetManager.updateAppWidget(componentName, views)
    }

    private fun setData(views: RemoteViews) {
        val dataSource = Injection.provideUserDataSource()

        // 今日支出
        val todayOutlay = dataSource.getTodayOutlay()
        val todayOutlayStr = if (todayOutlay.isEmpty()) {
            "-"
        } else {
            BigDecimalUtil.fen2Yuan(todayOutlay[0].daySumMoney)
        }
        views.setTextViewText(R.id.tv_today_outlay, todayOutlayStr)

        // 月支出
        val monthOutlay = dataSource.getCurrentOutlay()
        var monthOutlayStr = "-"

        var outlay = BigDecimal(0)
        if (monthOutlay.isNotEmpty()) {
            for ((type, sumMoney) in monthOutlay) {
                if (type == RecordType.TYPE_OUTLAY) {
                    outlay = sumMoney
                    monthOutlayStr = BigDecimalUtil.fen2Yuan(sumMoney)
                }
            }
        }
        views.setTextViewText(R.id.tv_month_outlay, monthOutlayStr)


        val budget = ConfigManager.budget
        val budgetStr = if (budget > 0) {
            val budgetStr = BigDecimalUtil.fen2Yuan(BigDecimal(budget).multiply(BigDecimal(100)).subtract(outlay))
            budgetStr
        } else {
            "-"
        }
        views.setTextViewText(R.id.tv_budget, budgetStr)
    }

    companion object {
        fun updateWidget(context: Context) {
            if (App.instance.isAppForeground()) {
                val intent = Intent(context, UpdateWidgetService::class.java)
                context.startService(intent)
            }
        }
    }
}
