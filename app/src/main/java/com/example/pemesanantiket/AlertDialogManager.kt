    package com.example.pemesanantiket

    import android.app.AlertDialog
    import android.content.Context
    import com.example.pemesanantiket.R

    class AlertDialogManager {

        fun showAlertDialog(context: Context, title: String, message: String, status: Boolean?) {
            val alertDialog = AlertDialog.Builder(context).create()
            alertDialog.setTitle(title)
            alertDialog.setMessage(message)

            status?.let {
                alertDialog.setIcon(if (it) R.drawable.ic_success else R.drawable.ic_fail)
            }

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
                dialog.dismiss()
            }

            alertDialog.show()
        }
    }
