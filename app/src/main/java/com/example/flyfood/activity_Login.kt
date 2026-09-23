package com.example.flyfood

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class activity_Login : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        val btnGuest = findViewById<Button>(R.id.btn_guest)

        btnLogin.isEnabled = false
        btnLogin.alpha = 0.6f

        etEmail.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasAt = s?.contains("@") == true
                btnLogin.isEnabled = hasAt
                btnLogin.alpha = if (hasAt) 1.0f else 0.6f
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })




        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (!email.contains("@")) {
                Toast.makeText(this@activity_Login, "กรุณากรอกอีเมลให้ถูกต้อง", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val intentToRegister = Intent(this@activity_Login, activity_home::class.java)
            startActivity(intentToRegister)
        }


        btnRegister.setOnClickListener {
            val intentToRegister = Intent(this@activity_Login, activity_register::class.java)
            startActivity(intentToRegister)
        }


        btnGuest.setOnClickListener {
            val intentToHomeAsGuest = Intent(this@activity_Login, activity_home::class.java)
            startActivity(intentToHomeAsGuest)
            finish()
        }
    }
}
