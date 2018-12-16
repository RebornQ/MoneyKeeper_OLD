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

package me.bakumon.moneykeeper.utill

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import me.bakumon.moneykeeper.DefaultSPHelper
import me.bakumon.moneykeeper.R

/**
 * PermissionUtil
 *
 * @author Bakumon https://bakumon.me
 */
object PermissionUtil {
    /**
     * 简单申请存储权限
     */
    fun requestStoragePermissionSimple(requestContext: Context) {
        AndPermission.with(requestContext)
            .runtime()
            .permission(Permission.Group.STORAGE)
            .rationale { context, _, executor ->
                MaterialDialog(context)
                    .title(res = R.string.text_storage)
                    .message(res = R.string.text_storage_content)
                    .cancelable(false)
                    .positiveButton(res = R.string.text_go_on) { executor.execute() }
                    .show()
            }
            .onGranted { DefaultSPHelper.isLocalAutoBackup = true }
            .onDenied {
                DefaultSPHelper.isLocalAutoBackup = false
                ToastUtils.show(R.string.toast_open_auto_backup)
            }
            .start()
    }

    /**
     * 申请存储权限
     */
    fun requestPermission(requestContext: Context, onResult: (Boolean) -> Unit) {
        // 严格申请
        AndPermission.with(requestContext)
            .runtime()
            .permission(Permission.Group.STORAGE)
            .rationale { context, _, executor ->
                MaterialDialog(context)
                    .title(res = R.string.text_storage)
                    .message(res = R.string.text_storage_content)
                    .cancelable(false)
                    .positiveButton(res = R.string.text_go_on) { executor.execute() }
                    .negativeButton { requestPermission(requestContext, onResult) }
                    .show()
            }
            .onGranted {
                DefaultSPHelper.isLocalAutoBackup = true
                onResult.invoke(true)
            }
            .onDenied {
                if (AndPermission.hasAlwaysDeniedPermission(requestContext, it)) {
                    MaterialDialog(requestContext)
                        .title(res = R.string.text_storage_permission_fail)
                        .message(res = R.string.text_storage_permission_tip)
                        .cancelable(false)
                        .positiveButton(res = R.string.text_storage_permission_tip_setting) {
                            AndPermission.with(requestContext)
                                .runtime()
                                .setting()
                                .onComeback { requestPermission(requestContext, onResult) }
                                .start()
                        }
                        .negativeButton {
                            DefaultSPHelper.isLocalAutoBackup = false
                            onResult.invoke(false)
                        }
                        .show()
                } else {
                    requestPermission(requestContext, onResult)
                }
            }
            .start()
    }

}
