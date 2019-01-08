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

package me.bakumon.moneykeeper.datasource

import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.RecordType
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
        type = RecordType()
        type.name = res.getString(R.string.type_eat)
        type.imgName = "type_eat"
        type.type = 0
        type.ranking = 0
        list.add(type)

        type = RecordType()
        type.name = res.getString(R.string.type_calendar)
        type.imgName = "type_calendar"
        type.type = 0
        type.ranking = 1
        list.add(type)

        type = RecordType()
        type.name = res.getString(R.string.type_3c)
        type.imgName = "type_3c"
        type.type = 0
        type.ranking = 2
        list.add(type)

        type = RecordType()
        type.name = res.getString(R.string.type_clothes)
        type.imgName = "type_clothes"
        type.type = 0
        type.ranking = 3
        list.add(type)

        type = RecordType()
        type.name = res.getString(R.string.type_pill)
        type.imgName = "type_pill"
        type.type = 0
        type.ranking = 4
        list.add(type)

        type = RecordType()
        type.name = res.getString(R.string.type_candy)
        type.imgName = "type_candy"
        type.type = 0
        type.ranking = 5
        list.add(type)

        type = RecordType()
        type.name = res.getString(R.string.type_humanity)
        type.imgName = "type_humanity"
        type.type = 0
        type.ranking = 6
        list.add(type)

        type = RecordType()
        type.name = res.getString(R.string.type_pet)
        type.imgName = "type_pet"
        type.type = 0
        type.ranking = 7
        list.add(type)

        // 收入
        type = RecordType()
        type.name = res.getString(R.string.type_salary)
        type.imgName = "type_salary"
        type.type = 1
        type.ranking = 0
        list.add(type)

        type = RecordType()
        type.name = res.getString(R.string.type_pluralism)
        type.imgName = "type_pluralism"
        type.type = 1
        type.ranking = 1
        list.add(type)

        return list.toTypedArray()
    }

}
