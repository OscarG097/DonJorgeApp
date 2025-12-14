package data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.bebidas.donjorge.data.Producto


@Entity(
    tableName = "sales",
    foreignKeys = [ForeignKey(
        entity = Producto::class,
        parentColumns = ["id"],
        childColumns = ["productoId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sales(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val productoId: Int,
    val cantidadVendida: Int,
    val precioUnitarioVenta: Double,
    val fecha: Long = System.currentTimeMillis(),
    val paymentMethod: String
)