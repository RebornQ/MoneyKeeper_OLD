package me.bakumon.moneykeeperclone.ui.statistics.reports

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import me.bakumon.moneykeeperclone.App
import me.bakumon.moneykeeperclone.ConfigManager
import me.bakumon.moneykeeperclone.R
import me.bakumon.moneykeeperclone.database.entity.RecordType
import me.bakumon.moneykeeperclone.database.entity.TypeSumMoneyBean
import me.bakumon.moneykeeperclone.utill.BigDecimalUtil
import me.bakumon.moneykeeperclone.utill.ResourcesUtil
import me.drakeet.multitype.ItemViewBinder
import java.math.BigDecimal

/**
 * @author Bakumon https://bakumon.me
 */
class ReportsViewBinder constructor(private val onItemClickListener: ((TypeSumMoneyBean) -> Unit)) : ItemViewBinder<TypeSumMoneyBean, ReportsViewBinder.ViewHolder>() {

    var colors: List<Int> = arrayListOf()
    private var maxvalueFix: BigDecimal = BigDecimal(0)
    var maxValue: BigDecimal = BigDecimal(0)
        set(value) {
            field = value
            // 和 BarEntryConverter 补偿高度相同逻辑
            maxvalueFix = value.divide(BigDecimal(25), 0, BigDecimal.ROUND_HALF_DOWN)
        }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_report, parent, false)
        return ViewHolder(root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: TypeSumMoneyBean) {
        holder.ivTypeImg.setImageResource(ResourcesUtil.getTypeImgId(holder.ivTypeImg.context, item.imgName))
        holder.tvTypeName.text = item.typeName
        holder.tvSumMoney.text = ConfigManager.symbol + BigDecimalUtil.fen2Yuan(item.typeSumMoney)
        val colorId = if (item.type == RecordType.TYPE_OUTLAY) {
            R.color.colorOutlay
        } else {
            R.color.colorIncome
        }
        holder.tvSumMoney.setTextColor(holder.tvSumMoney.context.resources.getColor(colorId))
        holder.tvCount.text = item.count.toString() + holder.tvCount.context.getString(R.string.text_unit_account)

        holder.viewLength.layoutParams = LinearLayout.LayoutParams(0, 10,
                item.typeSumMoney.add(maxvalueFix).toFloat())
        holder.viewLengthEnd.layoutParams = LinearLayout.LayoutParams(0, 10,
                maxValue.subtract(item.typeSumMoney.add(maxvalueFix)).toFloat())

        val gradient = holder.viewLength.background as GradientDrawable
        if (colors.size > holder.adapterPosition) {
            gradient.setColor(colors[holder.adapterPosition])
        } else {
            gradient.setColor(ContextCompat.getColor(App.instance, R.color.colorPieChart1))
        }

        holder.llItemReport.setOnClickListener { onItemClickListener.invoke(item) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTypeImg: ImageView = itemView.findViewById(R.id.ivTypeImg)
        val tvTypeName: TextView = itemView.findViewById(R.id.tvTypeName)
        val viewLength: View = itemView.findViewById(R.id.viewLength)
        val viewLengthEnd: View = itemView.findViewById(R.id.viewLengthEnd)
        val tvSumMoney: TextView = itemView.findViewById(R.id.tvSumMoney)
        val tvCount: TextView = itemView.findViewById(R.id.tvCount)
        val llItemReport: LinearLayout = itemView.findViewById(R.id.llItemReport)
    }
}
