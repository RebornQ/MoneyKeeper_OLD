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

package me.bakumon.moneykeeperclone.ui.assets.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.bakumon.moneykeeperclone.base.Resource
import me.bakumon.moneykeeperclone.database.entity.RecordWithType
import me.bakumon.moneykeeperclone.datasource.AppDataSource
import me.bakumon.moneykeeperclone.ui.common.BaseViewModel

/**
 * OrderListViewModel
 *
 * @author Bakumon https://bakumon.me
 */
class OrderListViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {
    fun getRecordWithTypesByAssetsId(id: Int): LiveData<List<RecordWithType>> {
        return mDataSource.getRecordWithTypesByAssetsId(id, 100)
    }

    fun deleteRecord(record: RecordWithType): LiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()
        mDisposable.add(mDataSource.deleteRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveData.value = Resource.create(true)
                }
                ) { throwable ->
                    liveData.value = Resource.create(throwable)
                })
        return liveData
    }
}
