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

package me.bakumon.moneykeeper.datasource

import android.text.TextUtils
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import me.bakumon.moneykeeper.App
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.AppDatabase
import me.bakumon.moneykeeper.database.entity.*
import me.bakumon.moneykeeper.ui.addtype.TypeImgBean
import me.bakumon.moneykeeper.utill.BackupUtil
import me.bakumon.moneykeeper.utill.DateUtils
import me.bakumon.moneykeeper.utill.ToastUtils
import java.math.BigDecimal
import java.util.*

/**
 * 数据源本地实现类
 *
 * @author Bakumon https://bakumon.me
 */
class LocalAppDataSource(private val mAppDatabase: AppDatabase) : AppDataSource {

    /**
     * 自动备份
     */
    @Throws(Exception::class)
    private fun autoBackup() {
        if (DefaultSPHelper.isLocalAutoBackup) {
            val isSuccess = BackupUtil.autoBackup()
            if (!isSuccess) {
                ToastUtils.show(R.string.toast_is_not_writable_external)
            }
        }
    }

    override fun initRecordTypes(): Completable {
        return Completable.fromAction {
            if (mAppDatabase.recordTypeDao().getRecordTypeCount() < 1) {
                // 没有记账类型数据记录，插入默认的数据类型
                mAppDatabase.recordTypeDao().insertRecordTypes(*RecordTypeInitCreator.createRecordTypeData())
            }
        }
    }

    override fun getRecordTypeCount(): Long {
        return mAppDatabase.recordTypeDao().getRecordTypeCount()
    }

    override fun addRecordType(type: Int, imgName: String, name: String): Completable {
        return Completable.fromAction {
            val recordType = mAppDatabase.recordTypeDao().getTypeByName(type, name)
            if (recordType != null) {
                // name 类型存在
                if (recordType.state == RecordType.STATE_DELETED) {
                    // 已删除状态
                    recordType.state = RecordType.STATE_NORMAL
                    recordType.ranking = System.currentTimeMillis()
                    recordType.imgName = imgName
                    mAppDatabase.recordTypeDao().updateRecordTypes(recordType)
                } else {
                    // 提示用户该类型已经存在
                    throw IllegalStateException(App.instance.getString(R.string.toast_type_is_exist, name))
                }
            } else {
                // 不存在，直接新增
                val insertType = RecordType()
                insertType.name = name
                insertType.imgName = imgName
                insertType.type = type
                insertType.ranking = System.currentTimeMillis()
                mAppDatabase.recordTypeDao().insertRecordTypes(insertType)
            }
            autoBackup()
        }
    }

    override fun updateRecordType(oldRecordType: RecordType, recordType: RecordType): Completable {
        return Completable.fromAction {
            val oldName = oldRecordType.name
            val oldImgName = oldRecordType.imgName
            if (!TextUtils.equals(oldName, recordType.name)) {
                val recordTypeFromDb = mAppDatabase.recordTypeDao().getTypeByName(recordType.type, recordType.name!!)
                if (recordTypeFromDb != null) {
                    if (recordTypeFromDb.state == RecordType.STATE_DELETED) {

                        // 1。recordTypeFromDb 改成正常状态，name改成recordType#name，imageName同理
                        // 2。更新 recordTypeFromDb
                        // 3。判断是否有 oldRecordType 类型的 record 记录
                        // 4。如果有记录，把这些记录的 type_id 改成 recordTypeFromDb.id
                        // 5。删除 oldRecordType 记录

                        recordTypeFromDb.state = RecordType.STATE_NORMAL
                        recordTypeFromDb.name = recordType.name
                        recordTypeFromDb.imgName = recordType.imgName
                        recordTypeFromDb.ranking = System.currentTimeMillis()

                        mAppDatabase.recordTypeDao().updateRecordTypes(recordTypeFromDb)

                        val recordsWithOldType = mAppDatabase.recordDao().getRecordsWithTypeId(oldRecordType.id)
                        if (recordsWithOldType != null && recordsWithOldType.isNotEmpty()) {
                            for (record in recordsWithOldType) {
                                record.recordTypeId = recordTypeFromDb.id
                            }
                            mAppDatabase.recordDao().updateRecords(*recordsWithOldType.toTypedArray())
                        }

                        mAppDatabase.recordTypeDao().deleteRecordType(oldRecordType)
                    } else {
                        // 提示用户该类型已经存在
                        throw IllegalStateException(
                            App.instance.getString(
                                R.string.toast_type_is_exist,
                                recordType.name
                            )
                        )
                    }
                } else {
                    mAppDatabase.recordTypeDao().updateRecordTypes(recordType)
                }
            } else if (!TextUtils.equals(oldImgName, recordType.imgName)) {
                mAppDatabase.recordTypeDao().updateRecordTypes(recordType)
            }
            autoBackup()
        }
    }

