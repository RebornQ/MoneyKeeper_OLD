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

package me.bakumon.moneykeeperclone.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import me.bakumon.moneykeeperclone.database.entity.Label

/**
 * LabelDao
 *
 * @author Bakumon https://bakumon.me
 */
@Dao
interface LabelDao {

    @Query("SELECT * FROM Label WHERE state=0 ORDER BY create_time DESC LIMIT 15")
    fun getLabels(): LiveData<List<Label>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLabel(vararg label: Label)
}
