package com.example.marketplace

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.Model.Users
import com.example.marketplace.Prevalent.Prevalent
import com.google.firebase.database.*
import com.rey.material.widget.CheckBox
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class Login : AppCompatActivity() {
    private var loginButton: Button? = null
    private var inputPhone: EditText? = null
    private var inputPassword: EditText? = null
    private var loadingBar: ProgressDialog? = null

    private val parentDbName = "Users"
    private var chkBoxRememberMe: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.login_btn)
        inputPhone = findViewById(R.id.phone_et)
        inputPassword = findViewById(R.id.password_et)
        loadingBar = ProgressDialog(this)

        chkBoxRememberMe = findViewById(R.id.remember_me_chb);
        Paper.init(this);

        login_btn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val phone: String = inputPhone?.text.toString()
        val password: String = inputPassword?.text.toString()

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your phone number", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show()
        } else {
            loadingBar!!.setTitle("Login Account")
            loadingBar!!.setMessage("Please wait")
            loadingBar!!.setCanceledOnTouchOutside(false)
            loadingBar!!.show()

            allowAccessToAccount(phone, password)
        }
    }

    private fun allowAccessToAccount(phone: String, password: String) {
        if (chkBoxRememberMe?.isChecked == true) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference

        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()) {
                    val usersData: Users? =
                        dataSnapshot.child(parentDbName).child(phone).getValue(Users::class.java)
                    if (usersData?.phone.equals(phone)) {
                        if (usersData?.password.equals(password)) {
                            Toast.makeText(
                                this@Login,
                                "logged in Successfully...",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadingBar!!.dismiss()
                            val intent = Intent(this@Login, Home::class.java)
                            startActivity(intent)
                        } else {
                            loadingBar!!.dismiss()
                            Toast.makeText(
                                this@Login,
                                "Password is incorrect.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@Login,
                        "Account with this $phone number do not exists.", Toast.LENGTH_SHORT
                    ).show()
                    loadingBar!!.dismiss()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}