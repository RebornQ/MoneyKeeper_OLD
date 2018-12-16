package me.bakumon.moneykeeperclone.ui.add

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.floo.Floo
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class TypeSettingViewBinder : ItemViewBinder<RecordType, TypeSettingViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_record_type, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: RecordType) {
        holder.ivTypeImg.setImageResource(ResourcesUtil.getTypeImgId(holder.ivTypeImg.context, item.imgName))
        holder.ivCheck.visibility = View.GONE
        holder.tvTypeName.text = item.name
        holder.llItemRecordType.setOnClickListener {
            Floo.navigation(holder.llItemRecordType.context, Router.Url.URL_TYPE_MANAGE)
                    .putExtra(Router.ExtraKey.KEY_TYPE, item.type)
                    .start()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTypeImg: ImageView = itemView.findViewById(R.id.ivTypeImg)
        val ivCheck: ImageView = itemView.findViewById(R.id.ivCheck)
        val tvTypeName: TextView = itemView.findViewById(R.id.tvTypeName)
        val llItemRecordType: LinearLayout = itemView.findViewById(R.id.llItemRecordType)
    }


}
