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

package me.bakumon.moneykeeper.database.entity

import androidx.room.*
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

/**
 * 资产调整记录
 *
 * @author bakumon https://bakumon.me
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = Assets::class,
        parentColumns = ["id"],
        onDelete = ForeignKey.CASCADE,
        childColumns = ["assets_id"]
    )
    ],
    indices = [(Index(value = arrayOf("assets_id", "create_time")))]
)
open class AssetsModifyRecord : Serializable {
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

    @ColumnInfo(name = "assets_id")
    var assetsId: Int

    /**
     * 调整余额前金额
     */
    @ColumnInfo(name = "money_before")
    var moneyBefore: BigDecimal

    /**
     * 调整余额后金额
     */
    @ColumnInfo(name = "money")
    var money: BigDecimal

    constructor(assetsId: Int, money: BigDecimal, moneyBefore: BigDecimal) {
        this.assetsId = assetsId
        this.money = money
        this.moneyBefore = moneyBefore
    }
}