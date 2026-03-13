package com.bebidas.donjorge

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bebidas.donjorge.data.AppDatabase
import com.bebidas.donjorge.data.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var productos: List<Producto>,
    private val context: Context
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val db = AppDatabase.getDatabase(context)

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

        "Seleccionar Imagen del Combo" to R.drawable.default_placeholder,
        "Combo Fernet+Coca" to R.drawable.combo_fernet_coca,
        "Combo 1882+Coca" to R.drawable.combo_1882_coca,
        "Combo Gancia+Sprite" to R.drawable.combo_gancia_sprite,
        "Combo Vodka+Jugo" to R.drawable.combo_smir_jugo,
        "Combo Vodka+Speed" to R.drawable.combo_smir_speed,
        "Combo Campari+Jugo" to R.drawable.combo_campari_jugo,
        "Combo Cosecha+Speed" to R.drawable.combo_cocecha_speed,
        "Combo Gordons+Tonica" to R.drawable.combo_gordons_tonica,
        "Combo Balbo+Manaos" to R.drawable.combo_balbo_manaos
    )

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_producto_nombre)
        val tvPrecio: TextView = view.findViewById(R.id.tv_producto_precio)
        val tvStockActual: TextView = view.findViewById(R.id.tv_producto_stock_actual)
        val tvTotalVendido: TextView = view.findViewById(R.id.tv_producto_total_vendido)
        val ivImagen: ImageView = view.findViewById(R.id.iv_producto_item_imagen)
        val btnEditar: ImageButton = view.findViewById(R.id.btn_editar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btn_eliminar)

        private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR"))

        fun formatMoneda(valor: Double): String {
            return currencyFormatter.format(valor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productos[position]

        holder.tvNombre.text = product.name
        holder.tvPrecio.text = "Precio venta: ${holder.formatMoneda(product.priceSale)}"
        holder.tvTotalVendido.text = "Total vendido: ${holder.formatMoneda(0.0)}"
        val imageKey = product.localImageRoute ?: product.name
        val drawableId = productoImagenMap[imageKey] ?: R.drawable.default_placeholder
        holder.ivImagen.setImageResource(drawableId)

        if (product.isCombo) {
            holder.tvStockActual.text = "Cargando componentes..."

            CoroutineScope(Dispatchers.IO).launch {
                val detalles = db.comboDetailDao().getComboRecipe(product.id).firstOrNull() ?: emptyList()

                val nombresComponentes = detalles.map { detalle ->
                    val prod = db.productoDao().getProductById(detalle.individualProductId)
                    if (prod != null) "${detalle.componentQuantity}x ${prod.name}" else "Desconocido"
                }

                val textoReceta = if (nombresComponentes.isNotEmpty()) {
                    "Incluye: " + nombresComponentes.joinToString(", ")
                } else {
                    "Sin componentes definidos"
                }

                withContext(Dispatchers.Main) {
                    holder.tvStockActual.text = textoReceta
                }
            }

        } else {
            holder.tvStockActual.text = "Stock actual: ${product.stock}"
        }

        // --- Botón ELIMINAR ---
        holder.btnEliminar.setOnClickListener {
            showDeleteConfirmationDialog(product)
        }

        // --- Botón EDITAR ---
        holder.btnEditar.setOnClickListener {
            val intent = Intent(context, EditProductActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            context.startActivity(intent)
        }
    }

    private fun showDeleteConfirmationDialog(product: Producto) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Eliminar Producto")
        builder.setMessage("¿Estás seguro de que deseas eliminar '${product.name}'? Esto también borrará su historial de ventas y recetas de combos.")
        
        builder.setPositiveButton("ELIMINAR") { _, _ ->
            deleteProduct(product)
        }
        
        builder.setNegativeButton("Cancelar", null)
        
        val dialog = builder.create()
        dialog.show()
        
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.RED)
    }

    private fun deleteProduct(product: Producto) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.productoDao().logicalDelete(product.id)
                // No hace falta avisar al adapter manualmente porque ListProductsActivity 
                // observa un Flow que se disparará automáticamente al cambiar la DB.
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Opcional: Mostrar error si falla
                }
            }
        }
    }

    override fun getItemCount(): Int = productos.size

    fun refreshList(nuevaLista: List<Producto>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }
}
