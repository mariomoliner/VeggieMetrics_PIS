package com.example.mmolinca20alumnes.veggiemetrics


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*

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

    fun loginListener(){
        logInButton.setOnClickListener(){
            var userText = userText.text.toString()
            var passwordText = passwordText.text.toString()

            if(userText.isEmpty() or passwordText.isEmpty()){
                Toast.makeText(this, getString(R.string.camp_buit), Toast.LENGTH_LONG).show()
            }
            else {
                auth.signInWithEmailAndPassword(userText, passwordText)
                    .addOnCompleteListener(this, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, getString(R.string.sessio_iniciada), Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, getString(R.string.error_inici), Toast.LENGTH_LONG).show()
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
}
