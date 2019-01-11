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

import android.annotation.SuppressLint
import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间工具类
 *
 * @author Bakumon https://bakumon.me
 */
object DateUtils {
    @SuppressLint("SimpleDateFormat")
    private val FORMAT: DateFormat = SimpleDateFormat("yyyy-MM")
    @SuppressLint("SimpleDateFormat")
    private val ALL_FORMAT: DateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    @SuppressLint("SimpleDateFormat")
    private val MONTH_DAY_FORMAT: DateFormat = SimpleDateFormat("MM-dd")
    @SuppressLint("SimpleDateFormat")
    private val YEAR_MONTH_DAY_FORMAT: DateFormat = SimpleDateFormat("yyyy-MM-dd")

    /**
     * 获取今天 Date 对象
     *
     * @return Date 对象
     */
    fun getTodayDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        // 这里特别续一秒
        calendar.set(Calendar.SECOND, 1)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * @month 2018-07
     * @return 6
     */
    fun month2Index(month: String): Int {
        val calendar = Calendar.getInstance()
        calendar.time = string2Date(month, FORMAT)
        return calendar.get(Calendar.MONTH)
    }

    /**
     * 获取当前年份月份
     */
    fun getCurrentYearMonth(): String {
        val calendar = Calendar.getInstance()
        return date2String(calendar.time, FORMAT)
    }

