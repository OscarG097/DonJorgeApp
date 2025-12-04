package com.bebidas.donjorge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert
    suspend fun insertarProducto(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodosProductos(): Flow<List<Producto>>

    @Update
    suspend fun actualizarProducto(producto: Producto)
}