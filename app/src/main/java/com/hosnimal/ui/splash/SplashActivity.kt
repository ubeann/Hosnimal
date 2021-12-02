package com.hosnimal.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.hosnimal.App
import com.hosnimal.ui.on_boarding.OnBoardingActivity
import com.hosnimal.R
import com.hosnimal.ui.register.RegisterActivity
import com.hosnimal.ui.on_boarding.OnBoardingPreferences
import java.lang.Exception

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    // Variable
    private val loadingTime:Long = 5000 //Delay of 5 seconds
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private var isOnBoardingCleared: Boolean = false
    private lateinit var onBoardingPreferences: OnBoardingPreferences
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Set preferences and viewModel
        onBoardingPreferences = OnBoardingPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, SplashViewModelFactory(onBoardingPreferences))[SplashViewModel::class.java]

        // Observe if OnBoarding is cleared
        viewModel.getOnBoardingSetting().observe(this, {
            isOnBoardingCleared = it
        })

        val splashThread: Thread = object : Thread() {
            override fun run() {
                try {
                    super.run()
                    sleep(loadingTime)
                } catch (e: Exception) {
                } finally {
                    whichIntent()
                }
            }
        }
        splashThread.start()
    }

    private fun whichIntent() {
        val intent = Intent(this@SplashActivity,
            if (isOnBoardingCleared)
                RegisterActivity::class.java
            else
                OnBoardingActivity::class.java
        )
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}