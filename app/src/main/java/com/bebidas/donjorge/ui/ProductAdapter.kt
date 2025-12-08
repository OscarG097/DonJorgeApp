package com.bebidas.donjorge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bebidas.donjorge.data.Producto
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var productos: List<Producto>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        private val productoImagenMap = mapOf(
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
        val drawableId = productoImagenMap[product.name] ?: R.drawable.default_placeholder

        holder.tvNombre.text = product.name
        holder.tvPrecio.text = "Precio venta: ${holder.formatMoneda(product.priceSale)}"
        holder.tvStockActual.text = "Stock actual: ${product.stock}"
        holder.ivImagen.setImageResource(drawableId)
        holder.tvPrecio.text = "Stock vendido: ${holder.formatMoneda(product.priceSale)}"
        holder.tvStockActual.text = "Stock actual: ${product.stock}"
        // Nota: 'Stock vendido' y 'Total vendido' se calcularían con otra tabla (ventas),
        // pero por ahora, los mostramos como cero.
        holder.tvTotalVendido.text = "Total vendido: ${holder.formatMoneda(0.0)}"

        // B. IMAGEN (Necesitarás un mapa similar al de NewProduct.kt aquí)
            // Por ahora, solo puedes cargar una imagen de placeholder o la que viene de la BD.
            // Aquí asumirías un mapa de recursos o una ruta local.

              holder.btnEliminar.setOnClickListener {
            // Lógica para eliminar el producto
            // Acá iría el código para llamar al DAO y eliminar el producto
        }
        holder.btnEditar.setOnClickListener {
            // Lógica para ir a la pantalla de edición
        }
    }

    override fun getItemCount(): Int = productos.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }
}