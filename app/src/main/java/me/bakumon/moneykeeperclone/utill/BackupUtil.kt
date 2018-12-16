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

package me.bakumon.moneykeeperclone.utill

import com.snatik.storage.Storage
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.BuildConfig
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.database.AppDatabase
import me.bakumon.moneykeeperclone.ui.setting.BackupBean
import org.ocpsoft.prettytime.PrettyTime
import java.io.File
import java.util.*

/**
 * 备份相关工具类
 *
 * @author Bakumon https:/bakumon.me
 */
object BackupUtil {
    private val BACKUP_DIR = if (BuildConfig.DEBUG) "backup_moneykeeperclone_debug" else "backup_moneykeeperclone"
    private val AUTO_BACKUP_PREFIX = if (BuildConfig.DEBUG) "MoneyKeeperCloneBackupAutoDebug" else "MoneyKeeperCloneBackupAuto"
    private val USER_BACKUP_PREFIX = if (BuildConfig.DEBUG) "MoneyKeeperCloneBackupUserDebug" else "MoneyKeeperCloneBackupUser"
    private const val SUFFIX = ".db"

    val backupFolder
        get() = getRootPath()

    val userBackupPath
        get() = getRootPath() + File.separator + USER_BACKUP_PREFIX + SUFFIX

    fun moveAllBackupFile(newFolder: String): Boolean {
        val storage = Storage(App.instance)
        if (!storage.isDirectoryExists(newFolder)) {
            val createResult = storage.createDirectory(newFolder)
            if (!createResult) {
                return false
            }
        }
        val oldFolder = BackupUtil.backupFolder
        val dbFiles = storage.getFiles(oldFolder, "[\\S]*\\.db")
        for (file in dbFiles) {
            val moveResult = storage.move(file.absolutePath, newFolder + File.separator + file.name)
            if (!moveResult) {
                return false
            }
        }
        // 如果旧备份文件夹为空，删除
        if (storage.getNestedFiles(oldFolder).isEmpty()) {
            storage.deleteDirectory(oldFolder)
        }
        return true
    }

    private fun getRootPath(): String {
        val storage = Storage(App.instance)
        val backupPath = ConfigManager.backupFolder
        return if (backupPath.isEmpty()) {
            storage.externalStorageDirectory + File.separator + BACKUP_DIR
        } else {
            backupPath
        }
    }

    fun getBackupFiles(): List<BackupBean> {
        val storage = Storage(App.instance)
        val dir = getRootPath()
        val backupBeans = ArrayList<BackupBean>()
        var bean: BackupBean
        val files = storage.getFiles(dir, "[\\S]*\\.db") ?: return backupBeans
        var fileTemp: File
        val prettyTime = PrettyTime()
        for (i in files.indices) {
            fileTemp = files[i]
            bean = BackupBean(
                    fileTemp,
                    fileTemp.name,
                    storage.getReadableSize(fileTemp),
                    prettyTime.format(Date(fileTemp.lastModified()))
            )
            backupBeans.add(bean)
        }
        return backupBeans
    }

    private fun backupDBToSDCard(fileName: String): Boolean {
        val storage = Storage(App.instance)
        val isWritable = Storage.isExternalWritable()
        if (!isWritable) {
            return false
        }
        val path = getRootPath()
        if (!storage.isDirectoryExists(path)) {
            storage.createDirectory(path)
        }
        val filePath = path + File.separator + fileName
        if (!storage.isFileExist(filePath)) {
            // 创建空文件，在模拟器上测试，如果没有这个文件，复制的时候会报 FileNotFound
            storage.createFile(filePath, "")
        }
        return storage.copy(App.instance.getDatabasePath(AppDatabase.DB_NAME)?.path, path + File.separator + fileName)
    }

    fun autoBackup(): Boolean {
        val fileName = AUTO_BACKUP_PREFIX + SUFFIX
        return backupDBToSDCard(fileName)
    }

    fun userBackup(): Boolean {
        val fileName = USER_BACKUP_PREFIX + SUFFIX
        return backupDBToSDCard(fileName)
    }

    fun restoreDB(restoreFile: String): Boolean {
        val storage = Storage(App.instance)
        if (storage.isFileExist(restoreFile)) {
            return storage.copy(restoreFile, App.instance.getDatabasePath(AppDatabase.DB_NAME)?.path)
        }
        return false
    }

    fun backupDB(backupPath: String): Boolean {
        val storage = Storage(App.instance)
        if (!storage.isFileExist(backupPath)) {
            // 创建空文件，在模拟器上测试，如果没有这个文件，复制的时候会报 FileNotFound
            storage.createFile(backupPath, "")
        }
        return storage.copy(App.instance.getDatabasePath(AppDatabase.DB_NAME)?.path, backupPath)
    }
}
