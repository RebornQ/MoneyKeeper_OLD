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

import com.burgstaller.okhttp.AuthenticationCacheInterceptor
import com.burgstaller.okhttp.CachingAuthenticatorDecorator
import com.burgstaller.okhttp.DispatchingAuthenticator
import com.burgstaller.okhttp.basic.BasicAuthenticator
import com.burgstaller.okhttp.digest.CachingAuthenticator
import com.burgstaller.okhttp.digest.Credentials
import com.burgstaller.okhttp.digest.DigestAuthenticator
import me.bakumon.moneykeeper.BuildConfig
import me.bakumon.moneykeeper.DefaultSPHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


object Network {

    private var davService: DavService? = null
    private var loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()

    init {
        // 日志拦截器
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
    }

    fun davService(): DavService {
        if (davService == null) {
            updateDavServiceConfig(DefaultSPHelper.webdavUrl, DefaultSPHelper.webdavUserName, DefaultSPHelper.webdavPsw)
        }
        return davService!!
    }

    fun updateDavServiceConfig(url: String, userName: String, pwd: String) {

        val authCache = ConcurrentHashMap<String, CachingAuthenticator>()
        val credentials = Credentials(userName, pwd)
        val basicAuthenticator = BasicAuthenticator(credentials)
        val digestAuthenticator = DigestAuthenticator(credentials)

        // note that all auth schemes should be registered as lowercase!
        val authenticator = DispatchingAuthenticator.Builder()
            .with("digest", digestAuthenticator)
            .with("basic", basicAuthenticator)
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .addInterceptor(loggingInterceptor)
            .authenticator(CachingAuthenticatorDecorator(authenticator, authCache))
            .addInterceptor(AuthenticationCacheInterceptor(authCache))
            .build()

        val retrofitBuilder = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(EmptyConverterFactory())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        if (url.isEmpty()) {
            return
        }
        val baseUrl = if (url.endsWith("/")) url else "$url/"
        val retrofit = retrofitBuilder.baseUrl(baseUrl)
            .build()
        davService = retrofit.create(DavService::class.java)
    }
}