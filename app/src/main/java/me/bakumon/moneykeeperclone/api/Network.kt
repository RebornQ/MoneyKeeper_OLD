package me.bakumon.moneykeeperclone.api

import com.burgstaller.okhttp.AuthenticationCacheInterceptor
import com.burgstaller.okhttp.CachingAuthenticatorDecorator
import com.burgstaller.okhttp.DispatchingAuthenticator
import com.burgstaller.okhttp.basic.BasicAuthenticator
import com.burgstaller.okhttp.digest.CachingAuthenticator
import com.burgstaller.okhttp.digest.DigestAuthenticator
import me.bakumon.moneykeeperclone.BuildConfig
import me.bakumon.moneykeeperclone.ConfigManager
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
            updateDavServiceConfig()
        }
        return davService!!
    }

    fun updateDavServiceConfig() {

        val authCache = ConcurrentHashMap<String, CachingAuthenticator>()
        val credentials = com.burgstaller.okhttp.digest.Credentials(ConfigManager.webDavAccount, ConfigManager.webDAVPsw)
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

        val url = ConfigManager.webDavUrl
        if (url.isEmpty()) {
            return
        }
        val baseUrl = if (url.endsWith("/")) url else "$url/"
        val retrofit = retrofitBuilder.baseUrl(baseUrl)
                .build()
        davService = retrofit.create(DavService::class.java)
    }
}