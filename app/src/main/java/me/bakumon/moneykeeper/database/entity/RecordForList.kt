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

package me.bakumon.moneykeeper.database.entity

/**
 * 包含 Assets 名称 和 RecordType 图片、名称 的 Record
 * 用于关联查询
 *
 * @author Bakumon https://bakumon.me
 */
class RecordForList : Record() {
    /**
     * 资产名称
     */
    var assetsName: String? = null
    /**
     * 分类图标
     */
    var typeImgName: String? = null
    /**
     * 分类名称
     */
    var typeName: String? = null
    /**
     * 分类类型
     */
    var type: Int = 0
}
