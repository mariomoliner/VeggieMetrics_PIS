package com.example.mmolinca20alumnes.veggiemetrics


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_new_recipe.*
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView

class LogIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        supportActionBar?.hide()
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        loginListener()
        forgot_password_list()
        register_user_listener()
    }

    public override fun onStart() {
        super.onStart()
        //Mirem si l'usuari ha fet sign in (non-null) i el portem a la pàgina principal si cal
        val currentUser = auth.currentUser
        if(currentUser != null){
            //entrem
            goToMainPage()
        } //Si currentUser == null, l'usuari es manté a la pantalla de login
    }

    fun fuct(v: View){
        //Toast.makeText(this, "fdfd", Toast.LENGTH_LONG).show()
    }

    fun loginListener(){
        logInButton.setOnClickListener(){
            var userText = userText.text.toString()
            var passwordText = passwordText.text.toString()
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if(userText.isEmpty() or passwordText.isEmpty()){
                Toast.makeText(this, getString(R.string.camp_buit), Toast.LENGTH_LONG).show()
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            else {
                auth.signInWithEmailAndPassword(userText, passwordText)
                    .addOnCompleteListener(this, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, getString(R.string.sessio_iniciada), Toast.LENGTH_LONG).show()
                            goToMainPage()
                        } else {
                            Toast.makeText(this, getString(R.string.error_inici), Toast.LENGTH_LONG).show()
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    })
            }
        }
    }

    fun forgot_password_list(){
        forgotPassword.setOnClickListener(){
            val intent = Intent(this, Rec_password_activity::class.java)
            startActivity(intent)
        }
    }

    fun register_user_listener(){
        newAccount.setOnClickListener(){
            var intent = Intent(this,Register::class.java)
            startActivity(intent)
        }
    }

    fun goToMainPage(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
