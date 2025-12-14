package com.bebidas.donjorge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert
    suspend fun insertProduct(product: Producto): Long

    @Query("SELECT * FROM productos ORDER BY name ASC")
    fun listAllProduct(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): Producto?

    @Update
    suspend fun updateProduct(product: Producto)
}