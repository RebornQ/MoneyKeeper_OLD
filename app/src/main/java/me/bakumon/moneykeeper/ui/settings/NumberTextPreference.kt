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
package me.bakumon.moneykeeper.ui.settings

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.preference.Preference
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.ui.common.BaseTextPreference
import me.bakumon.moneykeeper.utill.BigDecimalUtil
import me.bakumon.moneykeeper.view.KeyboardDialog

class NumberTextPreference : BaseTextPreference {

    private var keyboardDialog: KeyboardDialog? = null

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun init() {
        summaryProvider = SimpleSummaryProvider.instance
    }

    override fun onClick() {
        if (keyboardDialog == null) {
            keyboardDialog = KeyboardDialog(context, text, true) { text = it }
        }
        keyboardDialog!!.show()
    }

    class SimpleSummaryProvider private constructor() : Preference.SummaryProvider<NumberTextPreference> {

        override fun provideSummary(preference: NumberTextPreference): CharSequence {
            return if (TextUtils.isEmpty(preference.text)) {
                preference.context.getString(R.string.text_no_setting)
            } else {
                BigDecimalUtil.formatNum(preference.text)
            }
        }

        companion object {

            private var sSimpleSummaryProvider: SimpleSummaryProvider? = null

            val instance: SimpleSummaryProvider
                get() {
                    if (sSimpleSummaryProvider == null) {
                        sSimpleSummaryProvider = SimpleSummaryProvider()
                    }
                    return sSimpleSummaryProvider!!
                }
        }
    }
}
