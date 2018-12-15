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
package me.bakumon.moneykeeper.api

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * 不转换实体类型，全部使用 ResponseBody
 *
 * @author Bakumon http://bakumon.me
 */
class EmptyConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        return EmptyResponseBodyConverter<ResponseBody>()
    }

    @Suppress("UNCHECKED_CAST")
    inner class EmptyResponseBodyConverter<T> : Converter<ResponseBody, T> {
        override fun convert(value: ResponseBody): T {
            return value as T
        }
    }
}
