package me.bakumon.moneykeeperclone.ui.typemanage

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
class TypeListViewBinder constructor(private val onLongClickItemListener: ((RecordType) -> Unit)) : ItemViewBinder<RecordType, TypeListViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_type_manage, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: RecordType) {
        holder.tvTypeName.text = item.name
        holder.ivTypeImg.setImageResource(ResourcesUtil.getTypeImgId(holder.ivTypeImg.context, item.imgName))
        holder.llItemRecordType.setOnClickListener {
            Floo.navigation(holder.llItemRecordType.context, Router.Url.URL_ADD_TYPE)
                    .putExtra(Router.ExtraKey.KEY_TYPE_BEAN, item)
                    .putExtra(Router.ExtraKey.KEY_TYPE, item.type)
                    .start()
        }
        holder.llItemRecordType.setOnLongClickListener {
            onLongClickItemListener.invoke(item)
            true
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTypeImg: ImageView = itemView.findViewById(R.id.ivTypeImg)
        val tvTypeName: TextView = itemView.findViewById(R.id.tvTypeName)
        val llItemRecordType: LinearLayout = itemView.findViewById(R.id.llItemRecordType)
    }
}
