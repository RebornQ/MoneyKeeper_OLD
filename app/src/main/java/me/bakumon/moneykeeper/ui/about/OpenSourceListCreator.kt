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
package me.bakumon.moneykeeper.ui.about

import me.drakeet.multitype.Items
import me.drakeet.support.about.License

/**
 * 开源库列表数据生成器
 *
 * @author Bakumon https://bakumon.me
 */
object OpenSourceListCreator {
    /**
     * multitype 添加开源库 item 实体
     */
    fun addAll(items: Items) {
        items.add(License("androidx", "Google", License.APACHE_2, "https://source.android.com"))
        items.add(License("androidx lifecycle", "Google", License.APACHE_2, "https://source.android.com"))
        items.add(License("androidx room", "Google", License.APACHE_2, "https://source.android.com"))
        items.add(License("flexbox-layout", "Google", License.APACHE_2, "https://github.com/google/flexbox-layout"))

        items.add(License("RxJava", "ReactiveX", License.APACHE_2, "https://github.com/ReactiveX/RxJava"))
        items.add(License("RxAndroid", "ReactiveX", License.APACHE_2, "https://github.com/ReactiveX/rxAndroid"))

        items.add(License("ProcessPhoenix", "JakeWharton", License.APACHE_2, "https://github.com/JakeWharton/ProcessPhoenix"))

        items.add(License("retrofit", "square", License.APACHE_2, "https://github.com/square/retrofit"))
        items.add(License("okhttp", "square", License.APACHE_2, "https://github.com/square/okhttp"))
        items.add(License("leakcanary", "square", License.APACHE_2, "https://github.com/square/leakcanary"))
        items.add(License("moshi", "square", License.APACHE_2, "https://github.com/square/moshi"))
        items.add(License("Picasso", "square", License.APACHE_2, "https://github.com/square/picasso"))

        items.add(License("okhttp-digest", "rburgst", License.APACHE_2, "https://github.com/rburgst/okhttp-digest"))

        items.add(License("MultiType", "drakeet", License.APACHE_2, "https://github.com/PureWriter/MultiType"))
        items.add(License("about-page", "drakeet", License.APACHE_2, "https://github.com/PureWriter/about-page"))

        items.add(License("material-dialogs", "afollestad", License.MIT, "https://github.com/afollestad/material-dialogs"))

        items.add(License("MaterialDateTimePicker", "wdullaer", License.APACHE_2, "https://github.com/wdullaer/MaterialDateTimePicker"))

        items.add(License("MPAndroidChart", "PhilJay", License.APACHE_2, "https://github.com/PhilJay/MPAndroidChart"))

        items.add(License("prettytime", "ocpsoft", License.APACHE_2, "https://github.com/ocpsoft/prettytime"))

        items.add(License("android-storage", "sromku", License.APACHE_2, "https://github.com/sromku/android-storage"))

        items.add(License("pager-layoutmanager", "GcsSloop", License.APACHE_2, "https://github.com/GcsSloop/pager-layoutmanager"))

        items.add(License("LayoutManagerGroup", "DingMouRen", License.APACHE_2, "https://github.com/DingMouRen/LayoutManagerGroup"))

        items.add(License("CircleIndicator", "ongakuer", License.APACHE_2, "https://github.com/ongakuer/CircleIndicator"))

        items.add(License("FingerprintIdentify", "uccmawei", License.MIT, "https://github.com/uccmawei/FingerprintIdentify"))

        items.add(License("AndPermission", "yanzhenjie", License.APACHE_2, "https://github.com/yanzhenjie/AndPermission"))
    }
}
