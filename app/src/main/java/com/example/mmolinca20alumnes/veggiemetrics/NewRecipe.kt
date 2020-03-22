package com.example.mmolinca20alumnes.veggiemetrics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.example.mmolinca20alumnes.veggiemetrics.R


class NewRecipe : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)


        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Registra una nova recepta"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}
