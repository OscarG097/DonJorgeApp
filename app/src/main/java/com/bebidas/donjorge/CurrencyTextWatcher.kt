package com.bebidas.donjorge

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.ref.WeakReference
import java.text.NumberFormat
import java.util.Locale

class CurrencyTextWatcher(editText: EditText) : TextWatcher {

    private val editTextWeakReference: WeakReference<EditText> = WeakReference(editText)
    private val localeAR = Locale("es", "AR")
    private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(localeAR)

    private var isFormatting = false

    private fun cleanString(str: String): String {
        val cleanedString = str.filter { it.isDigit() }

        return if (cleanedString.isEmpty()) "0" else cleanedString
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable) {
        if (isFormatting) return

        isFormatting = true
        val editText = editTextWeakReference.get() ?: return
        val cleanedString = cleanString(s.toString())
        val value = cleanedString.toLongOrNull() ?: 0L

        val formattedString = currencyFormatter.format(value / 100.0)

        editText.setText(formattedString)
        editText.setSelection(formattedString.length)

        isFormatting = false
    }
}