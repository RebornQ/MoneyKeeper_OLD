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
                    .title(text = "存储权限")
                    .message(text = "存储权限仅用于自动和手动备份数据到本地，便于数据的保存和恢复，请授予权限。")
                    .cancelable(false)
                    .positiveButton(text = "继续") { executor.execute() }
                    .show()
            }
            .onGranted { DefaultSPHelper.isLocalAutoBackup = true }
            .onDenied {
                DefaultSPHelper.isLocalAutoBackup = false
                ToastUtils.show("注意：没有存储权限，本地备份自动备份已关闭")
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
                    .title(text = "存储权限")
                    .message(text = "存储权限仅用于自动和手动备份数据到本地，便于数据的保存和恢复，请授予权限。")
                    .cancelable(false)
                    .positiveButton(text = "继续") { executor.execute() }
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
                        .title(text = "存储权限申请失败")
                        .message(text = "存储权限仅用于自动和手动备份数据到本地，便于数据的保存和恢复，请到设置界面手动授权，否则将放弃本地备份会恢复功能。")
                        .cancelable(false)
                        .positiveButton(text = "去设置开启") {
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
