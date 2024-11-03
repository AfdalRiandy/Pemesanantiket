package com.example.pemesanantiket

import android.content.Intent  // Tambahkan import untuk Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var txtName: EditText
    private lateinit var txtUsername: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnDaftar: Button
    private lateinit var btnKeLogin: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)

        txtName = findViewById(R.id.reg_nama)
        txtUsername = findViewById(R.id.reg_username)
        txtEmail = findViewById(R.id.reg_email)
        txtPassword = findViewById(R.id.reg_password)
        btnDaftar = findViewById(R.id.daftar)
        btnKeLogin = findViewById(R.id.ke_login)

        btnDaftar.setOnClickListener {
            val name = txtName.text.toString().trim()
            val username = txtUsername.text.toString().trim()
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (name.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val isRegistered = dbHelper.register(username, email, password, name)
                if (isRegistered) {
                    Toast.makeText(this, "Pendaftaran berhasil", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Pendaftaran gagal, email mungkin sudah terdaftar!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Mohon lengkapi semua data!", Toast.LENGTH_LONG).show()
            }
        }

        btnKeLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
