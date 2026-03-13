package com.bebidas.donjorge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bebidas.donjorge.data.AppDatabase
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListProductsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_products)

        db = AppDatabase.getDatabase(applicationContext)
        
        initViews()
        setupToolbar()
        setupRecyclerView()
        observeProducts()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rv_productos)
        toolbar = findViewById(R.id.toolbar_list)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList(), this)
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeProducts() {
        lifecycleScope.launch {
            db.productoDao().listAllProduct().collect { listProducts ->
                productAdapter.refreshList(listProducts)
            }
        }
    }
}
