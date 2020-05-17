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
import java.util.*

class recipe : AppCompatActivity() {
    lateinit var ratingBar: RatingBar
    lateinit var buttonSave: Button
    val user = FirebaseAuth.getInstance().currentUser
    lateinit var ref:DatabaseReference
    lateinit var reference:DatabaseReference
    lateinit var refval:DatabaseReference

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


    private fun tradueixDieta(dieta: String): String {
        var paraula = ""
        if (dieta.equals("0"))
            paraula = getString(R.string.vegana)
        else if (dieta.equals("1"))
            paraula = getString(R.string.vegetariana)
        else if (dieta.equals("2"))
            paraula = getString(R.string.piscivegetariana)
        else if (dieta.equals("3"))
            paraula = getString(R.string.flexitariana)
        return paraula
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
                            autor.text = autor.text.toString() + " " + i.child("autor").getValue().toString()
                            diet.text = diet.text.toString() + " " + tradueixDieta(i.child("tipus").getValue().toString())
                            stepsText.text = i.child("recepta_detall").child("description").value.toString()
                            Glide.with(applicationContext).load(i.child("foto").value.toString()).centerCrop().into(recipePic)

                            var ingredients: String = ""
                            var x = i.child("recepta_detall").child("llista_ingredients")
                            for(j in x.children){
                                Log.e("dfd", j.child("aliment").child("nom").value.toString())
                                ingredients = ingredients + j.child("aliment").child("nom").value.toString() + "  " + j.child("qty").value.toString() + " "+ j.child("unitat").value.toString() + "\n"
                            }
                            ingredientsList.text = ingredients
                            valoraciomitjana()
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
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    b = 0
                    for (i in p0.children) {
                        Log.e("df", i.child("idUsuari_rating").value.toString())
                        Log.e("df", i.child("idRecepta_rating").value.toString())
                        if (i.child("idUsuari_rating").value.toString().equals(user!!.uid) && i.child("idRecepta_rating").value.toString().equals(id_recept)
                        ) {
                            Log.e("df", "si")
                            b = 1
                            var d = i.ref
                            Log.e("df", "si")
                            /*Guardo el rating actual*/
                            /*val rating_vell = i.child("valoracio_recepta").value.toString().toFloat()*/
                            //fa falta fer que s'actualitzi el child
                            val updatesRating = HashMap<String, Any>()
                            updatesRating.put("/valoracio_recepta", ratingBar.rating)
                            /*/*Guardo el rating nou*/
                            val rating_nou = ratingBar.rating
                            /*Creo una referencia per receptes*/
                            refval = FirebaseDatabase.getInstance().getReference("receptes").child(id_recept)
                            refval.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p1: DataSnapshot) {
                                    if (p1.exists()) {
                                        /*Llegeixo la nota mitjana i nombre de vots actual*/
                                        val notaMitjana =
                                            p1.child("valoracio_mitjana").value.toString().toFloat()
                                        val numVots =
                                            p1.child("num_vots").value.toString().toFloat()
                                        /*if(numVots>0){
                                             val notaMitjanaNova = ((notaMitjana * numVots) - rating_vell + rating_nou) / numVots
                                             /*Actualitzo la nota mitjana de la recepta*/
                                             val updatesRatingMig = HashMap<String, Any>()
                                             updatesRatingMig.put("valoracio_mitjana", notaMitjanaNova)
                                         }else{*/
                                        val notaMitjanaNova =
                                            ((notaMitjana * numVots) - rating_vell + rating_nou) / numVots
                                        /*Actualitzo la nota mitjana de la recepta*/
                                        val updatesRatingMig = HashMap<String, Any>()
                                        updatesRatingMig.put("valoracio_mitjana", notaMitjanaNova)

                                    }
                                }
                            })*/
                            d.updateChildren(updatesRating, object : DatabaseReference.CompletionListener {
                                override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {
                                        Toast.makeText(applicationContext, getString(R.string.rating_actualitzat), Toast.LENGTH_LONG).show()

                                    }
                                })

                            valoraciomitjana()

                        }

                    }
                    if (b == 0) {
                        val uid = user!!.uid
                        val ref = FirebaseDatabase.getInstance().getReference("rating")
                        var key = ref.push().key
                        val rat = Rating(ratingBar.rating, uid, id_recept, key.toString())
                        ref.child(key.toString()).setValue(rat).addOnCompleteListener {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.rating_guardat),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        /*/*Guardo el rating nou*/
                        val rating_nou = ratingBar.rating
                        /*Creo una referencia per receptes*/
                        refval = FirebaseDatabase.getInstance().getReference("receptes").child(id_recept)
                        refval.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()) {
                                    for (j in p0.children) {
                                        /*Llegeixo la nota mitjana i nombre de vots actual*/
                                        val notaMitjana =
                                            j.child("valoracio_mitjana").getValue().toString()
                                                .toFloat()
                                        val numVots =
                                            j.child("num_vots").getValue().toString().toInt()
                                        val notaMitjanaNova =
                                            ((notaMitjana * numVots) + rating_nou) / (numVots + 1)
                                        /*Actualitzo la nota mitjana i el nombre de vots*/
                                        val updatesRatingMig = HashMap<String, Any>()
                                        updatesRatingMig.put("valoracio_mitjana", notaMitjanaNova)
                                        updatesRatingMig.put("num_vots", numVots + 1)
                                    }
                                }

                            }
                        })*/
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

    private fun valoraciomitjana(){
        ref = FirebaseDatabase.getInstance().getReference("rating")
        var mitjana=0.0
        var numval=0.0
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (i in p0.children) {
                        Log.e("user_id_df", i.child("idRecepta_rating").value.toString())
                        Log.e("user_id",id_recept)
                        if (i.child("idRecepta_rating").getValue().toString().equals(id_recept)) {
                            numval = numval + 1.0
                            mitjana += i.child("valoracio_recepta").getValue().toString().toFloat()
                        }
                    }
                    mitjana = mitjana / numval
                    refval=FirebaseDatabase.getInstance().getReference("receptes")
                    val updatesRatingMig = HashMap<String, Any>()
                    updatesRatingMig.put("valoracio_mitjana", -mitjana)
                    refval.child(id_recept).updateChildren(updatesRatingMig)
                }
            }
        })
    }
}
