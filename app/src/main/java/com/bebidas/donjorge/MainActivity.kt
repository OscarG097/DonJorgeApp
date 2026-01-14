package com.bebidas.donjorge

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onTileClicked(view: View) {
        when (view.id) {
            R.id.btn_ingresos -> {
                startActivity(Intent(this, NewProduct::class.java))
            }

            R.id.btn_productos -> {
                startActivity(Intent(this, ListProductsActivity::class.java))
            }

            R.id.btn_registrar_venta -> {
                startActivity(Intent(this, RegistrySaleActivity::class.java))
            }

            else -> {
                val nextActivityTitle: String = when (view.id) {
                    R.id.btn_ganancias -> "Ganancias"
                    R.id.btn_exportar -> "Exportar Datos (CSV)"
                    R.id.btn_reportes -> "Reportes"
                    else -> "Pantalla en Desarrollo"
                }

                Toast.makeText(
                    this,
                    "Próximamente: $nextActivityTitle",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}