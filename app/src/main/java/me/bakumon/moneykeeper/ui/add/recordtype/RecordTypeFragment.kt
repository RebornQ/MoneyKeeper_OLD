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

package me.bakumon.moneykeeper.ui.add.recordtype

import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_record_type.*
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.RecordForList
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.ui.common.BaseFragment

/**
 * 记一笔 记账类型 fragment
 * 对外提供的功能：
 * 1.获取选择的类型
 *
 * @author Bakumon https://bakumon.me
 */
class RecordTypeFragment : BaseFragment() {

    private lateinit var mViewModel: RecordTypeViewModel

    private var mRecord: RecordForList? = null
    private var mType: Int = RecordType.TYPE_OUTLAY

    override val layoutId: Int
        get() = R.layout.fragment_record_type

    override fun onInit(savedInstanceState: Bundle?) {
        mRecord = arguments?.getSerializable(KEY_RECORD_BEAN) as RecordForList?
        if (arguments != null) {
            mType = arguments!!.getInt(KEY_RECORD_TYPE)
        }

        mViewModel = getViewModel()
        getAllRecordTypes()
    }

    override fun lazyInitData() {

    }

    private fun getAllRecordTypes() {
        mViewModel.allRecordTypes.observe(this, Observer {
            typePage.setItems(it, mType, mRecord)
        })
    }

    fun getType(): RecordType? {
        return typePage.currentItem
    }

    companion object {
        private const val KEY_RECORD_TYPE = "KEY_RECORD_TYPE"
        private const val KEY_RECORD_BEAN = "KEY_RECORD_BEAN"
        fun newInstance(type: Int, record: RecordForList? = null): RecordTypeFragment {
            val fragment = RecordTypeFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_RECORD_TYPE, type)
            bundle.putSerializable(KEY_RECORD_BEAN, record)
            fragment.arguments = bundle
            return fragment
        }
    }
}
