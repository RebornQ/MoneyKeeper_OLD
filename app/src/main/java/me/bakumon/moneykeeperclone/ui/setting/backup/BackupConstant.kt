package me.bakumon.moneykeeperclone.ui.setting.backup

import me.bakumon.moneykeeperclone.BuildConfig

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
