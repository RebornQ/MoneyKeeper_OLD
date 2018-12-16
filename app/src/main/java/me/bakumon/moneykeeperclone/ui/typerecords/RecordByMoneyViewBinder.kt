package me.bakumon.moneykeeperclone.ui.typerecords

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
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.database.entity.RecordWithType
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.bakumon.moneykeeperclone.utill.DateUtils
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.floo.Floo
import me.drakeet.multitype.ItemViewBinder

/**
 * @author Bakumon https://bakumon.me
 */
class RecordByMoneyViewBinder constructor(private val onDeleteClickListener: ((RecordWithType) -> Unit)) : ItemViewBinder<RecordWithType, RecordByMoneyViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_record_sort_money, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: RecordWithType) {
        // 时间
        holder.tvTime.text = DateUtils.date2MonthDay(item.time!!)

        // 类型图标
        holder.ivTypeImg.setImageResource(ResourcesUtil.getTypeImgId(holder.ivTypeImg.context, item.mRecordTypes!![0].imgName))

        // 类型名称
        holder.tvTypeName.text = item.mRecordTypes!![0].name

        // 备注
        holder.tvRemark.text = item.remark
        holder.tvRemark.visibility = if (item.remark.isNullOrEmpty()) View.GONE else View.VISIBLE

        // 费用
        // 费用
        val money = if (item.mRecordTypes!![0].type == RecordType.TYPE_OUTLAY) {
            holder.tvMoney.setTextColor(holder.tvMoney.context.resources.getColor(R.color.colorOutlay))
            "-" + BigDecimalUtil.fen2Yuan(item.money)
        } else {
            holder.tvMoney.setTextColor(holder.tvMoney.context.resources.getColor(R.color.colorIncome))
            "+" + BigDecimalUtil.fen2Yuan(item.money)
        }
        holder.tvMoney.text = money

        holder.llItemClick.setOnClickListener {
            Floo.navigation(holder.llItemClick.context, Router.Url.URL_ADD_RECORD)
                    .putExtra(Router.ExtraKey.KEY_RECORD_BEAN, item)
                    .start()
        }

        holder.llItemClick.setOnLongClickListener {
            showOperateDialog(holder.tvMoney.context, item)
            true
        }
    }

    private fun showOperateDialog(context: Context, record: RecordWithType) {
        val money = " (" + ConfigManager.symbol + BigDecimalUtil.fen2Yuan(record.money) + ")"
        MaterialDialog(context)
                .title(text = record.mRecordTypes!![0].name + money)
                .message(R.string.text_delete_record_note)
                .negativeButton(R.string.text_cancel)
                .positiveButton(R.string.text_affirm_delete) { onDeleteClickListener.invoke(record) }
                .show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val ivTypeImg: ImageView = itemView.findViewById(R.id.ivTypeImg)
        val tvTypeName: TextView = itemView.findViewById(R.id.tvTypeName)
        val tvRemark: TextView = itemView.findViewById(R.id.tvRemark)
        val tvMoney: TextView = itemView.findViewById(R.id.tvMoney)
        val llItemClick: LinearLayout = itemView.findViewById(R.id.llItemClick)
    }
}
