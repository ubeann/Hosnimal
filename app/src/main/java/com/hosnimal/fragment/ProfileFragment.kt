package com.hosnimal.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.databinding.FragmentProfileBinding
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.main.MainViewModel
import com.hosnimal.ui.main.MainViewModelFactory
import com.hosnimal.ui.register.RegisterActivity

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
        viewModel = ViewModelProvider(this, MainViewModelFactory(preferences))[MainViewModel::class.java]

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
                joined.text = String.format(getString(R.string.fragment_profile_joined), user.createdAt)
                nameText.setText(user.name)
                emailText.setText(user.email)
                phoneText.setText(user.phone)
                birthdayText.setText(user.birthday)
            }
        })

        // Button Exit
        binding.btnLogout.setOnClickListener {
            viewModel.forgetUserLogin(true)
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            activity?.finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}