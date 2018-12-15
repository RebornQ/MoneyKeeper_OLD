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

package me.bakumon.moneykeeper.ui.addtype

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.bakumon.moneykeeper.base.Resource
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.datasource.AppDataSource
import me.bakumon.moneykeeper.ui.common.BaseViewModel

/**
 * 添加记账类型 ViewModel
 *
 * @author Bakumon https://bakumon.me
 */
class AddTypeViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    fun getAllTypeImgBeans(type: Int): LiveData<List<TypeImgBean>> {
        val liveData = MutableLiveData<List<TypeImgBean>>()
        liveData.value = mDataSource.getAllTypeImgBeans(type)
        return liveData
    }

    /**
     * 保存记账类型，包括新增和更新
     *
     * @param recordType 修改时传
     * @param type       类型
     * @param imgName    图片
     * @param name       类型名称
     */
    fun saveRecordType(recordType: RecordType?, type: Int, imgName: String, name: String): LiveData<Resource<Boolean>> {
        val completable = if (recordType == null) {
            // 添加
            mDataSource.addRecordType(type, imgName, name)
        } else {
            // 修改
            val updateType = RecordType(recordType.id, name, imgName, recordType.type, recordType.ranking)
            updateType.state = recordType.state
            mDataSource.updateRecordType(recordType, updateType)
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
