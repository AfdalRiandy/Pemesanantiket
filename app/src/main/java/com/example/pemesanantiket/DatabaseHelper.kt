// DatabaseHelper.kt
package com.example.pemesanantiket

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "db_travel"
        const val DATABASE_VERSION = 2
        const val TABLE_USER = "TB_USER" // Mengubah nama tabel menjadi TB_USER untuk konsistensi
        const val COL_USERNAME = "username"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
        const val COL_NAME = "name"

        // Konstanta untuk TB_BOOK
        const val TABLE_BOOK = "TB_BOOK"
        const val COL_ID_BOOK = "id_book"
        const val COL_ASAL = "asal"
        const val COL_TUJUAN = "tujuan"
        const val COL_TANGGAL = "tanggal"
        const val COL_DEWASA = "dewasa"
        const val COL_ANAK = "anak"

        // Konstanta untuk TB_HARGA
        const val TABLE_HARGA = "TB_HARGA"
        const val COL_ID_HARGA = "id"
        const val COL_HARGA_DEWASA = "harga_dewasa"
        const val COL_HARGA_ANAK = "harga_anak"
        const val COL_HARGA_TOTAL = "harga_total"
    }

    fun saveBooking(email: String, asal: String, tujuan: String, tanggal: String,
                    dewasa: String, anak: String, hargaDewasa: Int, hargaAnak: Int, hargaTotal: Int): Long {
        val db = this.writableDatabase
        var bookingId: Long = -1

        db.beginTransaction()
        try {
            // Insert ke TB_BOOK
            val bookValues = ContentValues().apply {
                put(COL_ASAL, asal)
                put(COL_TUJUAN, tujuan)
                put(COL_TANGGAL, tanggal)
                put(COL_DEWASA, dewasa)
                put(COL_ANAK, anak)
            }
            bookingId = db.insert(TABLE_BOOK, null, bookValues)

            // Insert ke TB_HARGA
            if (bookingId != -1L) {
                val hargaValues = ContentValues().apply {
                    put("username", email) // menggunakan email sebagai username
                    put(COL_ID_BOOK, bookingId)
                    put(COL_HARGA_DEWASA, hargaDewasa)
                    put(COL_HARGA_ANAK, hargaAnak)
                    put(COL_HARGA_TOTAL, hargaTotal)
                }
                db.insert(TABLE_HARGA, null, hargaValues)
            }

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }

        return bookingId
    }

    // Method untuk mendapatkan semua booking user
    fun getBookings(email: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("""
        SELECT b.*, h.harga_total 
        FROM $TABLE_BOOK b 
        INNER JOIN $TABLE_HARGA h ON b.$COL_ID_BOOK = h.$COL_ID_BOOK 
        WHERE h.username = ?
    """, arrayOf(email))
    }

    // Method untuk menghapus booking
    fun deleteBooking(bookingId: String): Boolean {
        val db = this.writableDatabase
        var success = false
        db.beginTransaction()
        try {
            // Karena ada ON DELETE CASCADE, kita hanya perlu menghapus dari TB_BOOK
            val result = db.delete(TABLE_BOOK, "$COL_ID_BOOK = ?", arrayOf(bookingId))
            success = result > 0
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
        return success
    }

    // Method untuk mengecek apakah booking dengan ID tertentu ada
    fun isBookingExists(bookingId: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_BOOK,
            arrayOf(COL_ID_BOOK),
            "$COL_ID_BOOK = ?",
            arrayOf(bookingId),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Method untuk mendapatkan detail booking
    fun getBookingDetails(bookingId: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("""
        SELECT b.*, h.harga_dewasa, h.harga_anak, h.harga_total
        FROM $TABLE_BOOK b
        INNER JOIN $TABLE_HARGA h ON b.$COL_ID_BOOK = h.$COL_ID_BOOK
        WHERE b.$COL_ID_BOOK = ?
    """, arrayOf(bookingId))
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_USER (
                $COL_USERNAME TEXT,
                $COL_EMAIL TEXT PRIMARY KEY,
                $COL_PASSWORD TEXT,
                $COL_NAME TEXT
            )
        """)

        // Tambahkan tabel TB_BOOK
        db.execSQL("""
            CREATE TABLE TB_BOOK (
                id_book INTEGER PRIMARY KEY AUTOINCREMENT,
                asal TEXT,
                tujuan TEXT,
                tanggal TEXT,
                dewasa TEXT,
                anak TEXT
            )
        """)

        // Tambahkan tabel TB_HARGA
        db.execSQL("""
            CREATE TABLE TB_HARGA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                id_book INTEGER,
                harga_dewasa INTEGER,
                harga_anak INTEGER,
                harga_total INTEGER,
                FOREIGN KEY(id_book) REFERENCES TB_BOOK(id_book) ON DELETE CASCADE
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS TB_BOOK")
        db.execSQL("DROP TABLE IF EXISTS TB_HARGA")
        onCreate(db)
    }

    fun register(username: String, email: String, password: String, name: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, username)
            put(COL_EMAIL, email)
            put(COL_PASSWORD, password)
            put(COL_NAME, name)
        }

        val result = db.insert(TABLE_USER, null, values)
        db.close()
        return result != -1L
    }

    fun login(email: String, password: String): User? {
        val db = this.readableDatabase
        var user: User? = null

        try {
            val cursor = db.query(
                TABLE_USER,
                null,
                "$COL_EMAIL = ? AND $COL_PASSWORD = ?",
                arrayOf(email, password),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                val usernameIndex = cursor.getColumnIndex(COL_USERNAME)
                val nameIndex = cursor.getColumnIndex(COL_NAME)
                val emailIndex = cursor.getColumnIndex(COL_EMAIL)

                user = User(
                    username = cursor.getString(usernameIndex),
                    email = cursor.getString(emailIndex),
                    password = password,
                    name = cursor.getString(nameIndex)
                )
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return user
    }
}