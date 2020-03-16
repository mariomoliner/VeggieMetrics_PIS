package com.example.mmolinca20alumnes.veggiemetrics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.register.*

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        auth = FirebaseAuth.getInstance()

        listener_boton()
    }

    fun listener_boton(){
        boton_conf.setOnClickListener(){
            if(pass.text.toString().isEmpty() or passrepetida.text.toString().isEmpty() or correo.text.toString().isEmpty()){
                Toast.makeText(this, "Cap parametre pot quedar buit!", Toast.LENGTH_LONG).show()
            }
            else if(!pass.text.toString().equals(passrepetida.text.toString())){
                Toast.makeText(this, "Les contraseñes han de coincidir!", Toast.LENGTH_LONG).show()
            }
            else{
                auth.createUserWithEmailAndPassword(correo.text.toString(), pass.text.toString()).addOnCompleteListener(this, OnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Registrat correctament", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, LogIn::class.java)
                        startActivity(intent)
                        finish()
                    }else {
                        Toast.makeText(this, "No ha sigut possible Registrar-te", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }
}
