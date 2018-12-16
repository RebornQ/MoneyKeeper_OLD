package me.bakumon.moneykeeperclone.ui.setting

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import me.bakumon.moneykeeperclone.R
import me.drakeet.multitype.ItemViewBinder


/**
 * @author Bakumon https://bakumon.me
 */
class ImgItemViewBinder constructor(private val onNormalItemClickListener: (ImgItem) -> Unit) : ItemViewBinder<ImgItem, ImgItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_setting_img, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ImgItem) {
        holder.tvTitle.text = item.title
        holder.tvContent.text = item.content
        holder.ivIcon.setImageResource(item.imgResId)
        holder.llItemSettingImg.setOnClickListener { onNormalItemClickListener.invoke(item) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvContent: TextView = itemView.findViewById(R.id.tvContent)
        var ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        var llItemSettingImg: LinearLayout = itemView.findViewById(R.id.llItemSettingImg)
    }

}
