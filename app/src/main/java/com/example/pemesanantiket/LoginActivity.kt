package com.example.pemesanantiket

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView  // Tambahkan import ini


class LoginActivity : AppCompatActivity() {
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView  // Ubah tipe dari Button ke TextView
    private val alert = AlertDialogManager()
    private lateinit var session: SessionManager
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize components
        dbHelper = DatabaseHelper(this)
        session = SessionManager(applicationContext)

        // Check if user is already logged in
        if (session.isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        txtEmail = findViewById(R.id.email)
        txtPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.masuk)
        btnRegister = findViewById(R.id.ke_daftar)  // Ini sekarang mengacu ke TextView

        btnLogin.setOnClickListener {
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                try {
                    val user = dbHelper.login(email, password)
                    if (user != null) {
                        // Create login session with all user details
                        session.createLoginSession(
                            email = user.email,
                            name = user.name,
                            username = user.username
                        )

                        // Redirect to MainActivity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        alert.showAlertDialog(
                            this,
                            "Login Gagal",
                            "Email atau Password salah!",
                            false
                        )
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                alert.showAlertDialog(
                    this,
                    "Login Gagal",
                    "Email dan Password tidak boleh kosong!",
                    false
                )
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        setupWindowDisplay()
    }

    private fun setupWindowDisplay() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }
}