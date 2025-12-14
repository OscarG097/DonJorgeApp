package com.bebidas.donjorge.data

import androidx.room.Dao

import androidx.room.Insert

import androidx.room.Query
import data.Sales

import kotlinx.coroutines.flow.Flow


@Dao

interface SalesDao {



    @Insert

    suspend fun insertVenta(venta: Sales)



    @Query("SELECT SUM(cantidadVendida) FROM sales WHERE productoId = :idProducto")

    fun getTotalStockVendidoPorProducto(idProducto: Int): Flow<Int?>

    @Query("SELECT SUM(CAST(cantidadVendida AS REAL) * precioUnitarioVenta) FROM sales WHERE productoId = :idProducto")

    fun getTotalDineroVendidoPorProducto(idProducto: Int): Flow<Double?>

} 