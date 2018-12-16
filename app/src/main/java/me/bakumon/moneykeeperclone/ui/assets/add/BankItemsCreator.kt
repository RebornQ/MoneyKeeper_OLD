package me.bakumon.moneykeeperclone.ui.assets.add

import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.R
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
