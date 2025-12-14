package com.bebidas.donjorge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val category: String = "No Asignada",

    val costPurchase: Double,

    val priceSale: Double,

    val stock: Int,

    val localImageRoute: String? = null,

    val gananciaUnidad: Double = priceSale - costPurchase,

    val isCombo: Boolean = false
)