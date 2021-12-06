package com.hosnimal.ui.detail_product

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
            descriptionText.text = product?.description
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

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }
}