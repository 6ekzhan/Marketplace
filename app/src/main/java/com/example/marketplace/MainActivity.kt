package com.example.marketplace

import android.R.attr.password
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.Model.Users
import com.example.marketplace.Prevalent.Prevalent
import com.google.firebase.database.*
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var registerButton: Button? = null
    private var loginButton: Button? = null
    private var loadingBar: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerButton = findViewById(R.id.register_btn)
        loginButton = findViewById(R.id.login_btn)
        loadingBar = ProgressDialog(this);

        Paper.init(this);

        register_btn.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        login_btn.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        val UserPhoneKey = Paper.book().read<String>(Prevalent.UserPhoneKey)
        val UserPasswordKey = Paper.book().read<String>(Prevalent.UserPasswordKey)

        if (UserPhoneKey !== "" && UserPasswordKey !== "") {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                allowAccess(UserPhoneKey, UserPasswordKey)
                loadingBar!!.setTitle("Already Logged in")
                loadingBar!!.setMessage("Please wait.....")
                loadingBar!!.setCanceledOnTouchOutside(false)
                loadingBar!!.show()
            }
        }
    }

    private fun allowAccess(phone: String?, password: String?) {
        val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference

        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("Users").child(phone.toString()).exists()) {
                    val usersData =
                        dataSnapshot.child("Users").child(phone.toString()).getValue(Users::class.java)
                    if (usersData!!.phone == phone) {
                        if (usersData!!.password == password) {
                            Toast.makeText(
                                this@MainActivity,
                                "Please wait, you are already logged in...",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadingBar!!.dismiss()
                            val intent = Intent(this@MainActivity, Home::class.java)
                            Prevalent.currentOnlineUser = usersData
                            startActivity(intent)
                        } else {
                            loadingBar!!.dismiss()
                            Toast.makeText(
                                this@MainActivity,
                                "Password is incorrect.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Account with this " + phone.toString() + " number do not exists.",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadingBar!!.dismiss()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}