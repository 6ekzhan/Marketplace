package com.example.marketplace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_home.*

class Home : AppCompatActivity() {
    private var logoutButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        logoutButton = findViewById(R.id.logout_btn)

        logout_btn.setOnClickListener{
            Paper.book().destroy()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}