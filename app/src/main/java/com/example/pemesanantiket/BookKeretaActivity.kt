package com.example.pemesanantiket

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.*

class BookKeretaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spinAsal: Spinner
    private lateinit var spinTujuan: Spinner
    private lateinit var spinDewasa: Spinner
    private lateinit var spinAnak: Spinner
    private lateinit var session: SessionManager
    private lateinit var etTanggal: EditText
    private lateinit var email: String
    private lateinit var btnBook: Button

    private var sAsal: String? = null
    private var sTujuan: String? = null
    private var sTanggal: String? = null
    private var sDewasa: String? = null
    private var sAnak: String? = null
    private var hargaDewasa: Int = 100000  // Base price for adult
    private var hargaAnak: Int = 70000     // Base price for child
    private val newCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_kereta)

        try {
            initializeComponents()
            setupToolbar()
            setupViews()
            setupSpinners()
            setupDatePicker()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initializeComponents() {
        dbHelper = DatabaseHelper(this)
        session = SessionManager(applicationContext)

        val user = session.getUserDetails()
        email = user[SessionManager.KEY_EMAIL] ?: ""
        if (email.isEmpty()) {
            Toast.makeText(this, "Sesi login tidak valid", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupViews() {
        spinAsal = findViewById(R.id.asal)
        spinTujuan = findViewById(R.id.tujuan)
        spinDewasa = findViewById(R.id.dewasa)
        spinAnak = findViewById(R.id.anak)
        etTanggal = findViewById(R.id.tanggal_berangkat)
        btnBook = findViewById(R.id.book)

        etTanggal.inputType = InputType.TYPE_NULL
        btnBook.setOnClickListener { handleBooking() }
    }

    private fun setupSpinners() {
        val asal = arrayOf("Jakarta", "Bandung", "Purwokerto", "Yogyakarta", "Surabaya")
        val tujuan = arrayOf("Jakarta", "Bandung", "Purwokerto", "Yogyakarta", "Surabaya")
        val dewasa = arrayOf("0", "1", "2", "3", "4", "5")
        val anak = arrayOf("0", "1", "2", "3", "4", "5")

        setupSpinner(spinAsal, asal) { sAsal = it }
        setupSpinner(spinTujuan, tujuan) { sTujuan = it }
        setupSpinner(spinDewasa, dewasa) { sDewasa = it }
        setupSpinner(spinAnak, anak) { sAnak = it }
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>, onSelect: (String) -> Unit) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                onSelect(parent.getItemAtPosition(pos).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupDatePicker() {
        etTanggal.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    sTanggal = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                    etTanggal.setText(sTanggal)
                },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.tbKrl)
        toolbar.title = "Pesan Tiket Kereta"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun calculatePrices(): Triple<Int, Int, Int> {
        val jumlahDewasa = sDewasa?.toIntOrNull() ?: 0
        val jumlahAnak = sAnak?.toIntOrNull() ?: 0

        val totalDewasa = jumlahDewasa * hargaDewasa
        val totalAnak = jumlahAnak * hargaAnak
        val total = totalDewasa + totalAnak

        return Triple(totalDewasa, totalAnak, total)
    }

    private fun handleBooking() {
        if (validateInput()) {
            val (totalDewasa, totalAnak, total) = calculatePrices()

            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Booking")
                .setMessage("""
                    Apakah anda yakin ingin memesan tiket?
                    
                    Dari: $sAsal
                    Ke: $sTujuan
                    Tanggal: $sTanggal
                    Dewasa: $sDewasa (Rp. $totalDewasa)
                    Anak: $sAnak (Rp. $totalAnak)
                    Total: Rp. $total
                """.trimIndent())
                .setPositiveButton("Ya") { _, _ ->
                    saveBooking(totalDewasa, totalAnak, total)
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }

    private fun validateInput(): Boolean {
        if (sAsal == null || sTujuan == null || sTanggal == null ||
            sDewasa == null || sAnak == null) {
            Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_LONG).show()
            return false
        }

        if (sAsal.equals(sTujuan, ignoreCase = true)) {
            Toast.makeText(this, "Stasiun asal dan tujuan tidak boleh sama",
                Toast.LENGTH_LONG).show()
            return false
        }

        if (sDewasa == "0" && sAnak == "0") {
            Toast.makeText(this, "Minimal pesan 1 tiket", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun saveBooking(totalDewasa: Int, totalAnak: Int, total: Int) {
        try {
            val bookingId = dbHelper.saveBooking(
                email, sAsal!!, sTujuan!!, sTanggal!!,
                sDewasa!!, sAnak!!, totalDewasa, totalAnak, total
            )

            if (bookingId != -1L) {
                Toast.makeText(this, "Booking berhasil!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal menyimpan booking", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}