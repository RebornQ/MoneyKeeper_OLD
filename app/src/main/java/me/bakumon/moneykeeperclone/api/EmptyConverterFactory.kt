package me.bakumon.moneykeeperclone.api

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
