package me.bakumon.moneykeeperclone.ui.add

import java.io.Serializable

class NoAccount(
        /**
         * 0:不选择具体账户
         * 1:增加账户
         */
        var type: Int,
        var title: String,
        var subtitle: String = "") : Serializable