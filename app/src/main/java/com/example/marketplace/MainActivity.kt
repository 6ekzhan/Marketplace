package com.example.marketplace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")
        user_id.setText("User ID:: $userId")
        email_id.setText( "Email ID:: $emailId")

        button.setOnClickListener{
            //Logout
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
}