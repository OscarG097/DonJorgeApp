package com.bebidas.donjorge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bebidas.donjorge.data.AppDatabase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListProductsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var productoAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_products)

        // 1. Inicializar BD y RecyclerView
        db = AppDatabase.getDatabase(applicationContext)
        recyclerView = findViewById(R.id.rv_productos)

        productoAdapter = ProductAdapter(emptyList())

        recyclerView.adapter = productoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        observeProductos()
    }

    private fun observeProductos() {
        lifecycleScope.launch {
            db.productoDao().listAllProduct().collect { listProducts ->
                productoAdapter.actualizarLista(listProducts)
            }
        }
    }
}