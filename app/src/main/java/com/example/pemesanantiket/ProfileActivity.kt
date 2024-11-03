// ProfileActivity.kt
package com.example.pemesanantiket

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ProfileActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DatabaseHelper(this)
        session = SessionManager(applicationContext)

        // Memeriksa login sebelum melanjutkan
        session.checkLogin()

        // Mengambil detail pengguna dari session
        val userDetails = session.getUserDetails()
        val email = userDetails[SessionManager.KEY_EMAIL] ?: ""

        // Mengambil data user dari database
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            null,
            "${DatabaseHelper.COL_EMAIL} = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_NAME)
            val emailIndex = cursor.getColumnIndex(DatabaseHelper.COL_EMAIL)

            if (nameIndex != -1 && emailIndex != -1) {
                val name = cursor.getString(nameIndex)
                val userEmail = cursor.getString(emailIndex)

                findViewById<TextView>(R.id.lblName).text = name
                findViewById<TextView>(R.id.lblEmail).text = userEmail
            }
        }

        cursor.close()
        db.close()

        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.tbProfile)
        toolbar.title = "Identitas Penyewa"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}