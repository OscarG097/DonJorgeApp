package com.bebidas.donjorge

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bebidas.donjorge.data.AppDatabase
import com.bebidas.donjorge.data.Producto
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.ImageView
import android.view.View
import android.widget.AdapterView

class NewProduct : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var spinnerProductBase: Spinner
//    private lateinit var spinnerCategoria: Spinner
    private lateinit var etStock: TextInputEditText
    private lateinit var etCostPurchase: TextInputEditText
    private lateinit var etPriceSale: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var ivImageProduct: ImageView

    private val productoImagenMap = mapOf(
        "Seleccione un Producto Base" to R.drawable.default_placeholder,
        "Coca-Cola 1.75Lts" to R.drawable.coca,
        "Coca-Cola 2.25Lts" to R.drawable.coca,
        "Botella Mananos" to R.drawable.manaos,
        "Botella Smirnoff saborizado" to R.drawable.botella_smir_sab,
        "Botella Smirnoff" to R.drawable.botella_smirnoff,
        "Lata cerveza Quilmes" to R.drawable.lata_quilmes,
        "Lata cerveza Brahma" to R.drawable.lata_brahma,
        "Lata cerveza Budweiser" to R.drawable.lata_budweiser,
        "Lata cerveza Heineken 710ml" to R.drawable.lata_heineken710,
        "Lata Smirnoff" to R.drawable.lata_smir,
        "Botella de agua" to R.drawable.botella_agua
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        db = AppDatabase.getDatabase(applicationContext)

        spinnerProductBase = findViewById(R.id.spinner_product_base)
//        spinnerCategoria = findViewById(R.id.spinner_categoria)
        etStock = findViewById(R.id.et_stock)
        etCostPurchase = findViewById(R.id.et_cost_purchase)
        etPriceSale = findViewById(R.id.et_price_sale)
        btnSave = findViewById(R.id.btn_save_product)
        ivImageProduct = findViewById(R.id.iv_product_imagen)

        setupSpinners()

        btnSave.setOnClickListener {
            saveProduct()
        }
        etCostPurchase.addTextChangedListener(CurrencyTextWatcher(etCostPurchase))
        etPriceSale.addTextChangedListener(CurrencyTextWatcher(etPriceSale))

//        etCostoCompra.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
//        etPrecioVenta.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    private fun setupSpinners() {
        ArrayAdapter.createFromResource(
            this,
            R.array.products_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerProductBase.adapter = adapter
        }

        spinnerProductBase.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedProduct = parent.getItemAtPosition(position).toString()
                mostrarImagenSeleccionada(selectedProduct)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hace nada si no se selecciona nada
            }
        }
//
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.categories_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinnerCategoria.adapter = adapter
//        }
    }

    private fun mostrarImagenSeleccionada(nameProduct: String) {
        val drawableId = productoImagenMap[nameProduct] ?: R.drawable.default_placeholder

        ivImageProduct.setImageResource(drawableId)
    }

    private fun getDoubleFromCurrencyInput(inputStr: String): Double? {
        val cleanedString = inputStr.filter { it.isDigit() }

        if (cleanedString.isEmpty()) return null

        return cleanedString.toDouble() / 100.0
    }
    private fun saveProduct() {
        val name = spinnerProductBase.selectedItem.toString()

        if (name.contains("Seleccione un Producto Base", ignoreCase = true)) {
            Toast.makeText(this, "Debe seleccionar un producto base.", Toast.LENGTH_SHORT).show()
            return
        }

        val stockStr = etStock.text.toString()
        val costoStr = etCostPurchase.text.toString()
        val ventaStr = etPriceSale.text.toString()

        if (stockStr.isEmpty() || costoStr.isEmpty() || ventaStr.isEmpty()) {
            Toast.makeText(this, "Debe completar todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val stock = stockStr.toIntOrNull()
        val costPurchase = getDoubleFromCurrencyInput(costoStr)
        val priceSale = getDoubleFromCurrencyInput(ventaStr)

        if (stock == null || stock < 0 || costPurchase == null || costPurchase <= 0 || priceSale == null || priceSale <= 0) {
            Toast.makeText(this, "Stock y precios deben ser números válidos y mayores a cero.", Toast.LENGTH_LONG).show()
            return
        }

        val newProduct = Producto(
            name = name,
            stock = stock,
            costPurchase = costPurchase,
            priceSale = priceSale
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.productoDao().insertProduct(newProduct)
            }
            Toast.makeText(this@NewProduct, "¡Producto '$name' guardado con éxito!", Toast.LENGTH_LONG).show()

            finish()
        }
    }
}