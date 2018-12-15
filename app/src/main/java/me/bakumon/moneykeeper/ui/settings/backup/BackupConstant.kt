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
package me.bakumon.moneykeeper.ui.settings.backup

import me.bakumon.moneykeeper.BuildConfig

/**
 * 云备份常量
 * @author Bakumon https://bakumon.me
 */
object BackupConstant {
    val BACKUP_DIR = if (BuildConfig.DEBUG) "MoneyKeeper_Debug" else "MoneyKeeper"
    private val BACKUP_FILE_NAME = if (BuildConfig.DEBUG) "MoneyKeeperCloudBackup_Debug.db" else "MoneyKeeperCloudBackup.db"
    val BACKUP_FILE = "$BACKUP_DIR/$BACKUP_FILE_NAME"
    const val BACKUP_FILE_TEMP = "backup_temp_cloud.db"
    const val BACKUP_FILE_BEFORE_RESTORE = "before_restore_cloud.db"
}
