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

import android.text.TextUtils
import me.bakumon.moneykeeperclone.database.entity.MonthSumMoneyBean
import me.bakumon.moneykeeperclone.database.entity.RecordType
import java.math.BigDecimal

/**
 * 回顾列表数据转换器
 *
 * @author Bakumon https://bakumon.me
 */
object ReviewItemDataConverter {

    fun getBarEntryList(beans: List<MonthSumMoneyBean>): List<ReviewItemBean> {
        val itemDataList = arrayListOf<ReviewItemBean>()
        try {
            if (beans.isNotEmpty()) {
                var reviewItemBean: ReviewItemBean
                var date: List<String>
                var bean: MonthSumMoneyBean
                for (i in beans.indices) {
                    bean = beans[i]
                    date = bean.month.split("-")
                    val year = date[0]
                    val month = date[1]
                    reviewItemBean = ReviewItemBean(year, month)
                    val outlay = if (bean.type == RecordType.TYPE_OUTLAY) bean.sumMoney else BigDecimal(0)
                    val income = if (bean.type == RecordType.TYPE_INCOME) bean.sumMoney else BigDecimal(0)
                    val overage = income.subtract(outlay)
                    reviewItemBean.outlay = outlay
                    reviewItemBean.income = income
                    reviewItemBean.overage = overage

                    if (itemDataList.isEmpty()) {
                        itemDataList.add(reviewItemBean)
                    } else {
                        // 由于 beans 是按照时间排序的，所以这里只和上一个比较
                        if (TextUtils.equals(itemDataList[itemDataList.size - 1].month, month) &&
                                TextUtils.equals(itemDataList[itemDataList.size - 1].year, year)) {
                            if (bean.type == RecordType.TYPE_OUTLAY) {
                                reviewItemBean.outlay = bean.sumMoney
                                reviewItemBean.income = itemDataList[itemDataList.size - 1].income
                                reviewItemBean.overage = reviewItemBean.income!!.subtract(reviewItemBean.outlay)
                                itemDataList[itemDataList.size - 1] = reviewItemBean
                            } else {
                                reviewItemBean.income = bean.sumMoney
                                reviewItemBean.outlay = itemDataList[itemDataList.size - 1].outlay
                                reviewItemBean.overage = reviewItemBean.income!!.subtract(reviewItemBean.outlay)
                                itemDataList[itemDataList.size - 1] = reviewItemBean
                            }
                        } else {
                            itemDataList.add(reviewItemBean)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return itemDataList
    }
}
