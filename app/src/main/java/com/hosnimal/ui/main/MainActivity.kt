package com.hosnimal.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hosnimal.R
import com.hosnimal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bindingContent:ActivityMainBinding
    private lateinit var navController:NavController
    private lateinit var navHostFragment:NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prepare View Binding
        bindingContent = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingContent.root)

        // Nav Controller
        navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupSmoothBottomMenu()
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.navigation_bar)
        val menu = popupMenu.menu
        bindingContent.bottomNavigationBar.setupWithNavController(menu, navController)
    }
}