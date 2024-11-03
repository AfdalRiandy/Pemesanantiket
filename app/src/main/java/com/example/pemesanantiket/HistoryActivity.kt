package com.example.pemesanantiket

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class HistoryActivity : AppCompatActivity() {
    private var cursor: Cursor? = null
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var session: SessionManager
    private lateinit var email: String
    private lateinit var tvNotFound: TextView
    private lateinit var listBook: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        initializeViews()
        setupToolbar()
        loadBookingHistory()
    }

    private fun initializeViews() {
        dbHelper = DatabaseHelper(this)
        db = dbHelper.readableDatabase
        session = SessionManager(applicationContext)

        tvNotFound = findViewById(R.id.noHistory)
        listBook = findViewById(R.id.list_booking)

        val user = session.getUserDetails()
        email = user[SessionManager.KEY_EMAIL] ?: ""
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.tbHistory)
        toolbar.title = "Riwayat Booking"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadBookingHistory() {
        try {
            val hasil = ArrayList<HistoryModel>()

            cursor = db.rawQuery("""
                SELECT b.id_book, b.asal, b.tujuan, b.tanggal, b.dewasa, b.anak, 
                       h.harga_total, h.username 
                FROM ${DatabaseHelper.TABLE_BOOK} b 
                INNER JOIN ${DatabaseHelper.TABLE_HARGA} h 
                ON b.${DatabaseHelper.COL_ID_BOOK} = h.${DatabaseHelper.COL_ID_BOOK} 
                WHERE h.username = ?
            """, arrayOf(email))

            cursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idBook = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_BOOK))
                        val asal = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASAL))
                        val tujuan = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TUJUAN))
                        val tanggal = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TANGGAL))
                        val dewasa = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEWASA))
                        val anak = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ANAK))
                        val total = cursor.getString(6) // index of harga_total

                        val riwayat = """
                            Berhasil melakukan booking untuk melakukan perjalanan dari $asal menuju $tujuan
                            pada tanggal $tanggal. Jumlah pembelian tiket dewasa sejumlah $dewasa
                            dan tiket anak-anak sejumlah $anak.
                        """.trimIndent()

                        hasil.add(HistoryModel(idBook, tanggal, riwayat, total, R.drawable.profile))
                    } while (cursor.moveToNext())
                }
            }

            val arrayAdapter = HistoryAdapter(this, hasil)
            listBook.adapter = arrayAdapter

            // Set item click listener untuk delete
            listBook.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedBooking = hasil[position]
                showDeleteDialog(selectedBooking.idBook)
            }

            // Update visibility
            updateVisibility(hasil.isNotEmpty())

        } catch (e: Exception) {
            e.printStackTrace()
            updateVisibility(false)
        }
    }

    private fun showDeleteDialog(bookingId: String) {
        AlertDialog.Builder(this)
            .setTitle("Pilihan")
            .setItems(arrayOf("Hapus Data")) { _, _ ->
                deleteBooking(bookingId)
            }
            .create()
            .show()
    }

    private fun deleteBooking(bookingId: String) {
        try {
            db.beginTransaction()
            db.delete(DatabaseHelper.TABLE_HARGA, "${DatabaseHelper.COL_ID_BOOK} = ?", arrayOf(bookingId))
            db.delete(DatabaseHelper.TABLE_BOOK, "${DatabaseHelper.COL_ID_BOOK} = ?", arrayOf(bookingId))
            db.setTransactionSuccessful()
            loadBookingHistory()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    private fun updateVisibility(hasData: Boolean) {
        tvNotFound.visibility = if (hasData) View.GONE else View.VISIBLE
        listBook.visibility = if (hasData) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        cursor?.close()
        db.close()
    }
}