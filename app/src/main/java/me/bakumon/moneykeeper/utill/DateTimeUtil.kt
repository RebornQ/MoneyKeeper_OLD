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

    /**
     * 获取自定义开始日期的月份范围
     */
    fun getCustomMonth(beforeOffset: Long = 0): Result {
        // TODO 设置项
        val customStartDay = 12

        val today = LocalDate.now()
        val temp = LocalDate.of(today.year, today.month, customStartDay)
        val customStart = if (temp.isBefore(today) || temp.isEqual(today)) {
            temp
        } else {
            temp.minusMonths(1)
        }

        val start: LocalDate = customStart.minusMonths(beforeOffset)
        val end = if (beforeOffset == 1L) {
            customStart.minusDays(1L)
        } else {
            customStart.minusMonths(beforeOffset - 1).minusDays(1L)
        }
        return Result(localDate2DateStart(start), localDate2DateEnd(end), getDisplayDate(start, end))
    }

    /**
     * LocalDate 转换为 Date
     * @return LocalDate 日期开始 time
     */
    private fun localDate2DateStart(localDate: LocalDate): Date {
        val calendar = Calendar.getInstance()
        calendar.set(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth)

        val temp = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, temp.getActualMinimum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, temp.getActualMinimum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, temp.getActualMinimum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, temp.getActualMinimum(Calendar.MILLISECOND))

        return calendar.time
    }

    /**
     * LocalDate 转换为 Date
     * @return LocalDate 日期结束 time
     */
    private fun localDate2DateEnd(localDate: LocalDate): Date {
        val calendar = Calendar.getInstance()
        calendar.set(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth)

        val temp = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, temp.getActualMaximum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, temp.getActualMaximum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, temp.getActualMaximum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, temp.getActualMaximum(Calendar.MILLISECOND))

        return calendar.time
    }

    /**
     * 获取自定义月份范围显示字符串
     * @return
     * 同年且今年：01.15～02.14
     * 同年且不在今年：2018.11.15～12.14
     * 不同年：2018.12.15～2019.01.14
     */
    private fun getDisplayDate(startLocalDate: LocalDate, endLocalDate: LocalDate): String {
        val today = LocalDate.now()
        return if (startLocalDate.year == endLocalDate.year) {
            if (today.year == startLocalDate.year) {
                "" + startLocalDate.monthValue + "." + startLocalDate.dayOfMonth + "~" + endLocalDate.monthValue + "." + endLocalDate.dayOfMonth
            } else {
                "" + startLocalDate.year + "." + startLocalDate.monthValue + "." + startLocalDate.dayOfMonth + "~" + endLocalDate.monthValue + "." + endLocalDate.dayOfMonth
            }
        } else {
            "" + startLocalDate.year + "." + startLocalDate.monthValue + "." + startLocalDate.dayOfMonth + "~" + endLocalDate.year + "." + endLocalDate.monthValue + "." + endLocalDate.dayOfMonth
        }
    }
}