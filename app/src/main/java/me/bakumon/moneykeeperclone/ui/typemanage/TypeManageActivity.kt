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

package me.bakumon.moneykeeperclone.ui.typemanage

import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_type_manager.*
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.ui.common.AbsTwoTabActivity
import me.drakeet.floo.Floo
import java.util.*

/**
 * 统计
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
        val type = intent.getIntExtra(Router.ExtraKey.KEY_TYPE, RecordType.TYPE_OUTLAY)
        if (type == RecordType.TYPE_OUTLAY) {
            setCurrentItem(0)
        } else {
            setCurrentItem(1)
        }

        btnAdd.setOnClickListener {
            Floo.navigation(this, Router.Url.URL_ADD_TYPE)
                    .putExtra(Router.ExtraKey.KEY_TYPE, getCurrentType())
                    .start()
        }
    }

    private fun getCurrentType(): Int {
        return when (getTabCurrentIndex()) {
            0 -> RecordType.TYPE_OUTLAY
            else -> RecordType.TYPE_INCOME
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_type_manage, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_sort -> Floo.navigation(this, Router.Url.URL_TYPE_SORT)
                    .putExtra(Router.ExtraKey.KEY_TYPE, getCurrentType())
                    .start()
            android.R.id.home -> finish()
        }
        return true
    }
}
