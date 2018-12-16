package me.bakumon.moneykeeperclone.utill

import android.view.View
import android.view.animation.AnimationUtils
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.R

/**
 * @author Bakumon https://bakumon.me
 */
object ViewUtil {
    fun startShake(view: View) {
        val animation = AnimationUtils.loadAnimation(App.instance, R.anim.shake)
        view.startAnimation(animation)
    }
}