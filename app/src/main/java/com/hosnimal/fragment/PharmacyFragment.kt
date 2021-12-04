package com.hosnimal.fragment

import android.content.Context
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
import com.hosnimal.adapter.ProductAdapter
import com.hosnimal.databinding.FragmentPharmacyBinding
import com.hosnimal.model.Product
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.main.MainViewModel
import com.hosnimal.ui.main.MainViewModelFactory

class PharmacyFragment : Fragment() {
    // Binding
    private var _binding: FragmentPharmacyBinding? = null
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
        _binding = FragmentPharmacyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set list product pharmacy
        binding.pharmacyList.layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean = false
        }
        binding.pharmacyList.setHasFixedSize(true)
        if (requireActivity().applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.pharmacyList.layoutManager = object : GridLayoutManager(requireContext(), 2) {
                override fun canScrollVertically(): Boolean = false
            }
        } else {
            binding.pharmacyList.layoutManager = object : LinearLayoutManager(requireContext())  {
                override fun canScrollVertically(): Boolean = false
            }
        }


        // Observe product pharmacy
        viewModel.getAllProduct().observe(viewLifecycleOwner, { listProduct ->
            showProduct(listProduct)
        })

        // Setting Button Back
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showProduct(listProduct: List<Product>) {
        val adapter = ProductAdapter(listProduct)
        binding.pharmacyList.adapter = adapter
        adapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                TODO("Not yet implemented")
            }
        })
    }
}