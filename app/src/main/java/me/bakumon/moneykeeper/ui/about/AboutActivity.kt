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

package me.bakumon.moneykeeper.ui.about

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import me.bakumon.moneykeeper.BuildConfig
import me.bakumon.moneykeeper.Constant
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.utill.AndroidUtil
import me.bakumon.moneykeeper.utill.Pi
import me.bakumon.moneykeeper.utill.StatusBarUtil
import me.bakumon.moneykeeper.utill.ToastUtils
import me.drakeet.multitype.Items
import me.drakeet.support.about.*
import me.drakeet.support.about.extension.RecommendedLoaderDelegate
import me.drakeet.support.about.extension.provided.MoshiJsonConverter
import me.drakeet.support.about.provided.PicassoImageLoader

/**
 * 关于
 *
 * @author Bakumon https://bakumon.me
 */
class AboutActivity : AbsAboutActivity(), OnRecommendedClickedListener, OnContributorClickedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setImmersiveStatus()
        setImageLoader(PicassoImageLoader())
        onRecommendedClickedListener = this
        onContributorClickedListener = this
        toolbar.setNavigationOnClickListener { finish() }
    }

    /**
     * 设置沉浸式状态栏
     */
    private fun setImmersiveStatus() {
        val view = toolbar
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, view)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.setImageResource(R.mipmap.ic_launcher_about)
        slogan.setText(R.string.text_slogan)
        version.text = "v${BuildConfig.VERSION_NAME}"
    }

    override fun onItemsCreated(items: Items) {
        items.add(Category(getString(R.string.text_about_Introduction)))
        items.add(Card(getString(R.string.text_about_detail)))

        items.add(Category(getString(R.string.text_dev_designer)))
        items.add(Contributor(R.mipmap.avatar_markcrs, "Markcrs", getString(R.string.text_designer)))
        items.add(Contributor(R.mipmap.avatar_liangyue, "梁月", getString(R.string.text_launcher_designer)))
        items.add(
            Contributor(
                R.mipmap.avatar_bakumon,
                "Bakumon",
                getString(R.string.text_developer_designer),
                Constant.AUTHOR_URL
            )
        )

        items.add(Category(getString(R.string.text_links)))
        val linksText = getString(
            R.string.text_contact_author,
            Constant.QQ_GROUP,
            Constant.TG_GROUP,
            Constant.AUTHOR_EMAIL,
            Constant.URL_CHANGELOG,
            Constant.APP_OPEN_SOURCE_URL,
            Constant.URL_GREEN_ANDROID
        )
        items.add(Card(linksText))

        items.add(Category(getString(R.string.text_statement)))
        items.add(Card(getString(R.string.text_statement_tip)))

        if (AndroidUtil.isZhRCN()) {
            // Android 应用友链
            RecommendedLoaderDelegate.attach(this, items.size, MoshiJsonConverter())
        }

        items.add(Category(getString(R.string.text_license)))
        OpenSourceListCreator.addAll(items)
    }

    override fun onContributorClicked(itemView: View, contributor: Contributor): Boolean {
        if (contributor.name == "Markcrs" || contributor.name == "梁月") {
            ToastUtils.show(Pi.randomPi())
            return true
        }
        return false
    }

    override fun onRecommendedClicked(itemView: View, recommended: Recommended): Boolean {
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_about, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_favorite -> AndroidUtil.goMarket(this)
        }
        return true
    }

    companion object {
        fun open(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }
}
