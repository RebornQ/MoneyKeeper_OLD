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

import org.threeten.bp.LocalDate
import java.util.*

/**
 * 日期时间工具类
 * 使用 java8 java.time API
 *
 * @author Bakumon https://bakumon.me
 */
object DateTimeUtil {

    data class Result(val startDate: Date, val endDate: Date, val displayDate: String)

    fun getCustomMonth(beforeOffset: Int): Result {

        val customStartDay = 1

        val now = LocalDate.of(2019, 1, 1)

        return Result(Date(), Date(), "")
    }
}