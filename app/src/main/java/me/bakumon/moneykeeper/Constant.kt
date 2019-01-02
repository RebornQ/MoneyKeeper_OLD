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

/**
 * 一些常量
 * @author Bakumon https://bakumon.me
 */
object Constant {
    const val AUTHOR_EMAIL = "bakumon.me@gmail.com"
    const val QQ_GROUP = "945643665"
    const val TG_GROUP = "https://t.me/thatmoney"
    const val URL_PRIVACY = "https://github.com/Bakumon/MoneyKeeper/blob/master/PrivacyPolicy.md"
    const val URL_CHANGELOG = "http://t.cn/E4cB8oC"
    const val AUTHOR_URL = "https://github.com/Bakumon"
    const val APP_OPEN_SOURCE_URL = "https://github.com/Bakumon/MoneyKeeper"
    const val URL_GREEN_ANDROID = "https://green-android.org/"

    fun getUrlTucao(): String {
        val baseUrl = "https://support.qq.com/product/" + if (BuildConfig.DEBUG) "41006" else "41058"
        // clientVersion：app 版本，如 v3.3.2_26
        val clientVersion = BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE
        return "$baseUrl?clientVersion=$clientVersion"
    }
}
