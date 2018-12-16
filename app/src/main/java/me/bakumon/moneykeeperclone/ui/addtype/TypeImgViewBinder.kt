package me.bakumon.moneykeeperclone.ui.addtype

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class TypeImgViewBinder constructor(private val onClickItemListener: ((Int) -> Unit)) : ItemViewBinder<TypeImgBean, TypeImgViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_type_img, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: TypeImgBean) {
        holder.ivTypeImg.setImageResource(ResourcesUtil.getTypeImgId(holder.ivTypeImg.context, item.imgName))
        holder.ivCheck.visibility = if (item.isChecked) View.VISIBLE else View.GONE
        holder.llItemTypeImg.setOnClickListener {
            if (holder.adapterPosition != -1) {
                onClickItemListener.invoke(holder.adapterPosition)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTypeImg: ImageView = itemView.findViewById(R.id.ivTypeImg)
        val ivCheck: ImageView = itemView.findViewById(R.id.ivCheck)
        val llItemTypeImg: LinearLayout = itemView.findViewById(R.id.llItemTypeImg)
    }
}
