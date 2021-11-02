package com.hosnimal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Setting Button Register
        val btnRegister:Button = findViewById(R.id.btn_register)
        btnRegister.setOnClickListener {
            val intent = Intent(
                this@RegisterActivity,
                MainActivity::class.java
            )
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
        // Setting Button Login Alternative
        val btnLogin:Button = findViewById(R.id.btn_alternative_login)
        btnLogin.setOnClickListener {
            val intent = Intent(
                this@RegisterActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }
}