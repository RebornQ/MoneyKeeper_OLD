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
 * 资产转账记录
 *
 * @author bakumon https://bakumon.me
 */
@Entity(
        foreignKeys = [ForeignKey(
                entity = Assets::class,
                parentColumns = ["id"],
                onDelete = ForeignKey.CASCADE,
                childColumns = ["assets_id_form"]
        )
            , ForeignKey(
                    entity = Assets::class,
                    parentColumns = ["id"],
                    onDelete = ForeignKey.CASCADE,
                    childColumns = ["assets_id_to"]
            )
        ],
        indices = [(Index(value = arrayOf("assets_id_form", "assets_id_to")))]

)
open class AssetsTransferRecord : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    /**
     * 状态
     * 0：正常
     * 1：已删除
     */
    @ColumnInfo(name = "state")
    var state: Int = 0

    @ColumnInfo(name = "create_time")
    var createTime: Date = Date()

    @ColumnInfo(name = "time")
    var time: Date

    @ColumnInfo(name = "assets_id_form")
    var assetsIdFrom: Int

    @ColumnInfo(name = "assets_id_to")
    var assetsIdTo: Int

    @ColumnInfo(name = "remark")
    var remark: String

    /**
     * 转账金额
     */
    @ColumnInfo(name = "money")
    var money: BigDecimal

    @Ignore
    constructor() {
        this.assetsIdFrom = 0
        this.assetsIdTo = 0
        this.money = BigDecimal(0)
        this.time = Date()
        this.remark = ""
    }

    constructor(assetsIdFrom: Int, assetsIdTo: Int, money: BigDecimal, time: Date, remark: String) {
        this.assetsIdFrom = assetsIdFrom
        this.assetsIdTo = assetsIdTo
        this.money = money
        this.time = time
        this.remark = remark
    }
}