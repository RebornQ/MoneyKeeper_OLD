package me.bakumon.moneykeeperclone.utill

import java.util.*

/**
 * 皮一下
 */
object Pi {
    private val piList = listOf("\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83E\uDD23",
            "\uD83E\uDD23", "\uD83D\uDE03", "\uD83D\uDE04", "\uD83D\uDE0A",
            "\uD83D\uDE05", "\uD83D\uDE06", "\uD83D\uDE09", "\uD83D\uDE0B",
            "\uD83D\uDE0E", "\uD83D\uDE0D", "\uD83D\uDE18", "\uD83D\uDE17",
            "\uD83D\uDE19", "\uD83D\uDE1A", "\uD83D\uDE42", "\uD83E\uDD17",
            "\uD83E\uDD14", "\uD83D\uDE10", "\uD83D\uDE11", "\uD83D\uDE36",
            "\uD83D\uDE44", "\uD83D\uDE0F", "\uD83D\uDE23", "\uD83D\uDE25")

    fun randomPi(): String {
        return piList[Random().nextInt(piList.size)]
    }
}