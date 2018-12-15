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

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.FloatRange

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.regex.Pattern

/**
 * 状态栏
 * Created by bakumon on 17-7-8.
 */
object StatusBarUtil {
    var DEFAULT_COLOR = 0
    var DEFAULT_ALPHA = 0f

    private val flymePattren = Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE)
    private val flymeOSPattren = Pattern.compile("Flyme", Pattern.CASE_INSENSITIVE)

    /**
     * 判断是否Flyme4以上
     */
    val isFlyme4Later: Boolean
        get() = (Build.FINGERPRINT.contains("Flyme_OS")
                || Build.VERSION.INCREMENTAL.contains("Flyme_OS")
                || flymePattren.matcher(Build.DISPLAY).find()
                || flymeOSPattren.matcher(Build.DISPLAY).find()
                || Build.FINGERPRINT.contains("Meizu"))

    /**
     * 判断是否为MIUI6以上
     */
    val isMIUI6Later: Boolean
        get() {
            try {
                val clz = Class.forName("android.os.SystemProperties")
                val mtd = clz.getMethod("get", String::class.java)
                var `val` = mtd.invoke(null, "ro.miui.ui.version.name") as String
                `val` = `val`.replace("[vV]".toRegex(), "")
                val version = Integer.parseInt(`val`)
                return version >= 6
            } catch (e: Exception) {
                return false
            }

        }

    fun immersive(activity: Activity) {
        immersive(activity.window, DEFAULT_COLOR, DEFAULT_ALPHA)
    }

    fun immersive(activity: Activity, color: Int = DEFAULT_COLOR, @FloatRange(from = 0.0, to = 1.0) alpha: Float = DEFAULT_ALPHA) {
        immersive(activity.window, color, alpha)
    }

    fun immersive(activity: Activity, color: Int) {
        immersive(activity.window, color, 1f)
    }

    fun immersive(window: Window) {
        immersive(window, DEFAULT_COLOR, DEFAULT_ALPHA)
    }

    @JvmOverloads
    fun immersive(window: Window, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1f) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = mixtureColor(color, alpha)

            var systemUiVisibility = window.decorView.systemUiVisibility
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.decorView.systemUiVisibility = systemUiVisibility
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            setTranslucentView(window.decorView as ViewGroup, color, alpha)
        } else if (Build.VERSION.SDK_INT >= 16) {
            var systemUiVisibility = window.decorView.systemUiVisibility
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.decorView.systemUiVisibility = systemUiVisibility
        }
    }

    fun darkMode(activity: Activity, dark: Boolean) {
        if (isFlyme4Later) {
            darkModeForFlyme4(activity.window, dark)
        } else if (isMIUI6Later) {
            darkModeForMIUI6(activity.window, dark)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(activity.window, dark)
        }
    }

    /**
     * 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)
     */
    fun darkMode(activity: Activity) {
        darkMode(activity.window, DEFAULT_COLOR, DEFAULT_ALPHA)
    }

    fun darkMode(activity: Activity, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        darkMode(activity.window, color, alpha)
    }

    /**
     * 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)
     */
    fun darkMode(window: Window, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        if (isFlyme4Later) {
            immersive(window, color, alpha)
            darkModeForFlyme4(window, true)
        } else if (isMIUI6Later) {
            immersive(window, color, alpha)
            darkModeForMIUI6(window, true)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            immersive(window, color, alpha)
            darkModeForM(window, true)
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            setTranslucentView(window.decorView as ViewGroup, color, alpha)
        }
    }

    /**
     * android 6.0设置字体颜色
     */
    private fun darkModeForM(window: Window, dark: Boolean) {
        var systemUiVisibility = window.decorView.systemUiVisibility
        if (dark) {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            systemUiVisibility = systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    /**
     * 设置Flyme4+的darkMode,darkMode时候字体颜色及icon变黑
     * http://open-wiki.flyme.cn/index.php?title=Flyme%E7%B3%BB%E7%BB%9FAPI
     */
    fun darkModeForFlyme4(window: Window?, dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                        .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (dark) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
                result = true
            } catch (var8: Exception) {
                Log.e("StatusBar", "darkIcon: failed")
            }

        }

        return result
    }

    /**
     * 设置MIUI6+的状态栏是否为darkMode,darkMode时候字体颜色及icon变黑
     * http://dev.xiaomi.com/doc/p=4769/
     */
    fun darkModeForMIUI6(window: Window, darkmode: Boolean): Boolean {
        val clazz = window.javaClass
        try {
            var darkModeFlag: Int
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            extraFlagField.invoke(window, if (darkmode) darkModeFlag else 0, darkModeFlag)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度
     */
    fun setPadding(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setPadding(view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                    view.paddingRight, view.paddingBottom)
        }
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
     */
    fun setPaddingSmart(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= 16) {
            val lp = view.layoutParams
            if (lp != null && lp.height > 0) {
                lp.height += getStatusBarHeight(context)
            }
            view.setPadding(view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                    view.paddingRight, view.paddingBottom)
        }
    }

    /**
     * 增加View的高度以及paddingTop,增加的值为状态栏高度.一般是在沉浸式全屏给ToolBar用的
     */
    fun setHeightAndPadding(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= 16) {
            val lp = view.layoutParams
            lp.height += getStatusBarHeight(context)
            view.setPadding(view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                    view.paddingRight, view.paddingBottom)
        }
    }

    /**
     * 增加View上边距（MarginTop）一般是给高度为 WARP_CONTENT 的小控件用的
     */
    fun setMargin(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= 16) {
            val lp = view.layoutParams
            if (lp is ViewGroup.MarginLayoutParams) {
                lp.topMargin += getStatusBarHeight(context)
            }
            view.layoutParams = lp
        }
    }

    /**
     * 创建假的透明栏
     */
    fun setTranslucentView(container: ViewGroup, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        if (Build.VERSION.SDK_INT >= 19) {
            val mixtureColor = mixtureColor(color, alpha)
            var translucentView: View? = container.findViewById(android.R.id.custom)
            if (translucentView == null && mixtureColor != 0) {
                translucentView = View(container.context)
                translucentView.id = android.R.id.custom
                val lp = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.context))
                container.addView(translucentView, lp)
            }
            if (translucentView != null) {
                translucentView.setBackgroundColor(mixtureColor)
            }
        }
    }

    fun mixtureColor(color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
        val a = if (color and -0x1000000 == 0) 0xff else color.ushr(24)
        return color and 0x00ffffff or ((a * alpha).toInt() shl 24)
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 24
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = context.resources.getDimensionPixelSize(resId)
        } else {
            result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    result.toFloat(), Resources.getSystem().displayMetrics).toInt()
        }
        return result
    }
}
