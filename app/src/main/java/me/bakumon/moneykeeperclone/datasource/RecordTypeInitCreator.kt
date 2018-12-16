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

package me.bakumon.moneykeeperclone.datasource

import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.database.entity.RecordType
import java.util.*


/**
 * 产生初始化的记录类型数据
 *
 * @author Bakumon https://bakumon.me
 */
object RecordTypeInitCreator {

    fun createRecordTypeData(): Array<RecordType> {

        val list = ArrayList<RecordType>()

        val res = App.instance.resources

        var type: RecordType

        // 支出
        type = RecordType(res.getString(R.string.type_eat), "type_eat", 0, 0)
        list.add(type)

        type = RecordType(res.getString(R.string.type_calendar), "type_calendar", 0, 1)
        list.add(type)

        type = RecordType(res.getString(R.string.type_3c), "type_3c", 0, 2)
        list.add(type)

        type = RecordType(res.getString(R.string.type_clothes), "type_clothes", 0, 3)
        list.add(type)

        type = RecordType(res.getString(R.string.type_pill), "type_pill", 0, 4)
        list.add(type)

        type = RecordType(res.getString(R.string.type_candy), "type_candy", 0, 5)
        list.add(type)

        type = RecordType(res.getString(R.string.type_humanity), "type_humanity", 0, 6)
        list.add(type)

        type = RecordType(res.getString(R.string.type_pet), "type_pet", 0, 7)
        list.add(type)

        // 收入
        type = RecordType(res.getString(R.string.type_salary), "type_salary", 1, 0)
        list.add(type)

        type = RecordType(res.getString(R.string.type_pluralism), "type_pluralism", 1, 1)
        list.add(type)

        return list.toTypedArray()
    }

}
