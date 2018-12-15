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

package me.bakumon.moneykeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.bakumon.moneykeeper.datasource.AppDataSource
import me.bakumon.moneykeeper.ui.add.AddRecordViewModel
import me.bakumon.moneykeeper.ui.add.option.OptionViewModel
import me.bakumon.moneykeeper.ui.add.recordtype.RecordTypeViewModel
import me.bakumon.moneykeeper.ui.add.transfer.TransferAssetsViewModel
import me.bakumon.moneykeeper.ui.addtype.AddTypeViewModel
import me.bakumon.moneykeeper.ui.assets.AssetsViewModel
import me.bakumon.moneykeeper.ui.assets.add.AddAssetsViewModel
import me.bakumon.moneykeeper.ui.assets.detail.AssetsDetailViewModel
import me.bakumon.moneykeeper.ui.assets.detail.ModifyListViewModel
import me.bakumon.moneykeeper.ui.assets.detail.OrderListViewModel
import me.bakumon.moneykeeper.ui.assets.detail.TransferRecordViewModel
import me.bakumon.moneykeeper.ui.home.HomeViewModel
import me.bakumon.moneykeeper.ui.review.ReviewViewModel
import me.bakumon.moneykeeper.ui.settings.backup.BackupViewModel
import me.bakumon.moneykeeper.ui.settings.other.OtherSettingsViewModel
import me.bakumon.moneykeeper.ui.statistics.bill.BillViewModel
import me.bakumon.moneykeeper.ui.statistics.reports.ReportsViewModel
import me.bakumon.moneykeeper.ui.typemanage.TypeManageViewModel
import me.bakumon.moneykeeper.ui.typerecords.TypeRecordsViewModel
import me.bakumon.moneykeeper.ui.typesort.TypeSortViewModel

/**
 * ViewModel 工厂
 *
 * @author Bakumon https://bakumon.me
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val mDataSource: AppDataSource) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddRecordViewModel::class.java) -> AddRecordViewModel(mDataSource) as T
            modelClass.isAssignableFrom(RecordTypeViewModel::class.java) -> RecordTypeViewModel(
                mDataSource
            ) as T
            modelClass.isAssignableFrom(TransferAssetsViewModel::class.java) -> TransferAssetsViewModel(
                mDataSource
            ) as T
            modelClass.isAssignableFrom(OptionViewModel::class.java) -> OptionViewModel(
                mDataSource
            ) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(mDataSource) as T
            modelClass.isAssignableFrom(TypeManageViewModel::class.java) -> TypeManageViewModel(mDataSource) as T
            modelClass.isAssignableFrom(TypeSortViewModel::class.java) -> TypeSortViewModel(mDataSource) as T
            modelClass.isAssignableFrom(AddTypeViewModel::class.java) -> AddTypeViewModel(mDataSource) as T
            modelClass.isAssignableFrom(BillViewModel::class.java) -> BillViewModel(mDataSource) as T
            modelClass.isAssignableFrom(ReportsViewModel::class.java) -> ReportsViewModel(mDataSource) as T
            modelClass.isAssignableFrom(TypeRecordsViewModel::class.java) -> TypeRecordsViewModel(mDataSource) as T
            modelClass.isAssignableFrom(ReviewViewModel::class.java) -> ReviewViewModel(mDataSource) as T
//            modelClass.isAssignableFrom(SettingViewModel::class.java) -> SettingViewModel(mDataSource) as T
            modelClass.isAssignableFrom(OtherSettingsViewModel::class.java) -> OtherSettingsViewModel(mDataSource) as T
            modelClass.isAssignableFrom(BackupViewModel::class.java) -> BackupViewModel(mDataSource) as T
//            modelClass.isAssignableFrom(BackupViewModel::class.java) -> BackupViewModel(mDataSource) as T
            modelClass.isAssignableFrom(AssetsViewModel::class.java) -> AssetsViewModel(mDataSource) as T
            modelClass.isAssignableFrom(AddAssetsViewModel::class.java) -> AddAssetsViewModel(mDataSource) as T
            modelClass.isAssignableFrom(AssetsDetailViewModel::class.java) -> AssetsDetailViewModel(mDataSource) as T
//            modelClass.isAssignableFrom(TransferViewModel::class.java) -> TransferViewModel(mDataSource) as T
            modelClass.isAssignableFrom(ModifyListViewModel::class.java) -> ModifyListViewModel(mDataSource) as T
            modelClass.isAssignableFrom(TransferRecordViewModel::class.java) -> TransferRecordViewModel(mDataSource) as T
            modelClass.isAssignableFrom(OrderListViewModel::class.java) -> OrderListViewModel(mDataSource) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