    override fun deleteRecordType(recordType: RecordType): Completable {
        return Completable.fromAction {
            if (mAppDatabase.recordDao().getRecordCountWithTypeId(recordType.id) > 0) {
                recordType.state = RecordType.STATE_DELETED
                mAppDatabase.recordTypeDao().updateRecordTypes(recordType)
            } else {
                mAppDatabase.recordTypeDao().deleteRecordType(recordType)
            }
            autoBackup()
        }
    }

    override fun getAllRecordType(): LiveData<List<RecordType>> {
        return mAppDatabase.recordTypeDao().getAllRecordTypes()
    }

    override fun getAllRecordTypeWithAsset(): LiveData<List<RecordTypeWithAsset>> {
        return mAppDatabase.recordTypeDao().getAllRecordTypesWithAsset()
    }

    override fun getRecordTypes(type: Int): LiveData<List<RecordType>> {
        return mAppDatabase.recordTypeDao().getRecordTypes(type)
    }

    override fun sortRecordTypes(recordTypes: List<RecordType>): Completable {
        return Completable.fromAction {
            if (recordTypes.size > 1) {
                val sortTypes = ArrayList<RecordType>()
                for (i in recordTypes.indices) {
                    val type = recordTypes[i]
                    if (type.ranking != i.toLong()) {
                        type.ranking = i.toLong()
                        sortTypes.add(type)
                    }
                }
                mAppDatabase.recordTypeDao().updateRecordTypes(*sortTypes.toTypedArray())
                autoBackup()
            }
        }
    }

    override fun sortAssets(assets: List<Assets>): Completable {
        return Completable.fromAction {
            if (assets.size > 1) {
                val sortAssets = ArrayList<Assets>()
                for (i in assets.indices) {
                    val type = assets[i]
                    if (type.ranking != i) {
                        type.ranking = i
                        sortAssets.add(type)
                    }
                }
                mAppDatabase.assetsDao().updateAssets(*sortAssets.toTypedArray())
                autoBackup()
            }
        }
    }

    override fun getAllTypeImgBeans(type: Int): List<TypeImgBean> {
        return TypeImgListCreator.createTypeImgBeanData(type)
    }

    override fun insertRecord(recordType: RecordType, assets: Assets?, record: Record): Completable {
        return Completable.fromAction {
            mAppDatabase.recordDao().insertRecord(record)
            if (assets != null) {
                if (recordType.type == RecordType.TYPE_OUTLAY) {
                    assets.money = assets.money.subtract(record.money)
                } else {
                    assets.money = assets.money.add(record.money)
                }
                mAppDatabase.assetsDao().updateAssets(assets)
            }
            // 保存常用备注
            if (!TextUtils.isEmpty(record.remark)) {
                mAppDatabase.labelDao().insertLabel(Label(record.remark!!))
            }
            // 更新分类类型对应的资产
            if (assets != null) {
                recordType.assetsId = assets.id
                mAppDatabase.recordTypeDao().updateRecordTypes(recordType)
            }
            autoBackup()
        }
    }

