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

package me.bakumon.moneykeeperclone.database.entity

import android.arch.persistence.room.*
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

/**
 * 记账记录
 *
 * @author bakumon https://bakumon.me
 */
@Entity(
        foreignKeys = [ForeignKey(
                entity = RecordType::class,
                parentColumns = ["id"],
                childColumns = ["record_type_id"]
        )],
        indices = [(Index(value = arrayOf("record_type_id", "time", "money", "create_time")))]
)
open class Record : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var money: BigDecimal? = null

    var remark: String? = null

    var time: Date? = null

    @ColumnInfo(name = "create_time")
    var createTime: Date? = null

    @ColumnInfo(name = "record_type_id")
    var recordTypeId: Int = 0

    @ColumnInfo(name = "assets_id")
    var assetsId: Int? = -1
}
