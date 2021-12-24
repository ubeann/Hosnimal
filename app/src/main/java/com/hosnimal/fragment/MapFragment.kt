package com.hosnimal.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.adapter.HospitalAdapter
import com.hosnimal.databinding.FragmentMapBinding
import com.hosnimal.model.Hospital
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.detail_hospital.DetailHospitalActivity
import com.hosnimal.ui.main.MainViewModel
import com.hosnimal.ui.main.MainViewModelFactory

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

        // Set list hospital
        with(binding) {
            with(hospitalList) {
                layoutManager = if (requireActivity().applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) GridLayoutManager(requireContext(), 2) else LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        // Observe hospital
        viewModel.getAllHospital().observe(viewLifecycleOwner, { listHospital ->
            showHospital(listHospital)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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