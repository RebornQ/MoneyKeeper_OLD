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
import me.bakumon.moneykeeper.database.entity.Assets
import me.bakumon.moneykeeper.database.entity.AssetsMoneyBean

/**
 * AssetsDao
 *
 * @author Bakumon https://bakumon.me
 */
@Dao
interface AssetsDao {

    @Query("SELECT * FROM Assets WHERE state=0 ORDER BY ranking, create_time DESC")
    fun getAllAssets(): LiveData<List<Assets>>

    @Query("SELECT * FROM Assets WHERE state=0 AND id=:id")
    fun getAssetsById(id: Int): LiveData<Assets>

    @Query("SELECT * FROM Assets WHERE state=0 AND id=:id")
    fun getAssetsBeanById(id: Int): Assets?

    @Insert
    fun insertAssets(vararg assets: Assets)

    @Update
    fun updateAssets(vararg assets: Assets)

    @Delete
    fun deleteAssets(assets: Assets)

    @Query("select sum(Assets.money) as netAssets,sum(case when Assets.money>0 then Assets.money else 0 end) as allAssets,sum(case when Assets.money<0 then Assets.money else 0 end) as liabilityAssets from Assets WHERE Assets.state=0")
    fun getAssetsMoney(): LiveData<AssetsMoneyBean>
}
