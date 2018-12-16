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

package me.bakumon.moneykeeperclone.datasource

import android.arch.lifecycle.LiveData
import io.reactivex.Completable
import me.bakumon.moneykeeperclone.database.entity.*
import me.bakumon.moneykeeperclone.ui.addtype.TypeImgBean
import java.math.BigDecimal
import java.util.*

/**
 * 数据源
 *
 * @author Bakumon https://bakumon.me
 */
interface AppDataSource {

    /**
     * 初始化默认的记账类型
     */
    fun initRecordTypes(): Completable

    /**
     * 获取类型表记录数
     */
    fun getRecordTypeCount(): Long

    /**
     * 添加记账类型
     *
     * @param type    类型
     * @param imgName 图片
     * @param name    类型名称
     * @see RecordType.TYPE_OUTLAY
     *
     * @see RecordType.TYPE_INCOME
     */
    fun addRecordType(type: Int, imgName: String, name: String): Completable

    /**
     * 修改记账类型
     *
     * @param oldRecordType 修改之前的 RecordType
     * @param recordType    修改的 RecordType
     */
    fun updateRecordType(oldRecordType: RecordType, recordType: RecordType): Completable

    /**
     * 删除记账类型
     *
     * @param recordType 要删除的记账类型对象
     */
    fun deleteRecordType(recordType: RecordType): Completable

    /**
     * 获取所有记账类型数据
     *
     * @return 所有记账类型数据
     */
    fun getAllRecordType(): LiveData<List<RecordType>>

    /**
     * 获取指出或收入记账类型数据
     *
     * @param type 类型
     * @return 记账类型数据
     * @see RecordType.TYPE_OUTLAY
     *
     * @see RecordType.TYPE_INCOME
     */
    fun getRecordTypes(type: Int): LiveData<List<RecordType>>

    /**
     * 记账类型排序
     *
     * @param recordTypes 记账类型对象
     */
    fun sortRecordTypes(recordTypes: List<RecordType>): Completable

    /**
     * 资产排序
     */
    fun sortAssets(assets: List<Assets>): Completable

    /**
     * 获取类型图片数据
     *
     * @param type 收入或支出类型
     * @return 所有获取类型图片数据
     * @see RecordType.TYPE_OUTLAY
     *
     * @see RecordType.TYPE_INCOME
     */
    fun getAllTypeImgBeans(type: Int): List<TypeImgBean>

    /**
     * 新增一条记账记录
     *
     * @param record 记账记录实体
     */
    fun insertRecord(type: Int, assets: Assets?, record: Record): Completable

    /**
     * 更新一条记账记录
     *
     * @param record 记录对象
     */
    fun updateRecord(oldMoney: BigDecimal, oldType: Int, type: Int, oldAssets: Assets?, assets: Assets?, record: Record): Completable

    /**
     * 删除一天记账记录
     *
     * @param record 要删除的记账记录
     */
    fun deleteRecord(record: RecordWithType): Completable

    /**
     * 获取首页现实的记账记录数据
     * 最近数据
     *
     * @return 当前月份的记录数据的 Flowable 对象
     */
    fun getRecordWithTypesRecent(): LiveData<List<RecordWithType>>

    /**
     * 获取某个资产的记账记录
     */
    fun getRecordWithTypesByAssetsId(assetsId: Int, limit: Int): LiveData<List<RecordWithType>>

    /**
     * 根据类型获取某段时间的记账记录数据
     *
     * @return 包含记录数据的 Flowable 对象
     */
    fun getRecordWithTypes(dateFrom: Date, dateTo: Date, type: Int): LiveData<List<RecordWithType>>

    /**
     * 获取某一类型某段时间的记账记录数据
     *
     * @return 包含记录数据的 Flowable 对象
     */
    fun getRecordWithTypes(dateFrom: Date, dateTo: Date, type: Int, typeId: Int): LiveData<List<RecordWithType>>

    /**
     * 获取某一类型某段时间的记账记录数据，money 排序
     *
     * @return 包含记录数据的 Flowable 对象
     */
    fun getRecordWithTypesSortMoney(dateFrom: Date, dateTo: Date, type: Int, typeId: Int): LiveData<List<RecordWithType>>

    /**
     * 获取本月支出和收入总数
     */
    fun getCurrentMonthSumMoneyLiveData(): LiveData<List<SumMoneyBean>>

    /**
     * 获取某月支出和收入总数
     */
    fun getMonthSumMoneyLiveData(dateFrom: Date, dateTo: Date): LiveData<List<SumMoneyBean>>

    /**
     * 获取某天的合计
     *
     * @param year  年
     * @param month 月
     * @param type  类型
     */
    fun getDaySumMoney(year: Int, month: Int, type: Int): LiveData<List<DaySumMoneyBean>>

    /**
     * 获取按类型汇总数据
     */
    fun getTypeSumMoney(from: Date, to: Date, type: Int): LiveData<List<TypeSumMoneyBean>>

    /**
     * 获取某年（或某段时间）内所有月份的收支总数
     */
    fun getMonthOfYearSumMoney(from: Date, to: Date): LiveData<List<MonthSumMoneyBean>>

    /**
     * 获取今日支出
     */
    fun getTodayOutlay(): List<DaySumMoneyBean>

    /**
     * 获取今日支出
     */
    fun getCurrentOutlay(): List<SumMoneyBean>

    /**
     * 添加资产
     *
     * @param assets 资产
     */
    fun addAssets(assets: Assets): Completable

    /**
     * 修改资产
     *
     * @param assets 资产
     */
    fun updateAssets(assets: Assets): Completable

    /**
     * 删除资产
     *
     * @param assets 资产
     */
    fun deleteAssets(assets: Assets): Completable

    /**
     * 获取资产列表
     */
    fun getAssets(): LiveData<List<Assets>>

    /**
     * 获取资产
     */
    fun getAssetsById(id: Int): LiveData<Assets>

    /**
     * 获取资产
     */
    fun getAssetsBeanById(id: Int): Assets?

    /**
     * 获取资产汇总
     */
    fun getAssetsMoney(): LiveData<AssetsMoneyBean>

    /**
     * 新增资产修改记录
     */
    fun insertAssetsRecord(assetsModifyRecord: AssetsModifyRecord): Completable

    /**
     * 获取资产修改记录列表
     */
    fun getAssetsRecordsById(id: Int): LiveData<List<AssetsModifyRecord>>

    /**
     * 新增转账记录
     */
    fun insertTransferRecord(outAssets: Assets, inAssets: Assets, transferRecord: AssetsTransferRecord): Completable

    /**
     * 新增转账记录
     */
    fun updateTransferRecord(oldMoney: BigDecimal, oldOutAssets: Assets, oldInAssets: Assets, outAssets: Assets, inAssets: Assets, transferRecord: AssetsTransferRecord): Completable

    /**
     * 获取转账记录
     */
    fun getTransferRecordsById(id: Int): LiveData<List<AssetsTransferRecordWithAssets>>

    /**
     * 删除转账记录
     */
    fun deleteTransferRecord(assetsTransferRecord: AssetsTransferRecord): Completable

    /**
     * 获取标签列表
     */
    fun getLabels(): LiveData<List<Label>>
}
