package com.example.pemesanantiket

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.text.NumberFormat
import java.util.Locale

class HistoryAdapter(
    private val mContext: Activity,
    private val bookings: ArrayList<HistoryModel>
) : ArrayAdapter<HistoryModel>(mContext, 0, bookings) {

    private val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = convertView ?: LayoutInflater.from(mContext)
            .inflate(R.layout.list_item_booking, parent, false)

        val currentBooking = getItem(position) ?: return listItemView

        // Find all views
        val idBookView = listItemView.findViewById<TextView>(R.id.id_booking)
        val tanggalView = listItemView.findViewById<TextView>(R.id.tanggal)
        val riwayatView = listItemView.findViewById<TextView>(R.id.riwayat)
        val tvTotalView = listItemView.findViewById<TextView>(R.id.tv_total)
        val totalView = listItemView.findViewById<TextView>(R.id.total)
        val imageView = listItemView.findViewById<ImageView>(R.id.image)

        // Set the values
        idBookView.text = "ID : ${currentBooking.idBook}"
        tanggalView.text = currentBooking.tanggal
        riwayatView.text = currentBooking.riwayat
        tvTotalView.text = "Total :"

        try {
            val numericTotal = currentBooking.total.replace(Regex("[^0-9]"), "").toLong()
            totalView.text = "Rp. ${numberFormat.format(numericTotal)}"
        } catch (e: Exception) {
            totalView.text = "Rp. ${currentBooking.total}"
        }

        // Handle image
        if (currentBooking.hasImage() && currentBooking.imageResourceId != null) {
            imageView.setImageResource(currentBooking.imageResourceId)
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }

        return listItemView
    }
}