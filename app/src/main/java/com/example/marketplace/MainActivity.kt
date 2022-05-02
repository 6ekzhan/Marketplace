package com.example.marketplace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var registerButton: Button? = null
    private var loginButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerButton = findViewById(R.id.register_btn)
        loginButton = findViewById(R.id.login_btn)

        login_btn.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }
}