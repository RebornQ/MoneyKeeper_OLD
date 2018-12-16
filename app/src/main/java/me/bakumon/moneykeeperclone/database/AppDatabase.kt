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

package me.bakumon.moneykeeperclone.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.database.converters.Converters
import me.bakumon.moneykeeperclone.database.dao.*
import me.bakumon.moneykeeperclone.database.entity.*


/**
 * 数据库
 *
 * @author Bakumon https:bakumon.me
 */
@Database(entities = [Record::class, RecordType::class, Assets::class, AssetsModifyRecord::class, AssetsTransferRecord::class, Label::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * 获取记账类型操作类
     *
     * @return RecordTypeDao 记账类型操作类
     */
    abstract fun recordTypeDao(): RecordTypeDao

    /**
     * 获取记账操作类
     *
     * @return RecordDao 记账操作类
     */
    abstract fun recordDao(): RecordDao

    /**
     * 资产
     */
    abstract fun assetsDao(): AssetsDao

    /**
     * 资产余额调整记录
     */
    abstract fun assetsModifyRecordDao(): AssetsModifyRecordDao

    /**
     * 资产转账记录
     */
    abstract fun assetsTransferRecordDao(): AssetsTransferRecordDao

    /**
     * 资产转账记录
     */
    abstract fun labelDao(): LabelDao

    companion object {
        const val DB_NAME = "MoneyKeeper.db"
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Record 表中添加字段 assets_id
                database.execSQL("ALTER TABLE `Record` ADD COLUMN `assets_id` INTEGER")

                database.execSQL("CREATE TABLE IF NOT EXISTS `Assets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `img_name` TEXT NOT NULL, `type` INTEGER NOT NULL, `state` INTEGER NOT NULL, `remark` TEXT NOT NULL, `create_time` INTEGER NOT NULL, `money` INTEGER NOT NULL, `init_money` INTEGER NOT NULL)")

                database.execSQL("CREATE TABLE IF NOT EXISTS `AssetsModifyRecord` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `state` INTEGER NOT NULL, `create_time` INTEGER NOT NULL, `assets_id` INTEGER NOT NULL, `money_before` INTEGER NOT NULL, `money` INTEGER NOT NULL, FOREIGN KEY(`assets_id`) REFERENCES `Assets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                database.execSQL("CREATE  INDEX `index_AssetsModifyRecord_assets_id_create_time` ON `AssetsModifyRecord` (`assets_id`, `create_time`)")

                database.execSQL("CREATE TABLE IF NOT EXISTS `AssetsTransferRecord` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `state` INTEGER NOT NULL, `create_time` INTEGER NOT NULL, `time` INTEGER NOT NULL, `assets_id_form` INTEGER NOT NULL, `assets_id_to` INTEGER NOT NULL, `remark` TEXT NOT NULL, `money` INTEGER NOT NULL, FOREIGN KEY(`assets_id_form`) REFERENCES `Assets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`assets_id_to`) REFERENCES `Assets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                database.execSQL("CREATE  INDEX `index_AssetsTransferRecord_assets_id_form_assets_id_to` ON `AssetsTransferRecord` (`assets_id_form`, `assets_id_to`)")
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Assets 表中添加字段 ranking
                database.execSQL("ALTER TABLE `Assets` ADD COLUMN `ranking` INTEGER")
            }
        }

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 增加标签表
                database.execSQL("CREATE TABLE IF NOT EXISTS `Label` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `state` INTEGER NOT NULL, `create_time` INTEGER NOT NULL, `ranking` INTEGER)")
                database.execSQL("CREATE UNIQUE INDEX `index_Label_name` ON `Label` (`name`)")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null
        val instance: AppDatabase?
            get() {
                if (INSTANCE == null) {
                    synchronized(AppDatabase::class) {
                        if (INSTANCE == null) {
                            INSTANCE = Room.databaseBuilder(App.instance, AppDatabase::class.java, DB_NAME)
                                    .setJournalMode(JournalMode.TRUNCATE)
                                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                                    .build()
                        }
                    }
                }
                return INSTANCE
            }
    }
}
