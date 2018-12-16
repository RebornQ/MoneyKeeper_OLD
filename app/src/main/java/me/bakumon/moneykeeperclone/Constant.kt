package me.bakumon.moneykeeperclone

import android.os.Build

/**
 * 一些常量
 * @author Bakumon https://bakumon.me
 */
object Constant {
    const val AUTHOR_EMAIL = "bakumon.me@gmail.com"
    const val QQ_GROUP = "945643665"
    const val TG_GROUP = "https://t.me/thatmoney"
    const val ALIPAY_CODE = "aex01251c8foqaprudcp503"
    const val URL_PRIVACY = "https://github.com/Bakumon/MoneyKeeper/blob/master/PrivacyPolicy.md"
    const val URL_HELP = "http://t.cn/Re07fFB"
    const val NUTSTORE_HELP_URL = "http://help.jianguoyun.com/?p=2064"
    const val AUTHOR_URL = "https://github.com/Bakumon"
    const val APP_OPEN_SOURCE_URL = "https://github.com/Bakumon/MoneyKeeper"
    const val URL_GREEN_ANDROID = "https://green-android.org/"

    fun getUrlTucao(): String {
        val baseUrl = "https://support.qq.com/product/" + if (BuildConfig.DEBUG) "41006" else "41058"
        // clientInfo：系统信息，如 OnePlus5T
        // clientVersion：app 版本，如 v3.3.2_26
        // osVersion：系统版本，如 8.1.0_27
        val clientInfo = Build.PRODUCT
        val clientVersion = BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE
        val osVersion = Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT
        return "$baseUrl?clientInfo=$clientInfo&clientVersion=$clientVersion&osVersion=$osVersion"
    }
}
