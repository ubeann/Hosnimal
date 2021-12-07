package com.hosnimal.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.databinding.FragmentProfileBinding
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.main.MainViewModel
import com.hosnimal.ui.main.MainViewModelFactory
import com.hosnimal.ui.register.RegisterActivity
import java.time.format.DateTimeFormatter
import java.util.*

class ProfileFragment : Fragment() {
    // Binding
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // ViewModel
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setting Preferences
        preferences = UserPreferences.getInstance(requireActivity().dataStore)

        // Set ViewModel
        viewModel = ViewModelProvider(this, MainViewModelFactory(requireActivity().application, preferences))[MainViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set profile
        viewModel.getUserSetting().observe(viewLifecycleOwner, { user ->
            with(binding) {
                name.text = user.name
                joined.text = String.format(getString(R.string.fragment_profile_joined), user.createdAt.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("in", "ID"))), user.createdAt.format(DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID"))))
                nameText.setText(user.name)
                emailText.setText(user.email)
                phoneText.setText(user.phone)
                birthdayText.setText(user.birthday.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("in", "ID"))))
            }
        })

        // Button Exit
        binding.btnLogout.setOnClickListener {
            viewModel.forgetUserLogin(true)
            activity?.let {
                val intent = Intent(it, RegisterActivity::class.java)
                it.startActivity(intent)
                it.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                it.finish()
            }
        }

        // Button Edit
        binding.btnEdit.setOnClickListener {
            it.findNavController().navigate(R.id.action_profile_fragment_to_editProfileFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}