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

package me.bakumon.moneykeeper.ui.assets.add

import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.R
import me.drakeet.multitype.Items

/**
 * BankItemsCreator
 *
 * @author Bakumon https://bakumon.me
 */
object BankItemsCreator {
    /**
     * multitype 添加开源库 item 实体
     */
    fun addAll(items: Items) {
        items.add(Bank(App.instance.getString(R.string.text_bank_zhaoshang), "bank_zhaoshang"))
        items.add(Bank(App.instance.getString(R.string.text_bank_icbc), "bank_icbc"))
        items.add(Bank(App.instance.getString(R.string.text_bank_abchina), "bank_abchina"))
        items.add(Bank(App.instance.getString(R.string.text_bank_boc), "bank_boc"))
        items.add(Bank(App.instance.getString(R.string.text_bank_ccb), "bank_ccb"))
        items.add(Bank(App.instance.getString(R.string.text_bank_pingan), "bank_pingan"))
        items.add(Bank(App.instance.getString(R.string.text_bank_bankcomm), "bank_bankcomm"))
        items.add(Bank(App.instance.getString(R.string.text_bank_citicbank), "bank_citicbank"))
        items.add(Bank(App.instance.getString(R.string.text_bank_cib), "bank_cib"))
        items.add(Bank(App.instance.getString(R.string.text_bank_cebbank), "bank_cebbank"))
        items.add(Bank(App.instance.getString(R.string.text_bank_cmbc), "bank_cmbc"))
        items.add(Bank(App.instance.getString(R.string.text_bank_chinapost), "bank_chinapost"))
        items.add(Bank(App.instance.getString(R.string.text_assets_card), "assets_card"))
    }
}
