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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.adapter.HospitalAdapter
import com.hosnimal.adapter.ProductAdapter
import com.hosnimal.databinding.FragmentHomeBinding
import com.hosnimal.model.Hospital
import com.hosnimal.model.Product
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.detail_product.DetailProductActivity
import com.hosnimal.ui.main.MainViewModel
import com.hosnimal.ui.main.MainViewModelFactory

class HomeFragment : Fragment() {
    // Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // ViewModel
    private lateinit var viewModel: MainViewModel

    // Much List Item to Show
    private val totalItem = 4

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setting Preferences
        preferences = UserPreferences.getInstance(requireActivity().dataStore)

        // Set ViewModel
        viewModel = ViewModelProvider(this, MainViewModelFactory(requireActivity().application, preferences))[MainViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set title name
        viewModel.getUserSetting().observe(viewLifecycleOwner, { user ->
            binding.name.text = String.format(getString(R.string.fragment_home_title), user.name)
        })

        // Setting button notification
        binding.notification.setOnClickListener {
            it.findNavController().navigate(R.id.action_home_fragment_to_notificationFragment)
        }

        // Set list product pharmacy and list hospital
        with(binding) {
            with(pharmacyList) {
                layoutManager = if (requireActivity().applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                    object : GridLayoutManager(requireContext(), 2)  {
                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                else
                    object : LinearLayoutManager(requireContext()) {
                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                setHasFixedSize(true)
            }
            with(hospitalList) {
                layoutManager = if (requireActivity().applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                   object : GridLayoutManager(requireContext(), 2)  {
                       override fun canScrollVertically(): Boolean {
                           return false
                       }
                   }
                else
                   object : LinearLayoutManager(requireContext()) {
                       override fun canScrollVertically(): Boolean {
                           return false
                       }
                   }
                setHasFixedSize(true)
            }
        }

        // Observe product pharmacy
        viewModel.getTopProduct(totalItem).observe(viewLifecycleOwner, { listProduct ->
            showProduct(listProduct)
        })

        // Setting button see all pharmacy
        binding.pharmacySeeAll.setOnClickListener {
            it.findNavController().navigate(R.id.action_home_fragment_to_pharmacyFragment)
        }

        // Observe hospital
        viewModel.getTopHospital(totalItem).observe(viewLifecycleOwner, { listHospital ->
            showHospital(listHospital)
        })
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
                showDetailProduct(data)
            }
        })
    }

    private fun showDetailProduct(product: Product) {
        val intent = Intent(requireContext(), DetailProductActivity::class.java)
        intent.putExtra(DetailProductActivity.EXTRA_PRODUCT, product)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
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

    }
}