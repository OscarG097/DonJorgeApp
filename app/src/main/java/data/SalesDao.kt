package com.bebidas.donjorge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesDao {

    @Insert
    suspend fun insertVenta(venta: Sales)

    @Query("""
        SELECT 
            strftime('%Y-%m-%d', datetime(s.fecha / 1000, 'unixepoch', 'localtime')) as saleDate,
            s.productoId,
            p.name as productName,
            p.localImageRoute as productImage,
            p.isCombo as isCombo,
            SUM(s.cantidadVendida) as totalQuantity,
            s.precioUnitarioVenta as unitPrice,
            SUM(s.cantidadVendida * s.precioUnitarioVenta) as totalAmount,
            SUM(CASE WHEN s.paymentMethod = 'Transferencia' THEN s.cantidadVendida * s.precioUnitarioVenta ELSE 0 END) as totalTransfer,
            SUM(CASE WHEN s.paymentMethod = 'Efectivo' THEN s.cantidadVendida * s.precioUnitarioVenta ELSE 0 END) as totalCash
        FROM sales s
        JOIN productos p ON s.productoId = p.id
        GROUP BY saleDate, s.productoId, s.precioUnitarioVenta
        ORDER BY s.fecha DESC
    """)
    fun getSalesReport(): Flow<List<SaleReportItem>>

    @Query("""
        SELECT 
            strftime('%Y-%m-%d', datetime(s.fecha / 1000, 'unixepoch', 'localtime')) as saleDate,
            s.productoId,
            p.name as productName,
            p.localImageRoute as productImage,
            p.isCombo as isCombo,
            SUM(s.cantidadVendida) as totalQuantity,
            s.precioUnitarioVenta as unitPrice,
            SUM(s.cantidadVendida * s.precioUnitarioVenta) as totalAmount,
            SUM(CASE WHEN s.paymentMethod = 'Transferencia' THEN s.cantidadVendida * s.precioUnitarioVenta ELSE 0 END) as totalTransfer,
            SUM(CASE WHEN s.paymentMethod = 'Efectivo' THEN s.cantidadVendida * s.precioUnitarioVenta ELSE 0 END) as totalCash
        FROM sales s
        JOIN productos p ON s.productoId = p.id
        WHERE strftime('%Y-%m-%d', datetime(s.fecha / 1000, 'unixepoch', 'localtime')) = :date
        GROUP BY s.productoId, s.precioUnitarioVenta
    """)
    fun getSalesReportByDate(date: String): Flow<List<SaleReportItem>>

    @Query("SELECT SUM(cantidadVendida) FROM sales WHERE productoId = :idProducto")
    fun getTotalStockVendidoPorProducto(idProducto: Int): Flow<Int?>

    @Query("SELECT SUM(CAST(cantidadVendida AS REAL) * precioUnitarioVenta) FROM sales WHERE productoId = :idProducto")
    fun getTotalDineroVendidoPorProducto(idProducto: Int): Flow<Double?>
}
