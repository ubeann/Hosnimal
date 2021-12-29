package com.hosnimal.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.adapter.HospitalAdapter
import com.hosnimal.adapter.ReservationUserAdapter
import com.hosnimal.databinding.FragmentMapBinding
import com.hosnimal.model.Hospital
import com.hosnimal.model.relational.UserReservation
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.detail_hospital.DetailHospitalActivity
import com.hosnimal.ui.main.MainViewModel
import com.hosnimal.ui.main.MainViewModelFactory
import java.time.format.DateTimeFormatter
import java.util.*

class MapFragment : Fragment() {
    // Binding
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // ViewModel
    private lateinit var viewModel: MainViewModel

    // Data Reservation
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setting Preferences
        preferences = UserPreferences.getInstance(requireActivity().dataStore)

        // Set ViewModel
        viewModel = ViewModelProvider(this, MainViewModelFactory(requireActivity().application, preferences))[MainViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe User Data
        viewModel.getUserSetting().observe(viewLifecycleOwner, { user ->
            userId = if (user.email.isNotEmpty()) viewModel.getUserIdByEmail(user.email) else 0

            // Observer reservation
            viewModel.getUserReservation(userId).observe(viewLifecycleOwner, { listReservation ->
                showReservation(listReservation)
            })
        })

        // Set list of reservation and hospital
        with(binding) {
            with(hospitalList) {
                layoutManager = if (requireActivity().applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) GridLayoutManager(requireContext(), 2) else LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            with(reservationList) {
                layoutManager = if (requireActivity().applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) GridLayoutManager(requireContext(), 2) else LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        // Observe hospital
        viewModel.getAllHospital().observe(viewLifecycleOwner, { listHospital ->
            showHospital(listHospital)
        })

        // Observe Notification
        viewModel.notificationText.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { text ->
                showSnackBar(text)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showReservation(listReservation: List<UserReservation>) {
        with(binding) {
            reservationList.visibility = if (listReservation.isEmpty()) View.GONE else View.VISIBLE
            emptyAnimation.visibility = if (listReservation.isEmpty()) View.VISIBLE else View.GONE
            emptyText.visibility = if (listReservation.isEmpty()) View.VISIBLE else View.GONE
        }
        if (listReservation.isNotEmpty()) {
            val adapterReservation = ReservationUserAdapter(listReservation)
            binding.reservationList.adapter = adapterReservation
            adapterReservation.setOnClickCallback(
                object : ReservationUserAdapter.OnCardClickCallback {
                    override fun onCardClicked(data: UserReservation) {
                        showDetailHospital(data.hospital)
                    }
                },
                object : ReservationUserAdapter.OnBtnClickCallback {
                    override fun onBtnClicked(data: UserReservation) {
                        cancelReservation(data)
                    }
                }
            )
        }
    }

    private fun cancelReservation(reservation: UserReservation) {
        // Format
        val dateFormat = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("in", "ID"))
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID"))

        // Alert Dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.fragment_map_cancel_dialog_title))
            .setMessage(String.format(getString(R.string.fragment_map_cancel_dialog_text), reservation.detailReservation.start.format(dateFormat), reservation.detailReservation.start.format(timeFormat), reservation.detailReservation.end.format(timeFormat)))
            .setNegativeButton(resources.getString(R.string.fragment_map_cancel_dialog_negative)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.fragment_map_cancel_dialog_positive)) { _, _ ->
                viewModel.cancelReservation(reservation)
            }
            .show()
    }

    private fun showHospital(listHospital: List<Hospital>) {
        val adapter = HospitalAdapter(listHospital)
        binding.hospitalList.adapter = adapter
        adapter.setOnItemClickCallback(object : HospitalAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Hospital) {
                showDetailHospital(data)
            }
        })
    }

    private fun showDetailHospital(hospital: Hospital) {
        val intent = Intent(requireContext(), DetailHospitalActivity::class.java)
        intent.putExtra(DetailHospitalActivity.EXTRA_HOSPITAL, hospital)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}