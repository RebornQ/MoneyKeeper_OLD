package me.bakumon.moneykeeperclone.utill

import android.content.Context

/**
 * @author Bakumon https://bakumon.me
 */
object ResourcesUtil {

    /**
     * 获取类型图片 ID
     */
    fun getTypeImgId(context: Context, imgName: String?): Int {
        return context.resources.getIdentifier(
                if (imgName.isNullOrEmpty()) "type_item_default" else imgName,
                "mipmap",
                context.packageName)
    }
}
