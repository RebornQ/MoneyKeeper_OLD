package me.bakumon.moneykeeperclone.ui.add

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class AssetsNoChooseViewBinder constructor(private val onClickListener: ((NoAccount) -> Unit)) : ItemViewBinder<NoAccount, AssetsNoChooseViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_assets_choose, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NoAccount) {
        val imgName = if (item.type == 0) {
            "ic_no_account"
        } else {
            "ic_add_account"
        }
        holder.ivAccount.setImageResource(ResourcesUtil.getTypeImgId(holder.ivAccount.context, imgName))
        holder.tvAccountName.text = item.title
        if (item.subtitle.isBlank()) {
            holder.tvAccountRemark.visibility = View.GONE
            holder.tvAccountRemark.text = ""
        } else {
            holder.tvAccountRemark.visibility = View.VISIBLE
            holder.tvAccountRemark.text = item.subtitle
        }
        holder.llAccount.setOnClickListener {
            onClickListener.invoke(item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAccount: ImageView = itemView.findViewById(R.id.ivAccount)
        val tvAccountName: TextView = itemView.findViewById(R.id.tvAccountName)
        val tvAccountRemark: TextView = itemView.findViewById(R.id.tvAccountRemark)
        val llAccount: View = itemView.findViewById(R.id.llAccount)
    }
}
