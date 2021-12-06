package com.hosnimal.ui.detail_product

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.adapter.ImageSliderAdapter
import com.hosnimal.databinding.ActivityDetailProductBinding
import com.hosnimal.model.Product
import com.hosnimal.preferences.UserPreferences
import java.text.NumberFormat
import java.util.*

class DetailProductActivity : AppCompatActivity() {
    // Binding
    private var _binding: ActivityDetailProductBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // Data Transaction
    private var qty = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive data
        val product = intent.getParcelableExtra<Product>(EXTRA_PRODUCT)

        // Setting Preferences
        preferences = UserPreferences.getInstance(dataStore)

        // Set images of product
        product?.let { productData ->
            val adapter = ImageSliderAdapter(productData.images)
            binding.images.adapter = adapter
            if (adapter.itemCount > 1) {
                binding.dotsIndicator.setViewPager2(binding.images)
            } else {
                binding.dotsIndicator.visibility = View.GONE
            }
        }

        // Set Data Product
        with(binding) {
            name.text = product?.name ?: getString(R.string.error_product_not_found)
            category.text = product?.category
            price.text = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(product?.price).toString().replace(",00","")
            stock.text = getString(R.string.detail_product_stock, product?.stock)
            detailOrder.qty.text = qty.toString()
            descriptionText.text = product?.description
        }

        // Setting Button Add
        product?.let { productData ->
            binding.detailOrder.btnAdd.setOnClickListener {
                if (qty == productData.stock) {
                    showSnackBar(getString(R.string.error_product_buy_maximum))
                } else {
                    qty += 1
                    binding.detailOrder.qty.text = qty.toString()
                }
            }
        }

        // Setting Button Minus
        binding.detailOrder.btnMinus.setOnClickListener {
            if (qty == 1) {
                showSnackBar(getString(R.string.error_product_buy_minimum))
            } else {
                qty -= 1
                binding.detailOrder.qty.text = qty.toString()
            }
        }

        // Setting Button Order
        binding.btnOrder.setOnClickListener {
            val pricing = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(product?.price?.times(qty)).toString().replace(",00","")
            MaterialAlertDialogBuilder(it.context)
                .setTitle(resources.getString(R.string.detail_product_dialog_title))
                .setMessage(String.format(getString(R.string.detail_product_dialog_text), qty, product?.name, pricing))
                .setNegativeButton(resources.getString(R.string.detail_product_dialog_negative)) { _, _ ->
                }
                .setPositiveButton(resources.getString(R.string.detail_product_dialog_positive)) { _, _ ->
                    showSnackBar(String.format(getString(R.string.detail_product_success), qty, product?.name))
                }
                .show()
        }

        // Setting Button Back
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }
}