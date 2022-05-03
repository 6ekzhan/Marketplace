package com.example.marketplace

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*


class Register : AppCompatActivity() {
    private var registerButton: Button? = null
    private var inputName: EditText? = null
    private var inputPhone: EditText? = null
    private var inputPassword: EditText? = null
    private var loadingBar: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton = findViewById(R.id.register_btn)
        inputName = findViewById(R.id.name_et)
        inputPhone = findViewById(R.id.phone_et)
        inputPassword = findViewById(R.id.password_et)
        loadingBar = ProgressDialog(this)

        register_btn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val name: String = inputName?.text.toString()
        val phone: String = inputPhone?.text.toString()
        val password: String = inputPassword?.text.toString()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar?.setTitle("Create Account")
            loadingBar?.setMessage("Please wait")
            loadingBar?.setCanceledOnTouchOutside(false)
            loadingBar?.show()

            validatePhone(name, phone, password)
        }
    }

    private fun validatePhone(name: String, phone: String, password: String) {
        val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.child("Users").child(phone).exists()) {
                    val userdataMap: HashMap<String, Any> = HashMap()
                    userdataMap["name"] = name
                    userdataMap["phone"] = phone
                    userdataMap["password"] = password
                    rootRef.child("Users").child(phone).updateChildren(userdataMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@Register,
                                    "Congratulations, your account has been created.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadingBar!!.dismiss()
                                val intent =
                                    Intent(this@Register, Login::class.java)
                                startActivity(intent)
                            } else {
                                loadingBar!!.dismiss()
                                Toast.makeText(
                                    this@Register,
                                    "Network Error: Please try again after some time",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        this@Register,
                        "This $phone already exists.",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadingBar!!.dismiss()
                    Toast.makeText(
                        this@Register,
                        "Please try again using another phone number.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@Register, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}