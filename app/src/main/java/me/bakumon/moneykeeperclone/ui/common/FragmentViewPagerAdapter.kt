package me.bakumon.moneykeeperclone.ui.common

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.*

/**
 * FragmentViewPagerAdapter
 *
 * @author Bakumon https://bakumon.me
 */
class FragmentViewPagerAdapter(fm: FragmentManager, private val fragments: ArrayList<Fragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}