package com.hosnimal.ui.detail_product

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.adapter.ImageSliderAdapter
import com.hosnimal.databinding.ActivityDetailProductBinding
import com.hosnimal.model.Product
import com.hosnimal.model.User
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.register.RegisterActivity
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

    // ViewModel
    private lateinit var viewModel: DetailProductViewModel

    // Data Transaction
    private var qty = 1
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receive data
        val product = intent.getParcelableExtra<Product>(EXTRA_PRODUCT)

        // Setting Preferences
        preferences = UserPreferences.getInstance(dataStore)

        // Set ViewModel
        viewModel = ViewModelProvider(this, DetailProductViewModelFactory(application, preferences))[DetailProductViewModel::class.java]

        // Observe User Data
        viewModel.getUserSetting().observe(this, {
            user = it
        })

        // Prepare View Binding
        _binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check user login
        if (viewModel.isRegistered(user.email)) {
            val intent = Intent(this@DetailProductActivity, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

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
            detailOrder.qty.text = qty.toString()
            descriptionText.text = product?.description
        }

        // Observe Stock of Product
        product?.let { productData ->
            viewModel.getProduct(productData.id).observe(this, { product ->
                setStock(product.stock)
            })
        }

        product?.let { productData ->
            // Setting Button Add
            binding.detailOrder.btnAdd.setOnClickListener {
                if (qty == productData.stock) {
                    showSnackBar(getString(R.string.error_product_buy_maximum))
                } else {
                    qty += 1
                    binding.detailOrder.qty.text = qty.toString()
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
                val pricing = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(productData.price.times(qty)).toString().replace(",00","")
                MaterialAlertDialogBuilder(it.context)
                    .setTitle(resources.getString(R.string.detail_product_dialog_title))
                    .setMessage(String.format(getString(R.string.detail_product_dialog_text), qty, productData.name, pricing))
                    .setNegativeButton(resources.getString(R.string.detail_product_dialog_negative)) { _, _ ->
                    }
                    .setPositiveButton(resources.getString(R.string.detail_product_dialog_positive)) { _, _ ->
                        viewModel.placeOrder(user.email, productData, qty)
                    }
                    .show()
            }
        }


        // Observe Notification
        viewModel.notificationText.observe(this, {
            it.getContentIfNotHandled()?.let { text ->
                showSnackBar(text)
            }
        })

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

    private fun setStock(stockProduct: Int) {
        with(binding) {
            with(detailOrder) {
                btnMinus.visibility = if (stockProduct < 1) View.GONE else View.VISIBLE
                btnAdd.visibility = if (stockProduct < 1) View.GONE else View.VISIBLE
                qty.visibility = if (stockProduct < 1) View.GONE else View.VISIBLE
            }
            stock.text = getString(R.string.detail_product_stock, stockProduct)
            stock.visibility = if (stockProduct < 1) View.GONE else View.VISIBLE
            btnOrder.visibility = if (stockProduct < 1) View.GONE else View.VISIBLE
            btnOrderStockEmpty.visibility = if (stockProduct < 1) View.VISIBLE else View.GONE
        }
    }

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }
}