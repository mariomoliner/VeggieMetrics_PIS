package com.example.mmolinca20alumnes.veggiemetrics

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_rec_password_activity.*



class Rec_password_activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rec_password_activity)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Recupera la contraseña"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        //auth = FirebaseAuth.getInstance()

        but_listener()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun but_listener(){

        button.setOnClickListener {
            //Toast.makeText(this, "hola?", Toast.LENGTH_LONG)
            auth = FirebaseAuth.getInstance()

            if(!Emailtext.text.toString().isEmpty()){
                auth!!.sendPasswordResetEmail(Emailtext.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "mensaje enviado", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "el mensaje no se pudo enviar", Toast.LENGTH_LONG).show()
                        }
                    }
            }else{
                Toast.makeText(this, "Has de posar el teu email", Toast.LENGTH_LONG).show()
            }


        }
    }
}