    override fun updateRecord(
        oldMoney: BigDecimal,
        oldType: Int,
        type: Int,
        oldAssets: Assets?,
        assets: Assets?,
        record: Record,
        recordType: RecordType
    ): Completable {
        return Completable.fromAction {
            mAppDatabase.recordDao().updateRecords(record)
            // 太灾难了
            if (oldType == type) {
                if (oldAssets == null) {
                    if (assets == null) {
                        // 不用更新资产

                    } else {
                        if (type == RecordType.TYPE_OUTLAY) {
                            // 更新 assets，减
                            assets.money = assets.money.subtract(record.money)
                            mAppDatabase.assetsDao().updateAssets(assets)
                        } else {
                            assets.money = assets.money.add(record.money)
                            mAppDatabase.assetsDao().updateAssets(assets)
                        }
                    }
                } else {
                    if (assets == null) {
                        if (type == RecordType.TYPE_OUTLAY) {
                            // 更新 oldAssets，加
                            oldAssets.money = oldAssets.money.add(oldMoney)
                            mAppDatabase.assetsDao().updateAssets(oldAssets)
                        } else {
                            oldAssets.money = oldAssets.money.subtract(oldMoney)
                            mAppDatabase.assetsDao().updateAssets(oldAssets)
                        }
                    } else {
                        if (type == RecordType.TYPE_OUTLAY) {
                            oldAssets.money = oldAssets.money.add(oldMoney)
                            assets.money = assets.money.subtract(record.money)
                            mAppDatabase.assetsDao().updateAssets(oldAssets)
                            mAppDatabase.assetsDao().updateAssets(assets)
                        } else {
                            oldAssets.money = oldAssets.money.subtract(oldMoney)
                            assets.money = assets.money.add(record.money)
                            mAppDatabase.assetsDao().updateAssets(oldAssets)
                            mAppDatabase.assetsDao().updateAssets(assets)
                        }
                    }
                }
            } else {
                if (oldAssets == null) {
                    if (assets == null) {
                        // 不用更新资产
                    } else {
                        if (type == RecordType.TYPE_OUTLAY) {
                            assets.money = assets.money.subtract(record.money)
                            mAppDatabase.assetsDao().updateAssets(assets)
                        } else {
                            assets.money = assets.money.add(record.money)
                            mAppDatabase.assetsDao().updateAssets(assets)
                        }
                    }
                } else {
                    if (assets == null) {
                        if (oldType == RecordType.TYPE_OUTLAY) {
                            oldAssets.money = oldAssets.money.add(oldMoney)
                            mAppDatabase.assetsDao().updateAssets(oldAssets)
                        } else {
                            oldAssets.money = oldAssets.money.subtract(oldMoney)
                            mAppDatabase.assetsDao().updateAssets(oldAssets)
                        }
                    } else {
                        if (type == RecordType.TYPE_OUTLAY) {
                            // oldType==RecordType.TYPE_INCOME
                            oldAssets.money = oldAssets.money.subtract(oldMoney)
                            assets.money = assets.money.subtract(record.money)
                            mAppDatabase.assetsDao().updateAssets(oldAssets)
                            mAppDatabase.assetsDao().updateAssets(assets)
                        } else {
                            oldAssets.money = oldAssets.money.add(oldMoney)
                            assets.money = assets.money.add(record.money)
                            mAppDatabase.assetsDao().updateAssets(oldAssets)
                            mAppDatabase.assetsDao().updateAssets(assets)
                        }
                    }
                }
            }
            // 保存常用备注
            if (!TextUtils.isEmpty(record.remark)) {
                mAppDatabase.labelDao().insertLabel(Label(record.remark!!))
            }
            // 更新分类类型对应的资产
            if (assets != null) {
                recordType.assetsId = assets.id
                mAppDatabase.recordTypeDao().updateRecordTypes(recordType)
            }
            autoBackup()
        }
    }

    override fun deleteRecord(record: RecordForList): Completable {
        return Completable.fromAction {
            mAppDatabase.recordDao().deleteRecord(record)
            val assets = mAppDatabase.assetsDao().getAssetsBeanById(record.assetsId!!)
            if (assets != null) {
                if (record.type == RecordType.TYPE_OUTLAY) {
                    assets.money = assets.money.add(record.money)
                } else {
                    assets.money = assets.money.subtract(record.money)
                }
                mAppDatabase.assetsDao().updateAssets(assets)
            }
            autoBackup()
        }
    }

    override fun getRecentRecords(): LiveData<List<RecordForList>> {
        return mAppDatabase.recordDao().getRecordsForList(100)
    }

