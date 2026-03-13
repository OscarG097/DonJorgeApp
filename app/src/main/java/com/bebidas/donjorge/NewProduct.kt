package com.bebidas.donjorge

import android.annotation.SuppressLint
import android.view.View
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bebidas.donjorge.data.AppDatabase
import com.bebidas.donjorge.data.ComboDetail
import com.bebidas.donjorge.data.Producto
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewProduct : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var toolbar: MaterialToolbar

    private lateinit var spinnerProductBase: Spinner
    private lateinit var etStock: TextInputEditText
    private lateinit var etCostPurchase: TextInputEditText
    private lateinit var etPriceSale: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var ivImageProduct: ImageView
    private lateinit var switchEsCombo: Switch
    private lateinit var llNormalProductInputs: LinearLayout
    private lateinit var llComboComponentsContainer: LinearLayout
    private lateinit var btnAddComponent: Button
    private lateinit var spinnerComboImageBase: Spinner
    private lateinit var llComboInputs: LinearLayout
    private lateinit var ivComboImage: ImageView
    private lateinit var etComboName: TextInputEditText
    private lateinit var tilComboName: TextInputLayout
    private lateinit var llComboCreationInputs: LinearLayout
    private lateinit var allIndividualProducts: List<Producto>
    private val componentViews = mutableListOf<View>()

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
        "Botella de agua" to R.drawable.botella_agua,

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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        db = AppDatabase.getDatabase(applicationContext)

        initViews()
        setupToolbar()
        setupSpinners()
        setupInputWatchers()
        setupComboLogic()

        btnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_new_product)
        spinnerProductBase = findViewById(R.id.spinner_product_base)
        etStock = findViewById(R.id.et_stock)
        etCostPurchase = findViewById(R.id.et_cost_purchase)
        etPriceSale = findViewById(R.id.et_price_sale)
        btnSave = findViewById(R.id.btn_save_product)
        ivImageProduct = findViewById(R.id.iv_product_imagen)
        switchEsCombo = findViewById(R.id.switch_es_combo)
        tilComboName = findViewById(R.id.til_combo_name)
        etComboName = findViewById(R.id.et_combo_name)
        ivComboImage = findViewById(R.id.iv_combo_imagen)
        llNormalProductInputs = findViewById(R.id.ll_normal_inputs)
        llComboCreationInputs = findViewById(R.id.ll_combo_creation_inputs)
        llComboComponentsContainer = findViewById(R.id.ll_combo_components_container)
        btnAddComponent = findViewById(R.id.btn_add_component)
        llComboInputs = findViewById(R.id.ll_combo_inputs)
        spinnerComboImageBase = findViewById(R.id.spinner_combo_image_base)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupInputWatchers() {
        etCostPurchase.addTextChangedListener(CurrencyTextWatcher(etCostPurchase))
        etPriceSale.addTextChangedListener(CurrencyTextWatcher(etPriceSale))
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

        ArrayAdapter.createFromResource(
            this,
            R.array.combo_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerComboImageBase.adapter = adapter
        }

        spinnerProductBase.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!switchEsCombo.isChecked) {
                    val selectedProduct = parent.getItemAtPosition(position).toString()
                    mostrarImagenSeleccionada(selectedProduct, isCombo = false)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerComboImageBase.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (switchEsCombo.isChecked) {
                    val selectedComboImage = parent.getItemAtPosition(position).toString()
                    mostrarImagenSeleccionada(selectedComboImage, isCombo = true)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun mostrarImagenSeleccionada(nameProduct: String, isCombo: Boolean) {
        val drawableId = productoImagenMap[nameProduct] ?: R.drawable.default_placeholder
        if (isCombo) {
            ivComboImage.setImageResource(drawableId)
        } else {
            ivImageProduct.setImageResource(drawableId)
        }
    }

    private fun getDoubleFromCurrencyInput(inputStr: String): Double? {
        val cleanedString = inputStr.filter { it.isDigit() }
        if (cleanedString.isEmpty()) return null
        return cleanedString.toDouble() / 100.0
    }

    private fun setupComboLogic() {
        lifecycleScope.launch {
            val allProductsFlow = db.productoDao().listAllProduct().firstOrNull() ?: emptyList()
            allIndividualProducts = allProductsFlow.filter { !it.isCombo }
            if (switchEsCombo.isChecked && componentViews.isEmpty()) {
                addComponentRow()
            }
        }

        switchEsCombo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                llNormalProductInputs.visibility = View.GONE
                llComboInputs.visibility = View.VISIBLE
                llComboCreationInputs.visibility = View.VISIBLE
                val selectedComboImage = spinnerComboImageBase.selectedItem.toString()
                mostrarImagenSeleccionada(selectedComboImage, isCombo = true)
                if (componentViews.isEmpty()) {
                    addComponentRow()
                }
            } else {
                llNormalProductInputs.visibility = View.VISIBLE
                llComboInputs.visibility = View.GONE
                llComboCreationInputs.visibility = View.GONE
                val selectedProduct = spinnerProductBase.selectedItem.toString()
                mostrarImagenSeleccionada(selectedProduct, isCombo = false)
            }
            etStock.setText("")
            etCostPurchase.setText("")
        }

        btnAddComponent.setOnClickListener {
            addComponentRow()
        }
    }

    private fun addComponentRow() {
        if (!::allIndividualProducts.isInitialized || allIndividualProducts.isEmpty()) {
            Toast.makeText(this, "Cargando productos.", Toast.LENGTH_SHORT).show()
            return
        }
        val componentView = LayoutInflater.from(this).inflate(R.layout.item_combo_component, llComboComponentsContainer, false)
        val spinner = componentView.findViewById<Spinner>(R.id.spinner_combo_product)
        val etQuantity = componentView.findViewById<EditText>(R.id.et_combo_quantity)
        val btnRemove = componentView.findViewById<ImageButton>(R.id.btn_remove_component)
        val productNames = allIndividualProducts.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        btnRemove.setOnClickListener {
            llComboComponentsContainer.removeView(componentView)
            componentViews.remove(componentView)
            if (switchEsCombo.isChecked && componentViews.isEmpty()) {
                addComponentRow()
            }
        }
        llComboComponentsContainer.addView(componentView)
        componentViews.add(componentView)
        etQuantity.requestFocus()
    }

    private fun saveProduct() {
        val isComboMode = switchEsCombo.isChecked
        val priceSaleStr = etPriceSale.text.toString()
        val priceSale = getDoubleFromCurrencyInput(priceSaleStr)
        val name: String = if (isComboMode) {
            etComboName.text.toString().trim()
        } else {
            spinnerProductBase.selectedItem.toString()
        }
        if (name.isEmpty() || (!isComboMode && name.contains("Seleccione un Producto Base", ignoreCase = true))) {
            Toast.makeText(this, "Debe ingresar un nombre válido.", Toast.LENGTH_SHORT).show()
            return
        }
        if (priceSale == null || priceSale <= 0) {
            Toast.makeText(this, "El precio de venta debe ser un número válido mayor a cero.", Toast.LENGTH_LONG).show()
            return
        }
        if (isComboMode) {
            saveCombo(name, priceSale)
        } else {
            saveNormalProduct(name, priceSale)
        }
    }

    private fun saveNormalProduct(name: String, priceSale: Double) {
        val stockStr = etStock.text.toString()
        val costoStr = etCostPurchase.text.toString()
        if (stockStr.isEmpty() || costoStr.isEmpty()) {
            Toast.makeText(this, "Debe completar Stock y Costo para un producto normal.", Toast.LENGTH_SHORT).show()
            return
        }
        val stock = stockStr.toIntOrNull()
        val costPurchase = getDoubleFromCurrencyInput(costoStr)
        if (stock == null || stock < 0 || costPurchase == null || costPurchase <= 0) {
            Toast.makeText(this, "Stock y costos deben ser números válidos y mayores a cero.", Toast.LENGTH_LONG).show()
            return
        }
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val existingProduct = db.productoDao().getProductByName(name)
                    if (existingProduct != null) {
                        if (existingProduct.isDeleted) {
                            val updatedProduct = existingProduct.copy(
                                stock = stock,
                                costPurchase = costPurchase,
                                priceSale = priceSale,
                                isDeleted = false,
                                isCombo = false
                            )
                            db.productoDao().updateProduct(updatedProduct)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@NewProduct, "¡Producto '$name' reactivado!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@NewProduct, "El producto ya existe activo.", Toast.LENGTH_LONG).show()
                            }
                            return@withContext
                        }
                    } else {
                        val newProduct = Producto(
                            name = name, stock = stock, costPurchase = costPurchase,
                            priceSale = priceSale, isCombo = false, isDeleted = false
                        )
                        db.productoDao().insertProduct(newProduct)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@NewProduct, "¡Producto guardado!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@NewProduct, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveCombo(comboName: String, priceSale: Double) {
        val recipe = mutableListOf<Pair<Int, Int>>()
        if (componentViews.isEmpty()) {
            Toast.makeText(this, "Agregue componentes al combo.", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedImageKey = spinnerComboImageBase.selectedItem.toString()
        for (view in componentViews) {
            val spinner = view.findViewById<Spinner>(R.id.spinner_combo_product)
            val etQuantity = view.findViewById<EditText>(R.id.et_combo_quantity)
            val selectedProductName = spinner.selectedItem?.toString() ?: ""
            val quantity = etQuantity.text.toString().toIntOrNull()
            if (selectedProductName.contains("Seleccione un Producto", ignoreCase = true) || quantity == null || quantity <= 0) {
                Toast.makeText(this, "Componente no válido.", Toast.LENGTH_SHORT).show()
                return
            }
            val productId = allIndividualProducts.find { it.name == selectedProductName }?.id
            if (productId == null) return
            recipe.add(Pair(productId, quantity))
        }

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val existingCombo = db.productoDao().getProductByName(comboName)
                    val comboId: Long = if (existingCombo != null) {
                        if (existingCombo.isDeleted) {
                            val updatedCombo = existingCombo.copy(
                                priceSale = priceSale, isCombo = true, isDeleted = false,
                                localImageRoute = selectedImageKey
                            )
                            db.productoDao().updateProduct(updatedCombo)
                            existingCombo.id.toLong()
                        } else {
                            return@withContext
                        }
                    } else {
                        val comboItem = Producto(
                            name = comboName, stock = 0, costPurchase = 0.0,
                            priceSale = priceSale, isCombo = true, isDeleted = false,
                            localImageRoute = selectedImageKey
                        )
                        db.productoDao().insertProduct(comboItem)
                    }

                    recipe.forEach { (productId, quantity) ->
                        val comboDetail = ComboDetail(
                            comboId = comboId.toInt(),
                            individualProductId = productId,
                            componentQuantity = quantity
                        )
                        db.comboDetailDao().insertComboDetail(comboDetail)
                    }
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@NewProduct, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
