package com.bebidas.donjorge

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bebidas.donjorge.data.AppDatabase
import com.bebidas.donjorge.data.Producto
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProductActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var productId: Int = -1
    private var currentProduct: Producto? = null

    private lateinit var etName: TextInputEditText
    private lateinit var etStock: TextInputEditText
    private lateinit var etCost: TextInputEditText
    private lateinit var etPrice: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        db = AppDatabase.getDatabase(applicationContext)
        productId = intent.getIntExtra("PRODUCT_ID", -1)

        initViews()

        // 🟢 1. Configurar los Watchers para Costo y Precio
        setupCurrencyWatchers()

        loadProductData()

        btnSave.setOnClickListener {
            saveChanges()
        }
        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        etName = findViewById(R.id.et_edit_name)
        etStock = findViewById(R.id.et_edit_stock)
        etCost = findViewById(R.id.et_edit_cost)
        etPrice = findViewById(R.id.et_edit_price)
        btnSave = findViewById(R.id.btn_save_changes)
        btnCancel = findViewById(R.id.btn_cancel_edit)
    }

    // 🟢 Función para conectar el formateador
    private fun setupCurrencyWatchers() {
        etCost.addTextChangedListener(CurrencyTextWatcher(etCost))
        etPrice.addTextChangedListener(CurrencyTextWatcher(etPrice))
    }

    private fun loadProductData() {
        if (productId == -1) {
            Toast.makeText(this, "Error de ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            currentProduct = db.productoDao().getProductById(productId)

            if (currentProduct != null) {
                etName.setText(currentProduct!!.name)
                etStock.setText(currentProduct!!.stock.toString())

                val costoFormateable = (currentProduct!!.costPurchase * 100).toInt().toString()
                val precioFormateable = (currentProduct!!.priceSale * 100).toInt().toString()

                etCost.setText(costoFormateable)
                etPrice.setText(precioFormateable)
            }
        }
    }

    private fun getDoubleFromCurrencyInput(inputStr: String): Double {
        val cleanedString = inputStr.filter { it.isDigit() }
        if (cleanedString.isEmpty()) return 0.0
        return cleanedString.toDouble() / 100.0
    }

    private fun saveChanges() {
        val newName = etName.text.toString()
        val newStockStr = etStock.text.toString()

        val newCost = getDoubleFromCurrencyInput(etCost.text.toString())
        val newPrice = getDoubleFromCurrencyInput(etPrice.text.toString())

        if (newName.isEmpty() || newStockStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPrice <= 0) {
            Toast.makeText(this, "El precio debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }

        val newStock = newStockStr.toIntOrNull() ?: 0

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val productoActualizado = currentProduct!!.copy(
                        name = newName,
                        stock = newStock,
                        costPurchase = newCost,
                        priceSale = newPrice
                    )
                    db.productoDao().updateProduct(productoActualizado)
                }
                Toast.makeText(this@EditProductActivity, "¡Actualizado!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@EditProductActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}