package com.hosnimal.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.databinding.ActivityMainBinding
import com.hosnimal.model.User
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.register.RegisterActivity

class MainActivity : AppCompatActivity() {
    // Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // ViewModel
    private lateinit var viewModel: MainViewModel

    // Data User
    private lateinit var user: User

    // Navigation
    private lateinit var navController:NavController
    private lateinit var navHostFragment:NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting Preferences
        preferences = UserPreferences.getInstance(dataStore)

        // Set ViewModel
        viewModel = ViewModelProvider(this, MainViewModelFactory(application, preferences))[MainViewModel::class.java]

        // User Data
        viewModel.getUserSetting().observe(this, {
            user = it
        })

        // Prepare View Binding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check user login
        if (viewModel.isRegistered(user.email)) {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

        // Nav Controller
        navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupSmoothBottomMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.navigation_bar)
        val menu = popupMenu.menu
        binding.bottomNavigationBar.setupWithNavController(menu, navController)
    }
}