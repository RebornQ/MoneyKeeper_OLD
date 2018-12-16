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
import android.arch.persistence.room.*
import me.bakumon.moneykeeperclone.database.entity.AssetsTransferRecord
import me.bakumon.moneykeeperclone.database.entity.AssetsTransferRecordWithAssets

/**
 * AssetsTransferRecordDao
 *
 * @author Bakumon https://bakumon.me
 */
@Dao
interface AssetsTransferRecordDao {

    @Query("SELECT transfer_record.*, assets_from.name AS assetsNameFrom, assets_to.name AS assetsNameTo FROM Assets AS assets_from, Assets AS assets_to, AssetsTransferRecord AS transfer_record WHERE assets_from.id = transfer_record.assets_id_form AND assets_to.id = transfer_record.assets_id_to AND (transfer_record.assets_id_form=:id OR transfer_record.assets_id_to=:id) ORDER BY transfer_record.time DESC, transfer_record.create_time DESC")
    fun getTransferRecordsById(id: Int): LiveData<List<AssetsTransferRecordWithAssets>>

    @Insert
    fun insertTransferRecord(vararg assetsTransferRecord: AssetsTransferRecord)

    @Update
    fun updateTransferRecord(assetsTransferRecord: AssetsTransferRecord)

    @Delete
    fun deleteTransferRecord(assetsTransferRecord: AssetsTransferRecord)
}
