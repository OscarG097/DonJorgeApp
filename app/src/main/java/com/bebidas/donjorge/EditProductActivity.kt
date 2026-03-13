package com.bebidas.donjorge

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bebidas.donjorge.data.AppDatabase
import com.bebidas.donjorge.data.Producto
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var btnDelete: Button
    private lateinit var btnCancel: Button
    
    // Elementos para Combos
    private lateinit var llImageSelection: LinearLayout
    private lateinit var ivProductImage: ImageView
    private lateinit var spinnerImage: Spinner
    private lateinit var tilStock: TextInputLayout
    private lateinit var tilCost: TextInputLayout

    private val productoImagenMap = mapOf(
        "Seleccionar Imagen del Combo" to R.drawable.default_placeholder,
        "Combo Fernet+Coca" to R.drawable.combo_fernet_coca,
        "Combo 1882+Coca" to R.drawable.combo_1882_coca,
        "Combo Gancia+Sprite" to R.drawable.combo_gancia_sprite,
        "Combo Vodka+Jugo" to R.drawable.combo_smir_jugo,
        "Combo Vodka+Speed" to R.drawable.combo_smir_speed,
        "Combo Campari+Jugo" to R.drawable.combo_campari_jugo,
        "Combo Cosecha+Speed" to R.drawable.combo_cocecha_speed,
        "Combo Gordons+Tonica" to R.drawable.combo_gordons_tonica,
        "Combo Balbo+Manaos" to R.drawable.combo_balbo_manaos
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        db = AppDatabase.getDatabase(applicationContext)
        productId = intent.getIntExtra("PRODUCT_ID", -1)

        initViews()
        setupCurrencyWatchers()
        setupImageSpinner()
        loadProductData()

        btnSave.setOnClickListener {
            saveChanges()
        }
        
        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
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
        btnDelete = findViewById(R.id.btn_delete_product)
        btnCancel = findViewById(R.id.btn_cancel_edit)
        
        llImageSelection = findViewById(R.id.ll_edit_image_selection)
        ivProductImage = findViewById(R.id.iv_edit_product_image)
        spinnerImage = findViewById(R.id.spinner_edit_image)
        tilStock = findViewById(R.id.til_edit_stock)
        tilCost = findViewById(R.id.til_edit_cost)
    }

    private fun setupCurrencyWatchers() {
        etCost.addTextChangedListener(CurrencyTextWatcher(etCost))
        etPrice.addTextChangedListener(CurrencyTextWatcher(etPrice))
    }

    private fun setupImageSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.combo_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerImage.adapter = adapter
        }

        spinnerImage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedImageKey = parent.getItemAtPosition(position).toString()
                val drawableId = productoImagenMap[selectedImageKey] ?: R.drawable.default_placeholder
                ivProductImage.setImageResource(drawableId)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
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
                etPrice.setText((currentProduct!!.priceSale * 100).toLong().toString())

                if (currentProduct!!.isCombo) {
                    // SI ES COMBO: Mostrar selector de imagen y ocultar Stock/Costo
                    llImageSelection.visibility = View.VISIBLE
                    tilStock.visibility = View.GONE
                    tilCost.visibility = View.GONE
                    
                    // Seleccionar la imagen actual en el spinner
                    val currentImageKey = currentProduct!!.localImageRoute
                    if (currentImageKey != null) {
                        val adapter = spinnerImage.adapter as ArrayAdapter<String>
                        val position = adapter.getPosition(currentImageKey)
                        if (position >= 0) spinnerImage.setSelection(position)
                    }
                } else {
                    // SI ES NORMAL: Mostrar Stock/Costo
                    tilStock.visibility = View.VISIBLE
                    tilCost.visibility = View.VISIBLE
                    llImageSelection.visibility = View.GONE
                    
                    etStock.setText(currentProduct!!.stock.toString())
                    etCost.setText((currentProduct!!.costPurchase * 100).toLong().toString())
                }
            }
        }
    }

    private fun getDoubleFromCurrencyInput(inputStr: String): Double {
        val cleanedString = inputStr.filter { it.isDigit() }
        if (cleanedString.isEmpty()) return 0.0
        return cleanedString.toDouble() / 100.0
    }

    private fun saveChanges() {
        val isCombo = currentProduct?.isCombo ?: false
        val newPrice = getDoubleFromCurrencyInput(etPrice.text.toString())
        
        if (newPrice <= 0) {
            Toast.makeText(this, "El precio debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val productoActualizado = if (isCombo) {
                        currentProduct!!.copy(
                            priceSale = newPrice,
                            localImageRoute = spinnerImage.selectedItem.toString()
                        )
                    } else {
                        val newStock = etStock.text.toString().toIntOrNull() ?: 0
                        val newCost = getDoubleFromCurrencyInput(etCost.text.toString())
                        currentProduct!!.copy(
                            stock = newStock,
                            costPurchase = newCost,
                            priceSale = newPrice
                        )
                    }
                    db.productoDao().updateProduct(productoActualizado)
                }
                Toast.makeText(this@EditProductActivity, "¡Producto actualizado!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@EditProductActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Producto")
        builder.setMessage("¿Estás seguro de que deseas eliminar '${currentProduct?.name}'? El historial de ventas se conservará.")
        
        builder.setPositiveButton("ELIMINAR") { _, _ ->
            deleteProductLogically()
        }
        
        builder.setNegativeButton("Cancelar", null)
        
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.RED)
    }

    private fun deleteProductLogically() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    currentProduct?.let {
                        db.productoDao().logicalDelete(it.id)
                    }
                }
                Toast.makeText(this@EditProductActivity, "Producto ocultado", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@EditProductActivity, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
