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

package me.bakumon.moneykeeper.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.bakumon.moneykeeper.database.entity.RecordType

/**
 * 记账类型表操作类
 *
 * @author Bakumon https://bakumon.me
 */
@Dao
interface RecordTypeDao {

    @Query("SELECT * FROM RecordType WHERE state = 0 ORDER BY ranking")
    fun getAllRecordTypes(): LiveData<List<RecordType>>

    @Query("SELECT count(RecordType.id) FROM RecordType")
    fun getRecordTypeCount(): Long

    @Query("SELECT * FROM RecordType WHERE state = 0 AND type = :type ORDER BY ranking")
    fun getRecordTypes(type: Int): LiveData<List<RecordType>>

    @Query("SELECT * FROM RecordType WHERE type = :type AND name = :name")
    fun getTypeByName(type: Int, name: String): RecordType?

    @Insert
    fun insertRecordTypes(vararg recordTypes: RecordType)

    @Update
    fun updateRecordTypes(vararg recordTypes: RecordType)

    @Delete
    fun deleteRecordType(recordType: RecordType)
}
