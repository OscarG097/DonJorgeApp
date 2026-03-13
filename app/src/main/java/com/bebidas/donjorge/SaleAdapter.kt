package com.bebidas.donjorge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bebidas.donjorge.data.Producto
import java.text.NumberFormat
import java.util.Locale

class SaleAdapter(
    private var allProducts: List<Producto>,
    private val context: Context,
    private val onCartChanged: (Double) -> Unit
) : RecyclerView.Adapter<SaleAdapter.SaleViewHolder>() {

    private var displayedProducts: List<Producto> = allProducts.toList()

    private val cartMap = mutableMapOf<Int, Int>()

    private val productoImagenMap = mapOf(
        "Seleccione un Producto Base" to R.drawable.default_placeholder,
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
        "Botella de agua" to R.drawable.botella_agua,
        // Combos
        "Combo Fernet+Coca" to R.drawable.combo_fernet_coca,
        "Combo 1882+Coca" to R.drawable.combo_1882_coca,
        "Combo Gancia+Sprite" to R.drawable.combo_gancia_sprite,
        "Combo Vodka+Jugo" to R.drawable.combo_smir_jugo,
        "Combo Vodka+Speed" to R.drawable.combo_smir_speed,
        "Combo Campari+Jugo" to R.drawable.combo_campari_jugo,
        "Combo Cosecha+Speed" to R.drawable.combo_cocecha_speed,
        "Combo Gancia+Sprite" to R.drawable.combo_gancia_sprite,
        "Combo Gordons+Tonica" to R.drawable.combo_gordons_tonica,
        "Combo Balbo+Manaos" to R.drawable.combo_balbo_manaos
    )

    inner class SaleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivThumb: ImageView = view.findViewById(R.id.iv_product_thumb)
        val tvName: TextView = view.findViewById(R.id.tv_sale_product_name)
        val tvPrice: TextView = view.findViewById(R.id.tv_sale_product_price)
        val btnAddInitial: ImageButton = view.findViewById(R.id.btn_add_initial)
        val layoutQtyControl: LinearLayout = view.findViewById(R.id.layout_qty_control)
        val btnMinus: ImageButton = view.findViewById(R.id.btn_minus)
        val btnPlus: ImageButton = view.findViewById(R.id.btn_plus)
        val tvQty: TextView = view.findViewById(R.id.tv_qty)

        fun bind(product: Producto) {
            tvName.text = product.name
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
            tvPrice.text = formatter.format(product.priceSale)

            // CORRECCIÓN: Usar localImageRoute (para combos) o localImagePath o name
            val imageKey = product.localImageRoute ?: product.localImagePath ?: product.name
            val drawableId = productoImagenMap[imageKey] ?: R.drawable.default_placeholder
            ivThumb.setImageResource(drawableId)

            val currentQty = cartMap[product.id] ?: 0

            if (currentQty > 0) {
                btnAddInitial.visibility = View.GONE
                layoutQtyControl.visibility = View.VISIBLE
                tvQty.text = currentQty.toString()
            } else {
                btnAddInitial.visibility = View.VISIBLE
                layoutQtyControl.visibility = View.GONE
            }

            btnAddInitial.setOnClickListener {
                if (!product.isCombo && product.stock <= 0) {
                    android.widget.Toast.makeText(context, "Sin stock disponible", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    updateQuantity(product.id, 1)
                }
            }

            btnPlus.setOnClickListener {
                val current = cartMap[product.id] ?: 0

                if (!product.isCombo && current >= product.stock) {
                    android.widget.Toast.makeText(context, "No hay más stock (Max: ${product.stock})", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    updateQuantity(product.id, current + 1)
                }
            }

            btnMinus.setOnClickListener {
                val current = cartMap[product.id] ?: 0
                if (current > 0) {
                    updateQuantity(product.id, current - 1)
                }
            }
        }
    }

    private fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity > 0) {
            cartMap[productId] = quantity
        } else {
            cartMap.remove(productId)
        }
        notifyDataSetChanged()
        calculateTotalAndNotify()
    }

    private fun calculateTotalAndNotify() {
        var total = 0.0
        cartMap.forEach { (id, qty) ->
            val product = allProducts.find { it.id == id }
            if (product != null) {
                total += (product.priceSale * qty)
            }
        }
        onCartChanged(total)
    }

    fun getCartItems(): Map<Producto, Int> {
        val items = mutableMapOf<Producto, Int>()
        cartMap.forEach { (id, qty) ->
            val product = allProducts.find { it.id == id }
            if (product != null) items[product] = qty
        }
        return items
    }

    fun clearCart() {
        cartMap.clear()
        notifyDataSetChanged()
        onCartChanged(0.0)
    }

    fun filter(query: String) {
        displayedProducts = if (query.isEmpty()) {
            allProducts
        } else {
            allProducts.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    fun updateData(newProducts: List<Producto>) {
        allProducts = newProducts
        displayedProducts = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale_product, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        holder.bind(displayedProducts[position])
    }

    override fun getItemCount(): Int = displayedProducts.size
}
