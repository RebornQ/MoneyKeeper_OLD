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

package me.bakumon.moneykeeperclone.ui.review

import android.arch.lifecycle.LiveData
import me.bakumon.moneykeeperclone.database.entity.MonthSumMoneyBean
import me.bakumon.moneykeeperclone.database.entity.SumMoneyBean
import me.bakumon.moneykeeperclone.datasource.AppDataSource
import me.bakumon.moneykeeperclone.ui.common.BaseViewModel
import me.bakumon.moneykeeperclone.utill.DateUtils

/**
 * 回顾
 *
 * @author Bakumon https://bakumon.me
 */
class ReviewViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {

    fun getYearSumMoney(year: Int): LiveData<List<SumMoneyBean>> {
        val dateFrom = DateUtils.getYearStart(year)
        val dateTo = DateUtils.getYearEnd(year)
        return mDataSource.getMonthSumMoneyLiveData(dateFrom, dateTo)
    }

    fun getMonthOfYearSumMoney(year: Int): LiveData<List<MonthSumMoneyBean>> {
        val dateFrom = DateUtils.getYearStart(year)
        val dateTo = DateUtils.getYearEnd(year)
        return mDataSource.getMonthOfYearSumMoney(dateFrom, dateTo)
    }

}
