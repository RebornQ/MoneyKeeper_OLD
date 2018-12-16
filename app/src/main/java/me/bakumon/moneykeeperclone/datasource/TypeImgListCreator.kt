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

import java.util.ArrayList

import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.ui.addtype.TypeImgBean


/**
 * 产生类型图片数据
 *
 * @author Bakumon https://bakumon.me
 */
object TypeImgListCreator {

    fun createTypeImgBeanData(type: Int): List<TypeImgBean> {

        val list = ArrayList<TypeImgBean>()
        var bean: TypeImgBean

        if (type == RecordType.TYPE_OUTLAY) {
            bean = TypeImgBean("type_eat")
            list.add(bean)

            bean = TypeImgBean("type_calendar")
            list.add(bean)

            bean = TypeImgBean("type_3c")
            list.add(bean)

            bean = TypeImgBean("type_clothes")
            list.add(bean)

            bean = TypeImgBean("type_candy")
            list.add(bean)

            bean = TypeImgBean("type_cigarette")
            list.add(bean)

            bean = TypeImgBean("type_humanity")
            list.add(bean)

            bean = TypeImgBean("type_pill")
            list.add(bean)

            bean = TypeImgBean("type_fitness")
            list.add(bean)

            bean = TypeImgBean("type_sim")
            list.add(bean)

            bean = TypeImgBean("type_study")
            list.add(bean)

            bean = TypeImgBean("type_pet")
            list.add(bean)

            bean = TypeImgBean("type_train")
            list.add(bean)

            bean = TypeImgBean("type_plain")
            list.add(bean)

            bean = TypeImgBean("type_bus")
            list.add(bean)

            bean = TypeImgBean("type_home")
            list.add(bean)

            bean = TypeImgBean("type_wifi")
            list.add(bean)

            bean = TypeImgBean("type_insure")
            list.add(bean)

            bean = TypeImgBean("type_outlay_red")
            list.add(bean)

            bean = TypeImgBean("type_adventure")
            list.add(bean)

            bean = TypeImgBean("type_movie")
            list.add(bean)

            bean = TypeImgBean("type_reduce")
            list.add(bean)

            bean = TypeImgBean("type_shopping")
            list.add(bean)

            bean = TypeImgBean("type_handling_fee")
            list.add(bean)
        } else {
            bean = TypeImgBean("type_salary")
            list.add(bean)

            bean = TypeImgBean("type_pluralism")
            list.add(bean)

            bean = TypeImgBean("type_wallet")
            list.add(bean)

            bean = TypeImgBean("type_income_red")
            list.add(bean)

            bean = TypeImgBean("type_back_money")
            list.add(bean)

            bean = TypeImgBean("type_unexpected_income")
            list.add(bean)

            bean = TypeImgBean("type_interest")
            list.add(bean)
        }
        return list
    }

}
