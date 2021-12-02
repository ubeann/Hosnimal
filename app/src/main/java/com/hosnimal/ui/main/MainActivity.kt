package com.hosnimal.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hosnimal.R
import com.hosnimal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // Navigation
    private lateinit var navController:NavController
    private lateinit var navHostFragment:NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prepare View Binding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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