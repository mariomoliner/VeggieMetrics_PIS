package com.example.mmolinca20alumnes.veggiemetrics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_test_setmanal.*


class testSetmanal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_setmanal)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Test Setmanal"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        sendTest_listener()
    }

    fun sendTest_listener(){
        sendTest.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}