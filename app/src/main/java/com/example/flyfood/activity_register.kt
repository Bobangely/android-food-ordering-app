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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val btn_register = findViewById<Button>(R.id.btn_register)
        val et_name = findViewById<EditText>(R.id.et_name)
        val et_email = findViewById<EditText>(R.id.et_email)
        val et_password = findViewById<EditText>(R.id.et_password)
        val et_confirmPassword = findViewById<EditText>(R.id.et_confirm_password)
        val et_address = findViewById<EditText>(R.id.et_address)
        val cb_terms = findViewById<CheckBox>(R.id.cb_terms)

        btn_register.setOnClickListener {
            if (et_name.text.isEmpty() || et_email.text.isEmpty() || et_password.text.isEmpty() ||
                et_confirmPassword.text.isEmpty() || et_address.text.isEmpty() || !cb_terms.isChecked) {
                Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบทุกช่อง และยอมรับเงื่อนไข", Toast.LENGTH_SHORT).show()
            } else {
                val intentToLogin = Intent(this@activity_register, activity_Login::class.java)
                startActivity(intentToLogin)
            }
        }
    }
}