    /**
     * 获取当前年份
     */
    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }

    /**
     * 获取修正后的当前月份
     */
    fun getCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH) + 1
    }

    /**
     * 获取当前月份开始时刻的 Date
     * 比如当前是 2018年4月
     * 返回的 Date 是 format 后： 2018-04-01T00:00:00.000+0800
     *
     * @return 当前月份开始的 Date
     */
    fun getCurrentMonthStart(): Date {
        val calendar = Calendar.getInstance()
        return getMonthStart(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    /**
     * 获取当前月份结束时刻的 Date
     * 比如当前是 2018年4月
     * 返回的 Date 是 format 后： 2018-04-30T23:59:59.999+0800
     *
     * @return 当前月份结束的 Date
     */
    fun getCurrentMonthEnd(): Date {
        val calendar = Calendar.getInstance()
        return getMonthEnd(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    /**
     * 根据年月日创建 Date 对象
     *
     * @param year        年
     * @param monthOfYear 月
     * @param dayOfMonth  日
     * @return Date 对象
     */
    fun getDate(year: Int, monthOfYear: Int, dayOfMonth: Int): Date? {
        // 这里特别续一秒
        val time = year.toString() + "-" + monthOfYear + "-" + dayOfMonth + "-00-00-01"
        return string2Date(time, ALL_FORMAT)
    }

    /**
     * 时间字符串转化为 Date 对象
     *
     * @param time   时间字符串
     * @param format DateFormat 格式化类
     * @return Date 对象
     */
    fun string2Date(time: String, format: DateFormat): Date? {
        try {
            return format.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Date 对象转化为 时间字符串
     *
     * @param date   Date 对象
     * @param format DateFormat 格式化类
     * @return 时间字符串
     */
    fun date2String(date: Date, format: DateFormat): String {
        return format.format(date)
    }

    /**
     * Date 对象转化为xx月xx日格式字符串
     *
     * @param date Date 对象
     * @return xx月xx日 字符串
     */
    fun date2MonthDay(date: Date): String {
        return date2String(date, MONTH_DAY_FORMAT)
    }

    /**
     * 判断两个 Date 是否是同一天
     *
     * @param date1 Date 对象
     * @param date2 Date 对象
     * @return true:同一天
     */
    fun isSameDay(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date1

        val calendar2 = Calendar.getInstance()
        calendar2.time = date2

        val year1 = calendar1.get(Calendar.YEAR)
        val year2 = calendar2.get(Calendar.YEAR)

        val month1 = calendar1.get(Calendar.MONTH)
        val month2 = calendar2.get(Calendar.MONTH)

        val day1 = calendar1.get(Calendar.DAY_OF_MONTH)
        val day2 = calendar2.get(Calendar.DAY_OF_MONTH)

        return year1 == year2 && month1 == month2 && day1 == day2
    }

    /**
     * 格式化年月
     *
     * @param year  年份
     * @param month 月份（字面）
     */
    fun getYearMonthFormatString(year: Int, month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        return date2String(calendar.time, FORMAT)
    }

    /**
     * 获取某月有多少天
     *
     * @param year  年份
     * @param month 月份
     * @return 该月的天数
     */
    fun getDayCount(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取某月份开始时刻的 Date
     *
     * @param year  年份
     * @param month 月份
     * @return 当前月份开始的 Date
     */
    fun getMonthStart(year: Int, month: Int): Date {
        val c = Calendar.getInstance()
        c.clear()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month - 1)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND))
        return calendar.time
    }

    /**
     * 获取某月份结束时刻的 Date
     *
     * @param year  年份
     * @param month 月份
     * @return 当前月份结束的 Date
     */
    fun getMonthEnd(year: Int, month: Int): Date {
        val c = Calendar.getInstance()
        // 解决 getActualMaximum 总是当前月份的最大天数
        c.clear()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month - 1)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, c.getActualMaximum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, c.getActualMaximum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, c.getActualMaximum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, c.getActualMaximum(Calendar.MILLISECOND))
        return calendar.time
    }

    /**
     * 获取某年开始时刻的 Date
     *
     * @param year  年份
     * @return 当前年开始的 Date
     */
    fun getYearStart(year: Int): Date {
        val c = Calendar.getInstance()
        c.clear()
        c.set(Calendar.YEAR, year)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, c.getActualMinimum(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND))
        return calendar.time
    }

    /**
     * 获取某年结束时刻的 Date
     *
     * @param year  年份
     * @return 某年结束的 Date
     */
    fun getYearEnd(year: Int): Date {
        val c = Calendar.getInstance()
        c.clear()
        c.set(Calendar.YEAR, year)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, c.getActualMaximum(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, c.getActualMaximum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, c.getActualMaximum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, c.getActualMaximum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, c.getActualMaximum(Calendar.MILLISECOND))
        return calendar.time
    }

    fun getTodayStart(): Date {
        return Date(todayStartMillis)
    }

    fun getTodayEnd(): Date {
        return Date(todayEndMillis)
    }

    /**
     * 获取今天开始时间戳
     *
     * @return 今天开始时间戳
     */
    private val todayStartMillis: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

    /**
     * 获取今天结束时间戳
     *
     * @return 今天结束时间戳
     */
    private val todayEndMillis: Long
        get() {
            val calendar = Calendar.getInstance()
            val maxHour = calendar.getActualMaximum(Calendar.HOUR_OF_DAY)
            val maxMinute = calendar.getActualMaximum(Calendar.MINUTE)
            val maxSecond = calendar.getActualMaximum(Calendar.SECOND)
            val maxMillisecond = calendar.getActualMaximum(Calendar.MILLISECOND)

            calendar.set(Calendar.HOUR_OF_DAY, maxHour)
            calendar.set(Calendar.MINUTE, maxMinute)
            calendar.set(Calendar.SECOND, maxSecond)
            calendar.set(Calendar.MILLISECOND, maxMillisecond)

            return calendar.timeInMillis
        }

    /**
     * 获取字面时间
     * 如：
     * 1.今天
     * 2.昨天
     * 3.当年内，4月20日
     * 4.超过当年，2017年3月30日
     *
     * @return 字面时间
     */
    fun getWordTime(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        val dateMillis = cal.timeInMillis

        val todayStartMillis = todayStartMillis
        val todayEndMillis = todayEndMillis

        val oneDayMillis = todayEndMillis - todayStartMillis + 1

        return if (dateMillis in todayStartMillis..todayEndMillis) {
            App.instance.resources.getString(R.string.text_today)
        } else if (dateMillis >= todayStartMillis - oneDayMillis && dateMillis <= todayEndMillis - oneDayMillis) {
            App.instance.resources.getString(R.string.text_yesterday)
        } else if (cal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            date2String(date, MONTH_DAY_FORMAT)
        } else {
            date2String(date, YEAR_MONTH_DAY_FORMAT)
        }
    }
}
