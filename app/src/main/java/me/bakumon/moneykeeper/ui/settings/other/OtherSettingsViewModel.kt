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

package me.bakumon.moneykeeper.ui.settings.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.bakumon.moneykeeper.base.Resource
import me.bakumon.moneykeeper.datasource.AppDataSource
import me.bakumon.moneykeeper.ui.common.BaseViewModel
import me.bakumon.moneykeeper.utill.BackupUtil

/**
 *
 * @author Bakumon https://bakumon.me
 */
class OtherSettingsViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    fun move(newFolder: String): LiveData<Resource<Boolean>> {
        val completable = Completable.create { e ->
            val result = BackupUtil.moveAllBackupFile(newFolder)
            if (result) {
                e.onComplete()
            } else {
                e.onError(Exception())
            }
        }
        val liveData = MutableLiveData<Resource<Boolean>>()
        mDisposable.add(completable
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
