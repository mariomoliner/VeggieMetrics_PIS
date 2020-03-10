package com.example.mmolinca20alumnes.veggiemetrics

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class perfil_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
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
