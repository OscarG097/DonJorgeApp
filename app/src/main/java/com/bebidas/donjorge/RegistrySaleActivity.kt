package com.bebidas.donjorge

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bebidas.donjorge.data.AppDatabase
import com.bebidas.donjorge.data.SaleRequest
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class RegistrySaleActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var saleAdapter: SaleAdapter
    private lateinit var rvProducts: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var searchView: SearchView
    private lateinit var btnConfirmSale: android.view.View
    private lateinit var toolbar: MaterialToolbar

    private var currentTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registry_sale)

        db = AppDatabase.getDatabase(applicationContext)

        initViews()
        setupToolbar()
        setupRecyclerView()
        loadProducts()
        setupListeners()
    }

    private fun initViews() {
        rvProducts = findViewById(R.id.rv_products_sale)
        tvTotalAmount = findViewById(R.id.tv_total_amount)
        searchView = findViewById(R.id.search_view_products)
        btnConfirmSale = findViewById(R.id.btn_confirm_sale)
        toolbar = findViewById(R.id.toolbar_registry)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        saleAdapter = SaleAdapter(emptyList(), this) { newTotal ->
            currentTotal = newTotal
            updateTotalUI()
        }
        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = saleAdapter
    }

    private fun updateTotalUI() {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
        tvTotalAmount.text = formatter.format(currentTotal)

        if (currentTotal > 0) {
            btnConfirmSale.setBackgroundColor(Color.parseColor("#4CAF50")) // Verde
            tvTotalAmount.setTextColor(Color.WHITE)
        } else {
            btnConfirmSale.setBackgroundColor(Color.parseColor("#E0E0E0")) // Gris
            tvTotalAmount.setTextColor(Color.BLACK)
        }
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            db.productoDao().listAllProduct().collect { allProducts ->

                val allRecipes = withContext(Dispatchers.IO) {
                    db.comboDetailDao().getAllComboDetails()
                }

                val stockMap = allProducts.associate { it.id to it.stock }

                val availableProducts = allProducts.filter { producto ->
                    if (!producto.isCombo) {
                        producto.stock > 0
                    } else {
                        val myIngredients = allRecipes.filter { it.comboId == producto.id }

                        if (myIngredients.isEmpty()) return@filter false

                        val esPosibleArmarlo = myIngredients.all { ingrediente ->
                            val stockRealIngrediente = stockMap[ingrediente.individualProductId] ?: 0
                            stockRealIngrediente >= ingrediente.componentQuantity
                        }

                        esPosibleArmarlo
                    }
                }

                saleAdapter.updateData(availableProducts)
            }
        }
    }

    private fun setupListeners() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                saleAdapter.filter(newText ?: "")
                return true
            }
        })

        btnConfirmSale.setOnClickListener {
            if (currentTotal > 0) {
                showPaymentMethodDialog()
            } else {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPaymentMethodDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_payment, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvDialogTotal = dialogView.findViewById<TextView>(R.id.tv_dialog_final_total)

        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
        tvDialogTotal.text = formatter.format(currentTotal)

        val btnCash = dialogView.findViewById<Button>(R.id.btn_pay_cash)
        val btnTransfer = dialogView.findViewById<Button>(R.id.btn_pay_transfer)

        btnCash.setOnClickListener {
            dialog.dismiss()
            processSaleAndStock(isCash = true)
        }

        val btnTransferListener = View.OnClickListener {
            dialog.dismiss()
            processSaleAndStock(isCash = false)
        }
        btnTransfer.setOnClickListener(btnTransferListener)

        dialog.show()
    }

    private fun processSaleAndStock(isCash: Boolean) {
        val cartItems = saleAdapter.getCartItems()
        val paymentMethodString = if (isCash) "Efectivo" else "Transferencia"

        // Mapeamos los items del carrito a SaleRequest para la transacción atómica
        val saleRequests = cartItems.map { (producto, cantidad) ->
            SaleRequest(
                productId = producto.id,
                quantity = cantidad,
                paymentMethod = paymentMethodString
            )
        }

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // LLAMADA ATÓMICA AL DAO
                    db.productoDao().executeSaleAtomics(saleRequests)
                }

                withContext(Dispatchers.Main) {
                    showSuccessDialog()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrySaleActivity, "Error al procesar venta: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showSuccessDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnClose = dialogView.findViewById<Button>(R.id.btn_close_success)

        btnClose.setOnClickListener {
            dialog.dismiss()
            saleAdapter.clearCart()
            searchView.setQuery("", false)
            searchView.clearFocus()
        }

        dialog.show()
    }
}
