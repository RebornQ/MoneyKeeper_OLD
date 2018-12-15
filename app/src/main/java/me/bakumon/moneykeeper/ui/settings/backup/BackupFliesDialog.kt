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

package me.bakumon.moneykeeper.ui.settings.backup

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.bakumon.moneykeeper.R
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.register
import java.io.File

/**
 * 恢复备份对话框
 *
 * @author Bakumon https://bakumon.me
 */
class BackupFliesDialog(
    private val mContext: Context,
    private val mBackupBeans: List<BackupBean>,
    private val onItemClickListener: ((File) -> Unit)
) {
    private lateinit var mDialog: BottomSheetDialog

    init {
        setupDialog()
    }

    @SuppressLint("InflateParams")
    private fun setupDialog() {
        val layoutInflater = LayoutInflater.from(mContext)
        val contentView = layoutInflater.inflate(R.layout.dialog_backup_files, null, false)
        val rvFiles = contentView.findViewById<RecyclerView>(R.id.rv_files)

        val adapter = MultiTypeAdapter()
        adapter.register(BackupBean::class, FilesViewBinder())
        rvFiles.adapter = adapter

        adapter.items = mBackupBeans
        adapter.notifyDataSetChanged()

        mDialog = BottomSheetDialog(mContext)
        mDialog.setContentView(contentView)
    }

    fun show() {
        mDialog.show()
    }

    fun getDialog(): BottomSheetDialog {
        return mDialog
    }

    internal inner class FilesViewBinder : ItemViewBinder<BackupBean, FilesViewBinder.ViewHolder>() {

        override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
            val root = inflater.inflate(R.layout.item_backup_file, parent, false)
            return ViewHolder(root)
        }

        override fun onBindViewHolder(holder: ViewHolder, item: BackupBean) {
            holder.tvFileName.text = item.name
            holder.tvFileTime.text = item.time
            holder.tvFileSize.text = item.size
            holder.llItemFile.setOnClickListener {
                onItemClickListener.invoke(item.file)
                mDialog.dismiss()
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
            val tvFileTime: TextView = itemView.findViewById(R.id.tvFileTime)
            val tvFileSize: TextView = itemView.findViewById(R.id.tvFileSize)
            val llItemFile: LinearLayout = itemView.findViewById(R.id.llItemFile)
        }
    }

}
