package com.bebidas.donjorge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bebidas.donjorge.data.SaleReportItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class SalesReportAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ReportDisplayItem> = emptyList()
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "AR"))

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PRODUCT = 1
    }

    sealed class ReportDisplayItem {
        data class Header(val date: String, val dayTotal: Double) : ReportDisplayItem()
        data class Product(val data: SaleReportItem) : ReportDisplayItem()
    }

    private val productoImagenMap = mapOf(
        "Combo Fernet+Coca" to R.drawable.combo_fernet_coca,
        "Combo 1882+Coca" to R.drawable.combo_1882_coca,
        "Combo Gancia+Sprite" to R.drawable.combo_gancia_sprite,
        "Combo Vodka+Jugo" to R.drawable.combo_smir_jugo,
        "Combo Vodka+Speed" to R.drawable.combo_smir_speed,
        "Combo Campari+Jugo" to R.drawable.combo_campari_jugo,
        "Combo Cosecha+Speed" to R.drawable.combo_cocecha_speed,
        "Combo Gordons+Tonica" to R.drawable.combo_gordons_tonica,
        "Combo Balbo+Manaos" to R.drawable.combo_balbo_manaos,
        "Coca-Cola 1.75Lts" to R.drawable.coca,
        "Coca-Cola 2.25Lts" to R.drawable.coca,
        "Botella Mananos" to R.drawable.manaos,
        "Botella Smirnoff saborizado" to R.drawable.botella_smir_sab,
        "Botella Smirnoff" to R.drawable.botella_smirnoff,
        "Lata cerveza Quilmes" to R.drawable.lata_quilmes,
        "Lata cerveza Brahma" to R.drawable.lata_brahma,
        "Lata cerveza Budweiser" to R.drawable.lata_budweiser,
        "Lata cerveza Heineken 710ml" to R.drawable.lata_heineken710,
        "Lata Smirnoff" to R.drawable.lata_smir,
        "Botella de agua" to R.drawable.botella_agua
    )

    fun setData(reportItems: List<SaleReportItem>) {
        val displayList = mutableListOf<ReportDisplayItem>()
        
        // Agrupar por fecha
        val groupedByDate = reportItems.groupBy { it.saleDate }

        for ((date, products) in groupedByDate) {
            val dayTotal = products.sumOf { it.totalAmount }
            displayList.add(ReportDisplayItem.Header(formatFriendlyDate(date), dayTotal))
            
            for (product in products) {
                displayList.add(ReportDisplayItem.Product(product))
            }
        }
        
        this.items = displayList
        notifyDataSetChanged()
    }

    private fun formatFriendlyDate(dateStr: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateStr)
            val friendlySdf = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "AR"))
            friendlySdf.format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            dateStr
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ReportDisplayItem.Header -> TYPE_HEADER
            is ReportDisplayItem.Product -> TYPE_PRODUCT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_product, parent, false)
            ProductViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is HeaderViewHolder && item is ReportDisplayItem.Header) {
            holder.tvDate.text = item.date
            holder.tvTotal.text = "Total: ${currencyFormatter.format(item.dayTotal)}"
        } else if (holder is ProductViewHolder && item is ReportDisplayItem.Product) {
            val data = item.data
            holder.tvName.text = data.productName
            holder.tvDetails.text = "Cant: ${data.totalQuantity} | Precio: ${currencyFormatter.format(data.unitPrice)}"
            holder.tvCash.text = "Efectivo: ${currencyFormatter.format(data.totalCash)}"
            holder.tvTransfer.text = "Transf: ${currencyFormatter.format(data.totalTransfer)}"
            holder.tvTotalItem.text = currencyFormatter.format(data.totalAmount)
            
            val imgKey = data.productImage ?: data.productName
            holder.ivImage.setImageResource(productoImagenMap[imgKey] ?: R.drawable.default_placeholder)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tv_header_date)
        val tvTotal: TextView = view.findViewById(R.id.tv_header_total)
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.iv_report_product)
        val tvName: TextView = view.findViewById(R.id.tv_report_product_name)
        val tvDetails: TextView = view.findViewById(R.id.tv_report_details)
        val tvCash: TextView = view.findViewById(R.id.tv_report_cash)
        val tvTransfer: TextView = view.findViewById(R.id.tv_report_transfer)
        val tvTotalItem: TextView = view.findViewById(R.id.tv_report_total_item)
    }
}
