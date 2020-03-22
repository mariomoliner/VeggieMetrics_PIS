package com.example.mmolinca20alumnes.veggiemetrics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_recipe.*

class recipe : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)


        recipe_title.text = intent.getStringExtra("nom_recepta")
    }
}
