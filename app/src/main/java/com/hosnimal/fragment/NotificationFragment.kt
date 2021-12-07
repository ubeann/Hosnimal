package com.hosnimal.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.adapter.OrderAdapter
import com.hosnimal.databinding.FragmentNotificationBinding
import com.hosnimal.model.Product
import com.hosnimal.model.User
import com.hosnimal.model.relational.UserOrder
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.detail_product.DetailProductActivity
import com.hosnimal.ui.main.MainViewModel
import com.hosnimal.ui.main.MainViewModelFactory
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*

class NotificationFragment : Fragment() {
    // Binding
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // ViewModel
    private lateinit var viewModel: MainViewModel

    // Data User
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setting Preferences
        preferences = UserPreferences.getInstance(requireActivity().dataStore)

        // Set ViewModel
        viewModel = ViewModelProvider(this, MainViewModelFactory(requireActivity().application, preferences))[MainViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Set list of notification
        with(binding.notificationList) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        // Observe user notification
        viewModel.getUserSetting().observe(viewLifecycleOwner, { userData ->
            viewModel.getUserOrders(userData.email).observe(viewLifecycleOwner, { userOrder ->
                showNotification(userOrder)
            })
        })

        // Observe system notification
        viewModel.notificationText.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
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

    private fun showNotification(listOrder: List<UserOrder>) {
        with(binding) {
            notificationList.visibility = if (listOrder.isEmpty()) View.GONE else View.VISIBLE
            emptyAnimation.visibility = if (listOrder.isEmpty()) View.VISIBLE else View.GONE
            emptyText.visibility = if (listOrder.isEmpty()) View.VISIBLE else View.GONE
        }
        if (listOrder.isNotEmpty()) {
            val adapter = OrderAdapter(listOrder)
            binding.notificationList.adapter = adapter
            adapter.setOnClickCallback(
                object : OrderAdapter.OnCardClickCallback {
                    override fun onCardClicked(data: Product) {
                        showDetailProduct(data)
                    }
                },
                object : OrderAdapter.OnBtnClickCallback {
                    override fun onBtnClicked(data: UserOrder) {
                        cancelOrder(data)
                    }
                }
            )
        }
    }

    private fun showDetailProduct(product: Product) {
        val intent = Intent(requireContext(), DetailProductActivity::class.java)
        intent.putExtra(DetailProductActivity.EXTRA_PRODUCT, product)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun cancelOrder(userOrder: UserOrder) {
        val date = userOrder.detailOrder.orderAt.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("in", "ID")))
        val pricing = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(userOrder.product.price * userOrder.detailOrder.qty).toString().replace(",00","")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.fragment_notification_dialog_title))
            .setMessage(String.format(getString(R.string.fragment_notification_dialog_text), userOrder.detailOrder.qty, userOrder.product.name, pricing, date))
            .setNegativeButton(getString(R.string.fragment_notification_dialog_negative)) { _, _ ->
            }
            .setPositiveButton(getString(R.string.fragment_notification_dialog_positive)) { _, _ ->
                viewModel.cancelOrder(userOrder)
            }
            .show()
    }
}