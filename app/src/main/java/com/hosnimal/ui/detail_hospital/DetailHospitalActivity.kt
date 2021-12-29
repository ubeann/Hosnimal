package com.hosnimal.ui.detail_hospital

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.adapter.ImageSliderAdapter
import com.hosnimal.adapter.ReservationHospitalAdapter
import com.hosnimal.databinding.ActivityDetailHospitalBinding
import com.hosnimal.model.Hospital
import com.hosnimal.model.User
import com.hosnimal.model.relational.HospitalReservation
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.maps.MapsActivity
import com.hosnimal.ui.register.RegisterActivity
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

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

    // Data Reservation
    private var dayEpoch: Long = OffsetDateTime.now().toInstant().toEpochMilli()
    private var timeEpoch: OffsetTime = OffsetTime.now()
    private lateinit var user: User
    private var userId: Int = 0

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

            // Get User Id
            userId = if (it.email.isNotEmpty()) viewModel.getUserIdByEmail(it.email) else 0
        })

        // Prepare View Binding
        _binding = ActivityDetailHospitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set list of reservation
        with(binding.reservationList) {
            layoutManager = LinearLayoutManager(this@DetailHospitalActivity)
            setHasFixedSize(true)
        }

        hospital?.let { hospitalData ->
            // Set images of hospital
            val adapter = ImageSliderAdapter(hospitalData.images)
            binding.images.adapter = adapter
            if (adapter.itemCount > 1) {
                binding.dotsIndicator.setViewPager2(binding.images)
            } else {
                binding.dotsIndicator.visibility = View.GONE
            }

            // Observe Data Hospital
            viewModel.getHospital(hospitalData.id).observe(this, { data ->
                setOpenClosed(data.open, data.close)
            })

            // Setting Reservation Button
            binding.btnReservationOpen.setOnClickListener {
                // Check are datetime is free
                if (viewModel.isReservationFilled(hospitalData.id, OffsetDateTime.ofInstant(Instant.ofEpochMilli(dayEpoch), ZoneId.systemDefault()), timeEpoch)) {
                    showSnackBar(getString(R.string.error_reservation_filled))
                } else {
                    // Format
                    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("in", "ID"))
                    val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID"))

                    // Alert Dialog
                    MaterialAlertDialogBuilder(it.context)
                        .setTitle(resources.getString(R.string.detail_hospital_dialog_title))
                        .setMessage(String.format(getString(R.string.detail_hospital_dialog_text), dateFormat.format(dayEpoch), timeEpoch.format(timeFormat), timeEpoch.plusHours(1L).format(timeFormat)))
                        .setNegativeButton(resources.getString(R.string.detail_hospital_dialog_negative)) { _, _ ->
                        }
                        .setPositiveButton(resources.getString(R.string.detail_hospital_dialog_positive)) { _, _ ->
                            viewModel.placeReservation(user.email, hospitalData, OffsetDateTime.ofInstant(Instant.ofEpochMilli(dayEpoch), ZoneId.systemDefault()), timeEpoch)
                        }
                        .show()
                }
            }

            // Observe reservation
            viewModel.getHospitalReservation(hospitalData.id).observe(this, { listReservation ->
                showReservation(listReservation)
            })
        }

        // Set Data Hospital
        with(binding) {
            name.text = hospital?.name
            location.text = hospital?.location
            day.editText?.setText(SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("in", "ID")).format(dayEpoch))
            time.editText?.setText(timeEpoch.format(DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID"))))
        }

        // Date Picker Click Listener
        binding.btnDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val start = LocalDateTime
                .of(calendar.get(Calendar.YEAR) - 0, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH) - 0, 0, 0, 0,)
                .atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC))
                .toInstant()
                .toEpochMilli()
            val end = LocalDateTime
                .of(calendar.get(Calendar.YEAR) - 0, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH) - 0, 0, 0, 0,)
                .atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC))
                .plusWeeks(2)
                .toInstant()
                .toEpochMilli()
            val constraintsBuilder = CalendarConstraints.Builder()
                .setStart(start)
                .setEnd(end)
                .setOpenAt(dayEpoch)
                .setValidator(CompositeDateValidator.allOf(listOf(DateValidatorPointForward.from(start), DateValidatorPointBackward.before(end))))
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.input_reservation_day_picker))
                .setSelection(dayEpoch)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()
            picker.show(this.supportFragmentManager, DATE_PICKER_TAG)
            picker.addOnPositiveButtonClickListener {
                // Save to data
                dayEpoch = it

                // Preview
                val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("in", "ID"))
                binding.day.editText?.setText(dateFormat.format(it))
            }
        }

        // Time Picker Click Listener
        binding.btnTimePicker.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(timeEpoch.hour)
                .setMinute(timeEpoch.minute)
                .setTitleText(getString(R.string.input_reservation_time_picker))
                .build()
            picker.show(this.supportFragmentManager, TIME_PICKER_TAG)
            picker.addOnPositiveButtonClickListener {
                // Save to data
                timeEpoch = OffsetTime.of(picker.hour, picker.minute, 0, 0, OffsetTime.now().offset)

                // Preview
                binding.time.editText?.setText(timeEpoch.format(DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID"))))
            }
        }

        // Setting Map Button
        binding.btnMap.setOnClickListener {
            hospital?.let { dataHospital ->
                val intent = Intent(this@DetailHospitalActivity, MapsActivity::class.java)
                intent.putExtra(MapsActivity.EXTRA_HOSPITAL, dataHospital)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
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

    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showReservation(listReservation: List<HospitalReservation>) {
        with(binding) {
            reservationList.visibility = if (listReservation.isEmpty()) View.GONE else View.VISIBLE
            reservationEmpty.visibility = if (listReservation.isEmpty()) View.VISIBLE else View.GONE
        }
        if (listReservation.isNotEmpty()) {
            val adapterReservation = ReservationHospitalAdapter(listReservation, userId)
            binding.reservationList.adapter = adapterReservation
            adapterReservation.setOnClickCallback(
                object : ReservationHospitalAdapter.OnBtnClickCallback {
                    override fun onBtnClicked(data: HospitalReservation) {
                        cancelReservation(data)
                    }
                }
            )
        }
    }

    private fun cancelReservation(reservation: HospitalReservation) {
        // Format
        val dateFormat = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("in", "ID"))
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID"))

        // Alert Dialog
        MaterialAlertDialogBuilder(this@DetailHospitalActivity)
            .setTitle(resources.getString(R.string.fragment_map_cancel_dialog_title))
            .setMessage(String.format(getString(R.string.fragment_map_cancel_dialog_text), reservation.detailReservation.start.format(dateFormat), reservation.detailReservation.start.format(timeFormat), reservation.detailReservation.end.format(timeFormat)))
            .setNegativeButton(resources.getString(R.string.fragment_map_cancel_dialog_negative)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.fragment_map_cancel_dialog_positive)) { _, _ ->
                viewModel.cancelReservation(reservation)
            }
            .show()
    }

    companion object {
        const val EXTRA_HOSPITAL = "extra_hospital"
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_TAG = "TimePicker"
    }
}