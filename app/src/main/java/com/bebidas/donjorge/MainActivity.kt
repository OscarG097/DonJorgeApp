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
        val buttonId = view.id

        if (buttonId == R.id.btn_ingresos) {
            val intent = Intent(this, NewProduct::class.java)
            startActivity(intent)
            return
        }

        if (buttonId == R.id.btn_productos) {
            val intent = Intent(this, ListProductsActivity::class.java)
            startActivity(intent)
            return
        }

        val nextActivityTitle: String = when (buttonId) {
            R.id.btn_registrar_venta -> "Registrar Venta"
            R.id.btn_ganancias -> "Ganancias"
            R.id.btn_exportar -> "Exportar Datos (CSV)"
            R.id.btn_reportes -> "Reportes"
            else -> "Pantalla en Desarrollo"
        }

        // 🟢 Solo se muestra el Toast para las funciones AÚN NO IMPLEMENTADAS.
        Toast.makeText(
            this,
            "¡Mosaico presionado! Navegando a: $nextActivityTitle",
            Toast.LENGTH_SHORT
        ).show()
    }

}