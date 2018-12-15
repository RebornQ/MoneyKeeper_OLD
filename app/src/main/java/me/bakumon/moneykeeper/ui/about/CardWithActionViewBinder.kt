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
package me.bakumon.moneykeeper.ui.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.bakumon.moneykeeper.R
import me.drakeet.multitype.ItemViewBinder


/**
 * @author Bakumon https://bakumon.me
 */
class CardWithActionViewBinder : ItemViewBinder<CardWithAction, CardWithActionViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_about_card_with_action, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: CardWithAction) {
        holder.tvContent.text = item.content
        holder.tvAction.text = item.actionText
        holder.tvAction.setOnClickListener { item.action.invoke() }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvContent: TextView = itemView.findViewById(R.id.content)
        var tvAction: TextView = itemView.findViewById(R.id.tvAction)
    }
}
