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

package me.bakumon.moneykeeperclone

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.squareup.leakcanary.LeakCanary
import me.drakeet.floo.Floo
import me.drakeet.floo.Target
import me.drakeet.floo.extensions.LogInterceptor
import me.drakeet.floo.extensions.OpenDirectlyHandler
import java.util.*

/**
 * @author Bakumon https://bakumon.me
 */
class App : Application(), Application.ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        // Normal app init code...
        val mappings = HashMap<String, Target>(16)
        mappings[Router.Url.URL_HOME] = Target("mkc://bakumon.me/home")
        mappings[Router.Url.URL_ADD_RECORD] = Target("mkc://bakumon.me/addRecord")
        mappings[Router.Url.URL_TYPE_MANAGE] = Target("mkc://bakumon.me/typeManage")
        mappings[Router.Url.URL_TYPE_SORT] = Target("mkc://bakumon.me/typeSort")
        mappings[Router.Url.URL_ADD_TYPE] = Target("mkc://bakumon.me/addType")
        mappings[Router.Url.URL_STATISTICS] = Target("mkc://bakumon.me/statistics")
        mappings[Router.Url.URL_TYPE_RECORDS] = Target("mkc://bakumon.me/typeRecords")
        mappings[Router.Url.URL_SETTING] = Target("mkc://bakumon.me/setting")
        mappings[Router.Url.URL_ABOUT] = Target("mkc://bakumon.me/about")
        mappings[Router.Url.URL_REVIEW] = Target("mkc://bakumon.me/review")
        mappings[Router.Url.URL_BACKUP] = Target("mkc://bakumon.me/backup")
        mappings[Router.Url.URL_OTHER_SETTING] = Target("mkc://bakumon.me/other_setting")
        mappings[Router.Url.URL_ASSETS] = Target("mkc://bakumon.me/assets")
        mappings[Router.Url.URL_CHOOSE_ASSETS] = Target("mkc://bakumon.me/choose_assets")
        mappings[Router.Url.URL_ADD_ASSETS] = Target("mkc://bakumon.me/add_assets")
        mappings[Router.Url.URL_ASSETS_DETAIL] = Target("mkc://bakumon.me/assets_detail")

        Floo.configuration()
                .setDebugEnabled(BuildConfig.DEBUG)
                .addRequestInterceptor(PureSchemeInterceptor(getString(R.string.scheme)))
                .addRequestInterceptor(LogInterceptor("Request"))
                .addTargetInterceptor(PureSchemeInterceptor(getString(R.string.scheme)))
                .addTargetInterceptor(LogInterceptor("Target"))
                .addTargetNotFoundHandler(OpenDirectlyHandler())

        Floo.apply(mappings)

        registerActivityLifecycleCallbacks(this)
    }

    private var foregroundActivityCount = 0

    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {

    }

    override fun onActivityStarted(activity: Activity?) {
        foregroundActivityCount++
    }

    override fun onActivityDestroyed(activity: Activity?) {

    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity?) {
        foregroundActivityCount--
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }

    fun isAppForeground(): Boolean {
        return foregroundActivityCount > 0
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
