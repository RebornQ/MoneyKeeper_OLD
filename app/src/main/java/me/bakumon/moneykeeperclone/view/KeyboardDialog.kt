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

package me.bakumon.moneykeeperclone.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import me.bakumon.moneykeeperclone.R

/**
 * 恢复备份对话框
 *
 * @author Bakumon https://bakumon.me
 */
class KeyboardDialog(private val mContext: Context, var text: String, var mOnAffirmClickListener: ((String) -> Unit)) {
    private lateinit var mDialog: BottomSheetDialog

    init {
        setupDialog()
    }

    @SuppressLint("InflateParams")
    private fun setupDialog() {
        val layoutInflater = LayoutInflater.from(mContext)
        val contentView = layoutInflater.inflate(R.layout.dialog_keyboard, null, false)
        val keyboardView = contentView.findViewById<KeyboardView>(R.id.keyboard)

        mDialog = BottomSheetDialog(mContext)

        keyboardView.mOnAffirmClickListener = {
            mOnAffirmClickListener.invoke(it)
            mDialog.dismiss()
        }
        keyboardView.setText(text)
        keyboardView.isShowMinus = true
        keyboardView.maxIntegerNumber = 8

        mDialog.setContentView(contentView)
    }

    fun show() {
        mDialog.show()
    }

}
