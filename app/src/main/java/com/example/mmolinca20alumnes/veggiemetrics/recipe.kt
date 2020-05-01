package com.example.mmolinca20alumnes.veggiemetrics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_new_recipe.*
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_recipe.ingredientsList
import kotlinx.android.synthetic.main.activity_recipe.recipePic
import models.Rating
import java.util.HashMap

class recipe : AppCompatActivity() {
    lateinit var ratingBar: RatingBar
    lateinit var buttonSave: Button
    val user = FirebaseAuth.getInstance().currentUser
    lateinit var ref:DatabaseReference
    lateinit var reference:DatabaseReference

    lateinit var id_recept: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        ratingBar=findViewById(R.id.ratingReceptes)
        buttonSave=findViewById(R.id.rate)



        recipe_title.text = intent.getStringExtra("nom_recepta")
        id_recept = intent.getStringExtra("idrecept")


        carrega_GUI()

        buttonSave.setOnClickListener {
            saveRecipeRating()
        }
    }


    private fun carrega_GUI(){
        reference = FirebaseDatabase.getInstance().getReference("receptes")
        reference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(i in p0.children){
                        if(i.key.equals(id_recept)){
                            autor.text = autor.text.toString() + i.child("autor").getValue().toString()
                            stepsText.text = i.child("recepta_detall").child("description").value.toString()
                            Glide.with(applicationContext).load(i.child("foto").value.toString()).centerCrop().into(recipePic)

                            var ingredients: String = ""
                            var x = i.child("recepta_detall").child("llista_ingredients")
                            for(j in x.children){
                                Log.e("dfd", j.child("aliment").child("nom").value.toString())
                                ingredients = ingredients + j.child("aliment").child("nom").value.toString() + "  " + j.child("qty").value.toString() + " "+ j.child("unitat").value.toString() + "\n"
                            }
                            ingredientsList.text = ingredients

                            break
                        }
                    }

                }
                else{

                }
            }

        })
    }

    private fun saveRecipeRating(){

        var b = 0
        ref = FirebaseDatabase.getInstance().getReference("rating")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0!!.exists()){
                    b = 0
                    for(i in p0.children){
                        Log.e("df",i.child("idUsuari_rating").value.toString())
                        Log.e("df",i.child("idRecepta_rating").value.toString())
                        if(i.child("idUsuari_rating").value.toString().equals(user!!.uid) && i.child("idRecepta_rating").value.toString().equals(id_recept)){
                            Log.e("df","si")

                            b = 1
                            var d = i.ref
                            Log.e("df","si")
                            //fa falta fer que s'actualitzi el child
                            val updates = HashMap<String,Any>()
                            updates.put("/valoracio_recepta", ratingBar.rating);

                            d.updateChildren(updates, object: DatabaseReference.CompletionListener{
                                override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {
                                    Toast.makeText(applicationContext, getString(R.string.rating_actualitzat), Toast.LENGTH_LONG).show()

                                }
                            })

                        }

                    }
                    if(b == 0){
                        val uid=user!!.uid

                        val ref=FirebaseDatabase.getInstance().getReference("rating")
                        var key = ref.push().key
                        val rat = Rating(ratingBar.rating, uid, id_recept, key.toString())
                        ref.child(key.toString()).setValue(rat).addOnCompleteListener{
                            Toast.makeText(applicationContext,getString(R.string.rating_guardat), Toast.LENGTH_LONG).show()
                        }
                    }


                }
            }

        })

        /*val uid=user!!.uid

        val ref=FirebaseDatabase.getInstance().getReference("rating")
        var key = ref.push().key
        val rat = Rating(ratingBar.rating, uid, id_recept, key.toString())
        ref.child(key.toString()).setValue(rat).addOnCompleteListener{
            Toast.makeText(applicationContext,"Rating guardat correctament", Toast.LENGTH_LONG).show()
        }*/

        if(b == 0){

        }
    }
}
