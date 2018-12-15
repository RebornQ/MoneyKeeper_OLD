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

package me.bakumon.moneykeeper.ui.typemanage

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_type_manager.*
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.database.entity.RecordType
import me.bakumon.moneykeeper.ui.addtype.AddTypeActivity
import me.bakumon.moneykeeper.ui.common.AbsTwoTabActivity
import me.bakumon.moneykeeper.ui.typesort.TypeSortActivity
import java.util.*

/**
 * 分类管理
 *
 * @author Bakumon https://bakumon
 */
class TypeManageActivity : AbsTwoTabActivity() {

    override val layoutId: Int
        get() = R.layout.activity_type_manager

    override fun onSetupTitle(tvTitle: TextView) {
        tvTitle.text = getString(R.string.text_type_manage)
    }

    override fun getTwoTabText(): ArrayList<String> {
        return arrayListOf(getString(R.string.text_outlay), getString(R.string.text_income))
    }

    override fun getTwoFragments(): ArrayList<Fragment> {
        val outlayFragment = TypeListFragment.newInstance(RecordType.TYPE_OUTLAY)
        val incomeFragment = TypeListFragment.newInstance(RecordType.TYPE_INCOME)
        return arrayListOf(outlayFragment, incomeFragment)
    }

    override fun onParentInitDone() {
        val type = intent.getIntExtra(KEY_TYPE, RecordType.TYPE_OUTLAY)
        if (type == RecordType.TYPE_OUTLAY) {
            setCurrentItem(0)
        } else {
            setCurrentItem(1)
        }

        btnAdd.setOnClickListener {
            AddTypeActivity.open(this, getCurrentType())
        }
    }

    private fun getCurrentType(): Int {
        return when (getTabCurrentIndex()) {
            0 -> RecordType.TYPE_OUTLAY
            else -> RecordType.TYPE_INCOME
        }
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_type_manage
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_sort -> TypeSortActivity.open(this, getCurrentType())
        }
        return super.onOptionsItemSelected(menuItem)
    }

    companion object {
        private const val KEY_TYPE = "KEY_TYPE"
        fun open(context: Context, type: Int = RecordType.TYPE_OUTLAY) {
            val intent = Intent(context, TypeManageActivity::class.java)
            intent.putExtra(KEY_TYPE, type)
            context.startActivity(intent)
        }
    }
}
