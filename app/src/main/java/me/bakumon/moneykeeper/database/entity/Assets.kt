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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

/**
 * 资产
 *
 * @author bakumon https://bakumon.me
 */
@Entity
open class Assets : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    @ColumnInfo(name = "name")
    var name: String
    @ColumnInfo(name = "img_name")
    var imgName: String
    @ColumnInfo(name = "type")
    var type: Int
    /**
     * 状态
     * 0：正常
     * 1：已删除
     */
    @ColumnInfo(name = "state")
    var state: Int
    @ColumnInfo(name = "remark")
    var remark: String
    @ColumnInfo(name = "create_time")
    var createTime: Date
    @ColumnInfo(name = "money")
    var money: BigDecimal

    /**
     * 排序
     */
    @ColumnInfo(name = "ranking")
    var ranking: Int? = 0

    /**
     * 初始化金额
     */
    @ColumnInfo(name = "init_money")
    var initMoney: BigDecimal

    constructor(name: String, imgName: String, type: Int, remark: String, initMoney: BigDecimal, money: BigDecimal) {
        this.name = name
        this.imgName = imgName
        this.type = type
        this.state = 0
        this.remark = remark
        this.createTime = Date()
        this.money = money
        this.initMoney = initMoney
    }
}