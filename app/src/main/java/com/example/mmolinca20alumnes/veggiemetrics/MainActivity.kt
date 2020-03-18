package com.example.mmolinca20alumnes.veggiemetrics

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Primer fragment:
        loadFragment(homeFragment())

        navegador_inferior.setOnNavigationItemSelectedListener {menuItem ->
            when{
                menuItem.itemId == R.id.home_nav -> {
                    loadFragment(homeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                menuItem.itemId == R.id.receptes_nav -> {
                    loadFragment(recipesFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                menuItem.itemId == R.id.perfil_nav -> {
                    loadFragment(profileFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }
    private fun loadFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().also { fragmentTransaction ->
            fragmentTransaction.replace(R.id.fragment_principal, fragment)
            fragmentTransaction.commit()
        }
    }
}
