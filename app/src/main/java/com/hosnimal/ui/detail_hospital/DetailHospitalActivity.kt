package com.hosnimal.ui.detail_hospital

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.adapter.ImageSliderAdapter
import com.hosnimal.databinding.ActivityDetailHospitalBinding
import com.hosnimal.model.Hospital
import com.hosnimal.model.User
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.register.RegisterActivity
import java.time.OffsetTime

class DetailHospitalActivity : AppCompatActivity() {
    // Binding
    private var _binding: ActivityDetailHospitalBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // ViewModel
    private lateinit var viewModel: DetailHospitalViewModel

    // Data Transaction
    private var qty = 1
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receive data
        val hospital = intent.getParcelableExtra<Hospital>(EXTRA_HOSPITAL)

        // Setting Preferences
        preferences = UserPreferences.getInstance(dataStore)

        // Set ViewModel
        viewModel = ViewModelProvider(this, DetailHospitalViewModelFactory(application, preferences))[DetailHospitalViewModel::class.java]

        // Observe User Data
        viewModel.getUserSetting().observe(this, {
            user = it

            // Check user login
            if (!viewModel.isRegistered(it.email) and it.email.isNotEmpty()) {
                val intent = Intent(this@DetailHospitalActivity, RegisterActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
        })

        // Prepare View Binding
        _binding = ActivityDetailHospitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set images of hospital
        hospital?.let { hospitalData ->
            val adapter = ImageSliderAdapter(hospitalData.images)
            binding.images.adapter = adapter
            if (adapter.itemCount > 1) {
                binding.dotsIndicator.setViewPager2(binding.images)
            } else {
                binding.dotsIndicator.visibility = View.GONE
            }
        }

        // Set Data Hospital
        with(binding) {
            name.text = hospital?.name
            location.text = hospital?.location
        }

        // Observe Data Hospital
        hospital?.let { hospitalData ->
            viewModel.getHospital(hospitalData.id).observe(this, { data ->
                setOpenClosed(data.open, data.close)
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setOpenClosed(openTime: OffsetTime, closedTime: OffsetTime) {
        val isOpen = openTime.isBefore(OffsetTime.now()) and closedTime.isAfter(OffsetTime.now())
        with(binding) {
            statusOpen.visibility = if (isOpen) View.VISIBLE else View.INVISIBLE
            statusClosed.visibility = if (isOpen) View.GONE else View.VISIBLE
            btnReservationOpen.visibility = if (isOpen) View.VISIBLE else View.INVISIBLE
            btnReservationClosed.visibility = if (isOpen) View.GONE else View.VISIBLE
            day.visibility = if (isOpen) View.VISIBLE else View.GONE
            btnDatePicker.visibility = if (isOpen) View.VISIBLE else View.GONE
            time.visibility = if (isOpen) View.VISIBLE else View.GONE
            btnTimePicker.visibility = if (isOpen) View.VISIBLE else View.GONE
        }
    }

    companion object {
        const val EXTRA_HOSPITAL = "extra_hospital"
    }
}