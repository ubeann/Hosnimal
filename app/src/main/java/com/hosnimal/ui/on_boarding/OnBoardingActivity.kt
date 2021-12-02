package com.hosnimal.ui.on_boarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.hosnimal.R
import com.hosnimal.ui.register.RegisterActivity
import com.hosnimal.databinding.ActivityOnBoardingBinding
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.hosnimal.App
import com.hosnimal.preferences.OnBoardingPreferences

class OnBoardingActivity : AppCompatActivity() {
    // Binding
    private var _binding: ActivityOnBoardingBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var preferences: OnBoardingPreferences

    // ViewModel
    private lateinit var viewModel: OnBoardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set preferences and viewModel
        preferences = OnBoardingPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, OnBoardingViewModelFactory(preferences))[OnBoardingViewModel::class.java]

        // Dataset using adapter
        val adapter = OnBoardingAdapter(
            listOf(
                OnBoardingItem(
                    getString(R.string.onBoarding_1_title),
                    getString(R.string.onBoarding_1_description),
                    R.raw.on_boarding_1_animal
                ),
                OnBoardingItem(
                    getString(R.string.onBoarding_2_title),
                    getString(R.string.onBoarding_2_description),
                    R.raw.on_boarding_2_shopping
                ),
                OnBoardingItem(
                    getString(R.string.onBoarding_3_title),
                    getString(R.string.onBoarding_3_description),
                    R.raw.on_boarding_3_consultation
                )
            )
        )

        // Setting content ViewPager2
        binding.listContent.adapter = adapter

        // Setting dotsIndicator
        binding.dotsIndicator.setViewPager2(binding.listContent)

        // Dynamic Content
        binding.listContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position+1 == adapter.itemCount) {
                    visibilityButton(binding.btnNext, false)
                    visibilityButton(binding.btnSkip, false)
                    visibilityButton(binding.btnEnd, true)
                } else {
                    visibilityButton(binding.btnEnd, false)
                    visibilityButton(binding.btnNext, true)
                    visibilityButton(binding.btnSkip, true)
                }
            }
        })

        // Button next onClick listener
        binding.btnNext.setOnClickListener{
            binding.listContent.currentItem += 1
        }

        // Button skip onClick listener
        binding.btnSkip.setOnClickListener {
            binding.listContent.currentItem = adapter.itemCount
        }

        // Button end onClick listener
        binding.btnEnd.setOnClickListener {
            viewModel.saveOnBoardingSetting(true)
            val intent = Intent(this@OnBoardingActivity, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun visibilityButton(view: View, isVisible: Boolean) {
        if (isVisible) {
            view.animate()
                .translationY(0F)
                .alpha(1F)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.VISIBLE
                    }
                })
        } else {
            view.animate()
                .translationY(view.height.toFloat())
                .alpha(0F)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.INVISIBLE
                    }
                })
        }
    }
}