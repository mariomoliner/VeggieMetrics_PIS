package com.example.mmolinca20alumnes.veggiemetrics

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class receptes_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receptes)
    }

    fun home_action(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun receptes_action(view: View){
        val intent = Intent(this, receptes_activity::class.java)
        startActivity(intent)
    }
    fun perfil_action(view: View){
        val intent = Intent(this, perfil_activity::class.java)
        startActivity(intent)
    }
}
