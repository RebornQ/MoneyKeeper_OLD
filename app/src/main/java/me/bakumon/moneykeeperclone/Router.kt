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

package me.bakumon.moneykeeperclone

/**
 * 路由 Url
 *
 * @author Bakumon https://bakumon.me
 */
object Router {
    const val SCHEME = "mkc"

    /**
     * floo 的页面映射 mappings 的 key
     */
    object Url {
        const val URL_HOME = "home"
        const val URL_ADD_RECORD = "add_record"
        const val URL_TYPE_MANAGE = "type_Manage"
        const val URL_TYPE_SORT = "type_sort"
        const val URL_ADD_TYPE = "add_type"
        const val URL_STATISTICS = "statistics"
        const val URL_TYPE_RECORDS = "type_records"
        const val URL_SETTING = "setting"
        const val URL_ABOUT = "about"
        const val URL_REVIEW = "review"
        const val URL_BACKUP = "backup"
        const val URL_OTHER_SETTING = "other_setting"
        const val URL_ASSETS = "assets"
        const val URL_CHOOSE_ASSETS = "choose_assets"
        const val URL_ADD_ASSETS = "add_assets"
        const val URL_ASSETS_DETAIL = "assets_detail"
//        const val URL_ASSETS_TRANSFER = "assets_transfer"
    }

    /**
     * floo stack 使用的key，用于标示已经打开的 activity
     */
    object IndexKey {
        const val INDEX_KEY_HOME = "index_key_home"
    }

    /**
     * floo 传递数据使用的 key
     */
    object ExtraKey {
        const val KEY_IS_SUCCESSIVE = "key_is_successive"
        const val KEY_IS_TRANSFER = "key_is_transfer"
        const val KEY_RECORD_BEAN = "key_record_bean"
        const val KEY_TYPE = "key_type"
        const val KEY_TYPE_BEAN = "key_type_bean"
        const val KEY_TYPE_NAME = "key_type_name"
        const val KEY_RECORD_TYPE = "key_record_type"
        const val KEY_RECORD_TYPE_ID = "key_record_type_id"
        const val KEY_YEAR = "key_year"
        const val KEY_MONTH = "key_month"
        const val KEY_ASSETS_TYPE = "key_assets_type"
        const val KEY_ASSETS = "key_assets"
        const val KEY_ASSETS_ID = "key_assets_id"
        const val KEY_TRANSFER = "key_transfer"
        const val KEY_IS_SHOW_CHOOSE_ASSETS = "key_is_show_choose_assets"
        const val KEY_IS_MODIFY = "key_is_option_modify"
        const val KEY_DATE = "key_date"
        const val KEY_REMARK = "key_remark"
    }
}
