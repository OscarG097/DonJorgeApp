package com.bebidas.donjorge

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bebidas.donjorge.data.AppDatabase
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SalesReportActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var reportAdapter: SalesReportAdapter
    private lateinit var rvReport: RecyclerView
    private lateinit var tvFilterStatus: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnClearFilter: ImageButton
    private lateinit var toolbar: MaterialToolbar

    private var observationJob: Job? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_report)

        db = AppDatabase.getDatabase(applicationContext)

        initViews()
        setupRecyclerView()
        setupListeners()
        observeAllSales()
    }

    private fun initViews() {
        rvReport = findViewById(R.id.rv_sales_report)
        tvFilterStatus = findViewById(R.id.tv_filter_status)
        btnPickDate = findViewById(R.id.btn_pick_date)
        btnClearFilter = findViewById(R.id.btn_clear_filter)
        toolbar = findViewById(R.id.toolbar_report)

        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        reportAdapter = SalesReportAdapter()
        rvReport.layoutManager = LinearLayoutManager(this)
        rvReport.adapter = reportAdapter
    }

    private fun setupListeners() {
        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        btnClearFilter.setOnClickListener {
            clearFilter()
        }
    }

    private fun observeAllSales() {
        observationJob?.cancel()
        observationJob = lifecycleScope.launch {
            db.salesDao().getSalesReport().collect { list ->
                reportAdapter.setData(list)
                tvFilterStatus.text = "Mostrando: Todas las ventas"
                btnClearFilter.visibility = View.GONE
            }
        }
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDate = sdf.format(calendar.time)
            
            filterByDate(selectedDate)
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun filterByDate(date: String) {
        observationJob?.cancel()
        observationJob = lifecycleScope.launch {
            db.salesDao().getSalesReportByDate(date).collect { list ->
                reportAdapter.setData(list)
                
                // Formatear fecha para el texto de estado
                val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val sdfOutput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = try {
                    sdfOutput.format(sdfInput.parse(date)!!)
                } catch (e: Exception) {
                    date
                }
                
                tvFilterStatus.text = "Filtrado por: $formattedDate"
                btnClearFilter.visibility = View.VISIBLE
            }
        }
    }

    private fun clearFilter() {
        observeAllSales()
    }
}
