package com.example.mmolinca20alumnes.veggiemetrics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_rec_password_activity.*
import kotlinx.android.synthetic.main.activity_recipe.*
import models.Rating
import models.recepta_model
import java.util.*

class recipe : AppCompatActivity() {
    lateinit var ratingBar: RatingBar
    lateinit var buttonSave: Button
    val user = FirebaseAuth.getInstance().currentUser
    lateinit var ref:DatabaseReference
    lateinit var rid: String
    lateinit var ratingUUID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        ratingBar=findViewById(R.id.ratingReceptes)
        buttonSave=findViewById(R.id.rate)

        buttonSave.setOnClickListener {
            saveRecipe()
        }

        recipe_title.text = intent.getStringExtra("nom_recepta")
    }
    private fun saveRecipe(){
        ratingUUID = UUID.randomUUID().toString()
        ref = FirebaseDatabase.getInstance().getReference("receptes")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0!!.exists()){
                    val recepta = p0.getValue(recepta_model::class.java)
                    rid= recepta!!.getId()
                }
            }

        })
        val uid=user!!.uid
        val ref=FirebaseDatabase.getInstance().getReference("rating")
        val rat = Rating(ratingBar.numStars, uid, rid, ratingUUID)
        ref.child(ratingUUID).setValue(rat).addOnCompleteListener{
            Toast.makeText(applicationContext,"Recepta guardada correctament", Toast.LENGTH_LONG).show()
        }
    }
}
