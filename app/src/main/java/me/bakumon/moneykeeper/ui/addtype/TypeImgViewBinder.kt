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

package me.bakumon.moneykeeper.ui.addtype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import me.bakumon.moneykeeper.R
import me.bakumon.moneykeeper.utill.ResourcesUtil
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
