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

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable
import java.util.*

/**
 * 标签
 *
 * @author bakumon https://bakumon.me
 */
@Entity(indices = [(Index(value = arrayOf("name"), unique = true))])
open class Label : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name = "name")
    var name: String
    /**
     * 状态
     * 0：正常
     * 1：已删除
     */
    @ColumnInfo(name = "state")
    var state: Int

    @ColumnInfo(name = "create_time")
    var createTime: Date

    /**
     * 排序
     */
    @ColumnInfo(name = "ranking")
    var ranking: Int? = 0

    constructor(name: String) {
        this.name = name
        this.state = 0
        this.createTime = Date()
    }
}