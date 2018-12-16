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

package me.bakumon.moneykeeperclone.ui.add

import android.arch.lifecycle.Observer
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_record_type.*
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.database.entity.RecordWithType
import me.bakumon.moneykeeperclone.ui.common.BaseFragment

/**
 * 记一笔 记账类型 fragment
 * 对外提供的功能：
 * 1.获取选择的类型
 *
 * @author Bakumon https://bakumon.me
 */
class RecordTypeFragment : BaseFragment() {

    private lateinit var mViewModel: RecordTypeViewModel

    private var mRecord: RecordWithType? = null
    private var mType: Int = RecordType.TYPE_OUTLAY

    override val layoutId: Int
        get() = R.layout.fragment_record_type

    override fun onInit(savedInstanceState: Bundle?) {
        mRecord = arguments?.getSerializable(Router.ExtraKey.KEY_RECORD_BEAN) as RecordWithType?
        if (arguments != null) {
            mType = arguments!!.getInt(Router.ExtraKey.KEY_RECORD_TYPE)
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
        fun newInstance(type: Int, record: RecordWithType? = null): RecordTypeFragment {
            val fragment = RecordTypeFragment()
            val bundle = Bundle()
            bundle.putInt(Router.ExtraKey.KEY_RECORD_TYPE, type)
            bundle.putSerializable(Router.ExtraKey.KEY_RECORD_BEAN, record)
            fragment.arguments = bundle
            return fragment
        }
    }
}