    override fun getRecordForListWithTypesByAssetsId(assetsId: Int, limit: Int): LiveData<List<RecordForList>> {
        return mAppDatabase.recordDao().getRecordForListWithTypesByAssetsId(assetsId, limit)
    }

    override fun getRecordForListWithTypes(dateFrom: Date, dateTo: Date, type: Int): LiveData<List<RecordForList>> {
        return mAppDatabase.recordDao().getRangeRecordForListWithTypes(dateFrom, dateTo, type)
    }

    override fun getRangeRecordForListWithTypesByTypeId(
        dateFrom: Date,
        dateTo: Date,
        type: Int,
        typeId: Int
    ): LiveData<List<RecordForList>> {
        return mAppDatabase.recordDao().getRangeRecordForListWithTypesByTypeId(dateFrom, dateTo, type, typeId)
    }

    override fun getRecordForListWithTypesSortMoney(
        dateFrom: Date,
        dateTo: Date,
        type: Int,
        typeId: Int
    ): LiveData<List<RecordForList>> {
        return mAppDatabase.recordDao().getRecordForListWithTypesSortMoney(dateFrom, dateTo, type, typeId)
    }

    override fun getCurrentMonthSumMoneyLiveData(): LiveData<List<SumMoneyBean>> {
        val dateFrom = DateUtils.getCurrentMonthStart()
        val dateTo = DateUtils.getCurrentMonthEnd()
        return mAppDatabase.recordDao().getSumMoneyLiveData(dateFrom, dateTo)
    }

    override fun getMonthSumMoneyLiveData(dateFrom: Date, dateTo: Date): LiveData<List<SumMoneyBean>> {
        return mAppDatabase.recordDao().getSumMoneyLiveData(dateFrom, dateTo)
    }

    override fun getDaySumMoney(dateFrom: Date, dateTo: Date, type: Int): LiveData<List<DaySumMoneyBean>> {
        return mAppDatabase.recordDao().getDaySumMoney(dateFrom, dateTo, type)
    }

    override fun getTypeSumMoney(from: Date, to: Date, type: Int): LiveData<List<TypeSumMoneyBean>> {
        return mAppDatabase.recordDao().getTypeSumMoney(from, to, type)
    }

    override fun getMonthOfYearSumMoney(from: Date, to: Date): LiveData<List<MonthSumMoneyBean>> {
        return mAppDatabase.recordDao().getMonthOfYearSumMoney(from, to)
    }

    override fun getTodayOutlay(): List<DaySumMoneyBean> {
        val dateFrom = DateUtils.getTodayStart()
        val dateTo = DateUtils.getTodayEnd()
        return mAppDatabase.recordDao().getDaySumMoneyData(dateFrom, dateTo, RecordType.TYPE_OUTLAY)
    }

    override fun getCurrentOutlay(): List<SumMoneyBean> {
        val dateFrom = DateUtils.getCurrentMonthStart()
        val dateTo = DateUtils.getCurrentMonthEnd()
        return mAppDatabase.recordDao().getSumMoney(dateFrom, dateTo)
    }

    override fun addAssets(assets: Assets): Completable {
        return Completable.fromAction {
            mAppDatabase.assetsDao().insertAssets(assets)
            autoBackup()
        }
    }

    override fun updateAssets(assets: Assets): Completable {
        return Completable.fromAction {
            mAppDatabase.assetsDao().updateAssets(assets)
            autoBackup()
        }
    }

    override fun deleteAssets(assets: Assets): Completable {
        return Completable.fromAction {
            mAppDatabase.assetsDao().deleteAssets(assets)
            autoBackup()
        }
    }

    override fun getAssets(): LiveData<List<Assets>> {
        return mAppDatabase.assetsDao().getAllAssets()
    }

    override fun getAssetsById(id: Int): LiveData<Assets> {
        return mAppDatabase.assetsDao().getAssetsById(id)
    }

    override fun getAssetsBeanById(id: Int): Assets? {
        return mAppDatabase.assetsDao().getAssetsBeanById(id)
    }

    override fun getAssetsMoney(): LiveData<AssetsMoneyBean> {
        return mAppDatabase.assetsDao().getAssetsMoney()
    }

