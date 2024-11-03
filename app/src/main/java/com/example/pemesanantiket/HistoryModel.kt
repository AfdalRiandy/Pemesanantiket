package com.example.pemesanantiket

class HistoryModel(
    val idBook: String,
    val tanggal: String,  // Gunakan val untuk mendapatkan getter otomatis
    val riwayat: String,
    val total: String,
    val imageResourceId: Int?
){
    // Menambahkan metode untuk memeriksa apakah ada gambar
    fun hasImage(): Boolean {
        return imageResourceId != null
    }
}