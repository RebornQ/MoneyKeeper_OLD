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

package me.bakumon.moneykeeper.utill

import java.util.*

/**
 * 皮一下
 */
object Pi {
    private val piList = listOf(
        "\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83E\uDD23",
        "\uD83E\uDD23", "\uD83D\uDE03", "\uD83D\uDE04", "\uD83D\uDE0A",
        "\uD83D\uDE05", "\uD83D\uDE06", "\uD83D\uDE09", "\uD83D\uDE0B",
        "\uD83D\uDE0E", "\uD83D\uDE0D", "\uD83D\uDE18", "\uD83D\uDE17",
        "\uD83D\uDE19", "\uD83D\uDE1A", "\uD83D\uDE42", "\uD83E\uDD17",
        "\uD83E\uDD14", "\uD83D\uDE10", "\uD83D\uDE11", "\uD83D\uDE36",
        "\uD83D\uDE44", "\uD83D\uDE0F", "\uD83D\uDE23", "\uD83D\uDE25"
    )

    fun randomPi(): String {
        return piList[Random().nextInt(piList.size)]
    }
}