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

package me.bakumon.moneykeeperclone.ui.setting.backup

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.snatik.storage.Storage
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.api.ApiResponse
import me.bakumon.moneykeeperclone.api.Network
import me.bakumon.moneykeeperclone.base.EmptyResource
import me.bakumon.moneykeeperclone.base.Resource
import me.bakumon.moneykeeperclone.datasource.AppDataSource
import me.bakumon.moneykeeperclone.ui.common.BaseViewModel
import me.bakumon.moneykeeperclone.utill.BackupUtil
import me.bakumon.moneykeeperclone.utill.EncryptUtil
import okhttp3.ResponseBody
import java.io.File

/**
 * 云备份 ViewModel
 *
 * @author Bakumon https://bakumon.me
 */
class BackupViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    fun savePsw(input: String): MutableLiveData<Resource<Boolean>> {
        val resultLiveData = MutableLiveData<Resource<Boolean>>()
        val key = EncryptUtil.key
        val salt = EncryptUtil.salt

        mDisposable.add(Flowable.create(FlowableOnSubscribe<Boolean> {
            val result = if (input.isEmpty()) {
                ConfigManager.setWebDavEncryptPsw("")
            } else {
                ConfigManager.setWebDavEncryptPsw(EncryptUtil.encrypt(input, key, salt))
            }
            it.onNext(result)
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    resultLiveData.value = Resource.create(it)
                })
                { throwable ->
                    resultLiveData.value = Resource.create(throwable)
                }
        )
        return resultLiveData
    }

    fun createDirLiveData(): LiveData<ApiResponse<ResponseBody>> {
        return Network.davService().createDirLiveData(BackupConstant.BACKUP_DIR)
    }

    fun getListLiveData(): LiveData<ApiResponse<ResponseBody>> {
        return Network.davService().listLiveData(BackupConstant.BACKUP_DIR)
    }

    fun restore(): LiveData<ApiResponse<ResponseBody>> {
        return Network.davService().download(BackupConstant.BACKUP_FILE)
    }

    fun restoreToDB(body: ResponseBody): MutableLiveData<Resource<Boolean>> {
        val resultLiveData = MutableLiveData<Resource<Boolean>>()
        val storage = Storage(App.instance)
        // 先把恢复前的 db 文件备份到内部 file 文件夹下
        val beforeRestorePath = storage.internalFilesDirectory + File.separator + BackupConstant.BACKUP_FILE_BEFORE_RESTORE
        mDisposable.add(Flowable.create(FlowableOnSubscribe<Boolean> {
            val backupResult = BackupUtil.backupDB(beforeRestorePath)
            if (!backupResult) {
                it.onNext(false)
            } else {
                val restoreFile = storage.internalCacheDirectory + File.separator + BackupConstant.BACKUP_FILE_TEMP
                // 保存下载的 db 文件到内部 cache 文件夹
                val result = storage.createFile(restoreFile, body.bytes())
                if (!result) {
                    it.onNext(false)
                } else {
                    // 恢复到数据库文件夹
                    val restoreDB = BackupUtil.restoreDB(restoreFile)
                    if (!restoreDB) {
                        it.onNext(false)
                    } else {
                        // 检查 db 文件是否完好
                        mDataSource.getRecordTypeCount()
                        it.onNext(true)
                    }
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    resultLiveData.value = Resource.create(it)
                })
                { throwable ->
                    // 说明 db 文件损坏
                    val result = BackupUtil.restoreDB(beforeRestorePath)
                    if (result) {
                        resultLiveData.value = EmptyResource()
                    } else {
                        resultLiveData.value = Resource.create(throwable)
                    }
                }
        )
        return resultLiveData
    }
}
