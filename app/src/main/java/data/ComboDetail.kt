package com.bebidas.donjorge.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "combo_details",
    primaryKeys = ["comboId", "individualProductId"],
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["comboId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["individualProductId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ComboDetail(
    val comboId: Int,
    val individualProductId: Int,
    val componentQuantity: Int
)