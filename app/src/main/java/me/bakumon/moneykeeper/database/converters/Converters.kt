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

package me.bakumon.moneykeeper.database.converters

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.*

/**
 * 数据库类型转换器
 *
 * @author Bakumon https://bakumon.me
 */
object Converters {
    @JvmStatic
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @JvmStatic
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @JvmStatic
    @TypeConverter
    fun stringToBig(intDecimal: Long): BigDecimal {
        return BigDecimal(intDecimal)
    }

    @JvmStatic
    @TypeConverter
    fun bigToString(bigDecimal: BigDecimal): Long {
        return bigDecimal.toLong()
    }
}
