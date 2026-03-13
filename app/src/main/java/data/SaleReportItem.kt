package com.bebidas.donjorge.data

/**
 * Objeto de transferencia de datos (DTO) para el reporte de ventas.
 * Representa un producto vendido agrupado por día.
 */
data class SaleReportItem(
    val saleDate: String,            // Formato YYYY-MM-DD
    val productoId: Int,
    val productName: String,
    val productImage: String?,
    val isCombo: Boolean,
    val totalQuantity: Int,
    val unitPrice: Double,           // Precio al que se vendió
    val totalAmount: Double,         // totalQuantity * unitPrice
    val totalTransfer: Double,       // Suma de montos por transferencia
    val totalCash: Double            // Suma de montos por efectivo
)