    override fun insertAssetsRecord(assetsModifyRecord: AssetsModifyRecord): Completable {
        return Completable.fromAction {
            mAppDatabase.assetsModifyRecordDao().insertAssetsRecord(assetsModifyRecord)
            autoBackup()
        }
    }

    override fun getAssetsRecordsById(id: Int): LiveData<List<AssetsModifyRecord>> {
        return mAppDatabase.assetsModifyRecordDao().getAssetsRecordsById(id)
    }

    override fun insertTransferRecord(
        outAssets: Assets,
        inAssets: Assets,
        transferRecord: AssetsTransferRecord
    ): Completable {
        return Completable.fromAction {
            mAppDatabase.assetsTransferRecordDao().insertTransferRecord(transferRecord)
            outAssets.money = outAssets.money.subtract(transferRecord.money)
            mAppDatabase.assetsDao().updateAssets(outAssets)
            inAssets.money = inAssets.money.add(transferRecord.money)
            mAppDatabase.assetsDao().updateAssets(inAssets)
            // 保存常用备注
            if (!TextUtils.isEmpty(transferRecord.remark)) {
                mAppDatabase.labelDao().insertLabel(Label(transferRecord.remark))
            }
            autoBackup()
        }
    }

    override fun updateTransferRecord(
        oldMoney: BigDecimal,
        oldOutAssets: Assets,
        oldInAssets: Assets,
        outAssets: Assets,
        inAssets: Assets,
        transferRecord: AssetsTransferRecord
    ): Completable {
        return Completable.fromAction {
            mAppDatabase.assetsTransferRecordDao().updateTransferRecord(transferRecord)

            oldOutAssets.money = oldOutAssets.money.add(oldMoney)
            mAppDatabase.assetsDao().updateAssets(oldOutAssets)
            if (oldOutAssets.id == outAssets.id) {
                outAssets.money = oldOutAssets.money
            }
            if (oldOutAssets.id == inAssets.id) {
                inAssets.money = oldOutAssets.money
            }

            oldInAssets.money = oldInAssets.money.subtract(oldMoney)
            mAppDatabase.assetsDao().updateAssets(oldInAssets)
            if (oldInAssets.id == outAssets.id) {
                outAssets.money = oldInAssets.money
            }
            if (oldInAssets.id == inAssets.id) {
                inAssets.money = oldInAssets.money
            }

            outAssets.money = outAssets.money.subtract(transferRecord.money)
            mAppDatabase.assetsDao().updateAssets(outAssets)

            inAssets.money = inAssets.money.add(transferRecord.money)
            mAppDatabase.assetsDao().updateAssets(inAssets)

            // 保存常用备注
            if (!TextUtils.isEmpty(transferRecord.remark)) {
                mAppDatabase.labelDao().insertLabel(Label(transferRecord.remark))
            }
            autoBackup()
        }
    }

    override fun getTransferRecordsById(id: Int): LiveData<List<AssetsTransferRecordWithAssets>> {
        return mAppDatabase.assetsTransferRecordDao().getTransferRecordsById(id)
    }

    override fun deleteTransferRecord(assetsTransferRecord: AssetsTransferRecord): Completable {
        return Completable.fromAction {
            mAppDatabase.assetsTransferRecordDao().deleteTransferRecord(assetsTransferRecord)
            val assetsFrom = mAppDatabase.assetsDao().getAssetsBeanById(assetsTransferRecord.assetsIdFrom)
            val assetsTo = mAppDatabase.assetsDao().getAssetsBeanById(assetsTransferRecord.assetsIdTo)
            if (assetsFrom != null) {
                assetsFrom.money = assetsFrom.money.add(assetsTransferRecord.money)
                mAppDatabase.assetsDao().updateAssets(assetsFrom)
            }
            if (assetsTo != null) {
                assetsTo.money = assetsTo.money.subtract(assetsTransferRecord.money)
                mAppDatabase.assetsDao().updateAssets(assetsTo)
            }
            autoBackup()
        }
    }

    override fun getLabels(): LiveData<List<Label>> {
        return mAppDatabase.labelDao().getLabels()
    }
}
