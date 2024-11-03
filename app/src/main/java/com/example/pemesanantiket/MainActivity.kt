package com.example.pemesanantiket

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val alert = AlertDialogManager()
    private lateinit var session: SessionManager
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        session = SessionManager(applicationContext)
        session.checkLogin()

        btnLogout = findViewById(R.id.out)
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Anda yakin ingin keluar ?")
                .setPositiveButton("Ya") { _, _ ->
                    session.logoutUser()
                    val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Tidak", null)
                .create()
                .show()
        }
    }

    // Pindahkan fungsi menu ke luar onCreate dan jadikan public
    fun profileMenu(view: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    fun historyMenu(view: View) {
        startActivity(Intent(this, HistoryActivity::class.java))
    }

    fun bookKereta(view: View) {
        startActivity(Intent(this, BookKeretaActivity::class.java))
    }

}