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

package me.bakumon.moneykeeperclone.ui.setting

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.snatik.storage.Storage
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.base.EmptyResource
import me.bakumon.moneykeeperclone.base.Resource
import me.bakumon.moneykeeperclone.datasource.AppDataSource
import me.bakumon.moneykeeperclone.ui.common.BaseViewModel
import me.bakumon.moneykeeperclone.utill.BackupUtil
import java.io.File

/**
 * 设置 ViewModel
 *
 * @author Bakumon https://bakumon.me
 */
class SettingViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    fun backupFiles(): LiveData<Resource<List<BackupBean>>> {
        val liveData = MutableLiveData<Resource<List<BackupBean>>>()
        Flowable.create(FlowableOnSubscribe<List<BackupBean>> { e ->
            e.onNext(BackupUtil.getBackupFiles())
            e.onComplete()
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveData.value = Resource.create(it)
                })
                { throwable ->
                    liveData.value = Resource.create(throwable)
                }
        return liveData
    }

    fun backupDB(): LiveData<Resource<Boolean>> {
        val completable = Completable.create { e ->
            val result = BackupUtil.userBackup()
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

    fun restoreToDB(restoreFile: String): MutableLiveData<Resource<Boolean>> {
        val resultLiveData = MutableLiveData<Resource<Boolean>>()
        val storage = Storage(App.instance)
        val beforeRestorePath = storage.internalFilesDirectory + File.separator + BACKUP_FILE_BEFORE_RESTORE
        mDisposable.add(Flowable.create(FlowableOnSubscribe<Boolean> {
            // 先把恢复前的 db 文件备份到内部 file 文件夹下
            val backupResult = BackupUtil.backupDB(beforeRestorePath)
            if (!backupResult) {
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

    companion object {
        const val BACKUP_FILE_BEFORE_RESTORE = "before_restore_local.db"
    }
}
