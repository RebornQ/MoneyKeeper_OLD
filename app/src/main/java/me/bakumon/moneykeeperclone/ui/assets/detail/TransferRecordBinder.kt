package me.bakumon.moneykeeperclone.ui.assets.detail

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.Router
import me.bakumon.moneykeeperclone.database.entity.AssetsTransferRecordWithAssets
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.bakumon.moneykeeperclone.utill.DateUtils
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.floo.Floo
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class TransferRecordBinder constructor(private val onDeleteClickListener: ((AssetsTransferRecordWithAssets) -> Unit)) : ItemViewBinder<AssetsTransferRecordWithAssets, TransferRecordBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_assets_record, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: AssetsTransferRecordWithAssets) {
        val content: String = item.assetsNameFrom + " ➡ " + item.assetsNameTo
        holder.ivTypeImg.setImageResource(ResourcesUtil.getTypeImgId(holder.ivTypeImg.context, "ic_transform"))
        holder.tvTypeName.text = holder.tvTypeName.context.resources.getString(R.string.text_assets_transfer)
        holder.tvRemark.text = content
        holder.tvSubtitle.visibility = View.VISIBLE
        holder.tvSubtitle.text = item.remark
        holder.tvMoney.visibility = View.VISIBLE
        holder.tvMoney.text = BigDecimalUtil.fen2Yuan(item.money)
        holder.tvTime.text = DateUtils.date2MonthDay(item.time)

        holder.llItemClick.setOnLongClickListener {
            showOperateDialog(holder.tvMoney.context, item)
            true
        }

        holder.llItemClick.setOnClickListener {
            Floo.navigation(holder.llItemClick.context, Router.Url.URL_ADD_RECORD)
                    .putExtra(Router.ExtraKey.KEY_IS_TRANSFER, true)
                    .putExtra(Router.ExtraKey.KEY_TRANSFER, item)
                    .start()
        }
    }

    private fun showOperateDialog(context: Context, item: AssetsTransferRecordWithAssets) {
        val money = " (" + ConfigManager.symbol + BigDecimalUtil.fen2Yuan(item.money) + ")"
        MaterialDialog(context)
                .title(text = context.getString(R.string.text_transfer) + money)
                .message(R.string.text_delete_record_note)
                .negativeButton(R.string.text_cancel)
                .positiveButton(R.string.text_affirm_delete) { onDeleteClickListener.invoke(item) }
                .show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llItemClick: LinearLayout = itemView.findViewById(R.id.llItemClick)
        val ivTypeImg: ImageView = itemView.findViewById(R.id.ivTypeImg)
        val tvTypeName: TextView = itemView.findViewById(R.id.tvTypeName)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvRemark: TextView = itemView.findViewById(R.id.tvRemark)
        val tvMoney: TextView = itemView.findViewById(R.id.tvMoney)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }
}
