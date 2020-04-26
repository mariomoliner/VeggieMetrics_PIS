package com.example.mmolinca20alumnes.veggiemetrics.APIcall

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.mmolinca20alumnes.veggiemetrics.LogIn
import com.example.mmolinca20alumnes.veggiemetrics.R
import com.google.firebase.auth.FirebaseAuth


class LogOutFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.logOut)
                .setPositiveButton(R.string.sortir,
                    DialogInterface.OnClickListener { dialog, id ->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this.context, LogIn::class.java)
                        startActivity(intent)
                        (context as Activity).finish()
                    })
                .setNegativeButton(
                    R.string.cancelar,
                    DialogInterface.OnClickListener { dialog, id ->
                        // L'usuari no surt
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
