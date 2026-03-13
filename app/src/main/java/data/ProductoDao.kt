package com.bebidas.donjorge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert
    suspend fun insertProduct(product: Producto): Long

    @Query("SELECT * FROM productos WHERE isDeleted = 0 ORDER BY name ASC")
    fun listAllProduct(): Flow<List<Producto>>

    @Update
    suspend fun updateProduct(product: Producto)

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): Producto?

    @Query("SELECT * FROM productos WHERE name = :name LIMIT 1")
    suspend fun getProductByName(name: String): Producto?

    @Query("UPDATE productos SET isDeleted = 1 WHERE id = :productId")
    suspend fun logicalDelete(productId: Int)

    @Query("UPDATE productos SET stock = stock - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: Int, quantity: Int)

    @Insert
    suspend fun insertSaleInternal(venta: Sales)

    @Query("SELECT * FROM combo_details WHERE comboId = :comboId")
    suspend fun getDetailsByComboIdInternal(comboId: Int): List<ComboDetail>

    @Transaction
    suspend fun executeSaleAtomics(saleItems: List<SaleRequest>) {
        for (item in saleItems) {
            val product = getProductById(item.productId) ?: continue

            insertSaleInternal(
                Sales(
                    productoId = product.id,
                    cantidadVendida = item.quantity,
                    precioUnitarioVenta = product.priceSale,
                    paymentMethod = item.paymentMethod,
                    fecha = System.currentTimeMillis()
                )
            )

            if (product.isCombo) {
                val ingredients = getDetailsByComboIdInternal(product.id)
                for (ing in ingredients) {
                    decreaseStock(ing.individualProductId, ing.componentQuantity * item.quantity)
                }
            } else {
                decreaseStock(product.id, item.quantity)
            }
        }
    }
}

data class SaleRequest(
    val productId: Int,
    val quantity: Int,
    val paymentMethod: String
)
