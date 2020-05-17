package com.example.mmolinca20alumnes.veggiemetrics

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.text.BoringLayout
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mmolinca20alumnes.veggiemetrics.APIcall.LogOutFragment
import com.example.mmolinca20alumnes.veggiemetrics.adapters.sharedprefs
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ActionItemTarget
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_recipe.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.profileName

class MainActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isLogged()
        ask_permissions()

        //Primer fragment:
        var fragment : Fragment
        fragment = homeFragment()
        fragment.setUser(auth)
        loadFragment(fragment)
        supportActionBar!!.title = getString(R.string.home)
        navegador_inferior.setOnNavigationItemSelectedListener { menuItem ->
            when {
                menuItem.itemId == R.id.home_nav -> {
                    fragment.setUser(auth)
                    loadFragment(fragment)
                    supportActionBar!!.title = getString(R.string.home)
                    return@setOnNavigationItemSelectedListener true
                }
                menuItem.itemId == R.id.receptes_nav -> {
                    var fragmentR = recipesFragment()
                    fragmentR.setUser(auth)
                    loadFragment(fragmentR)
                    supportActionBar!!.title = getString(R.string.receptes)
                    return@setOnNavigationItemSelectedListener true
                }
                menuItem.itemId == R.id.perfil_nav -> {
                    var fragmentP = profileFragment()
                    fragmentP.setUser(auth)
                    loadFragment(fragmentP)
                    supportActionBar!!.title = getString(R.string.perfil)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }


        }

        mostra()



    }
    private fun loadFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().also { fragmentTransaction ->
            fragmentTransaction.replace(R.id.fragment_principal, fragment)
            fragmentTransaction.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.actionbar_home, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.plus_butt) {
            startActivity(Intent(this, NewRecipe::class.java))
            return true
        }

        if (id == R.id.exit) {
            val newFragment = LogOutFragment()
            newFragment.show(supportFragmentManager, "logOut")
            return true
        }
/*
        if (id == R.id.op1) {
            //cal detallar les funcionalitats
            return true
        }
        if (id == R.id.op2) {
            //cal detallar les funcionalitats
            return true
        }
        if (id == R.id.op3) {
            val newFragment = LogOutFragment()
            newFragment.show(supportFragmentManager, "logOut")
        }*/

        return super.onOptionsItemSelected(item)

    }

    private fun isLogged(): Boolean{
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            return false;
        }
        else{
            return true;
        }
    }

    private fun ask_permissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                111)
        }
    }


    private fun mostra(){
        var m: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(m)

        if(sharedprefs().getTutorialStatus1(this) != true){

            TapTargetSequence(this).targets(TapTarget.forBounds(Rect(m.widthPixels-200,0,m.widthPixels-100,200), getString(R.string.guia_afegir_recepta)).outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                .targetCircleColor(R.color.white)
                .transparentTarget(true)// Specify a color for the target circle
                .titleTextSize(20) // Specify the size (in sp) of the title text
                .titleTextColor(R.color.white)).start()

            sharedprefs().storeTutorialStatus1(this, true)

        }

    }
}