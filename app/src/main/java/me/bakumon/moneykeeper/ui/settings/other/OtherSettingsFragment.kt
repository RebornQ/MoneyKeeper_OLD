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
package me.bakumon.moneykeeper.ui.settings.other

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat
import me.bakumon.moneykeeper.Injection
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.base.ErrorResource
import me.bakumon.moneykeeper.base.SuccessResource
import me.bakumon.moneykeeper.ui.common.BaseViewModel
import me.bakumon.moneykeeper.utill.BackupUtil
import me.bakumon.moneykeeper.utill.ToastUtils

class OtherSettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mViewModel: OtherSettingsViewModel

    /**
     * 实例化 BaseViewModel 子类
     */
    inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_other_settings, rootKey)
        mViewModel = getViewModel()
        setupPreferences()
    }

    private fun setupPreferences() {
        val oldFolder = BackupUtil.backupFolder
        findPreference("localBackupFilePath").setOnPreferenceChangeListener { _, _ ->
            chooseFolder(oldFolder)
            true
        }
    }

    private fun chooseFolder(oldFolder: String) {
        mViewModel.move(oldFolder).observe(this, Observer {
            when (it) {
                is SuccessResource<Boolean> -> ToastUtils.show(R.string.toast_move_backup_files_success)
                is ErrorResource<Boolean> -> ToastUtils.show(R.string.toast_move_backup_files_fail)
            }
        })
    }

    companion object {
        fun newInstance(): OtherSettingsFragment {
            return OtherSettingsFragment()
        }
    }

}
