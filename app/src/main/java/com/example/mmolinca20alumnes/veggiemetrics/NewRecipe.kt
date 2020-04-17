package com.example.mmolinca20alumnes.veggiemetrics

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley .Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_ingredients_adapter
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_ingredients_adapter.OnItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_new_recipe.*
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import models.Aliment
import models.Ingredient
import models.recepta_detall
import models.recepta_model
import java.util.*
import kotlin.collections.ArrayList


class NewRecipe : AppCompatActivity() {

    lateinit var receptaUUID: String //Unic User ID per la recepta
    var selectedPhotoUri: Uri? = null //Uri de la foto de la recepta
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth //Necessitem extreure el nom de l'autor
    private lateinit var ingredientList_adapter: llista_ingredients_adapter
    private lateinit var nova_Recepta: recepta_detall


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Registra una nova recepta"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        //TODO
        nova_Recepta = recepta_detall("Tenerina","Mario")

        init_recycler()
        search_listener()
        recipePic_listener() //per canviar la foto de la recepta
        post_listener() // per publicar la recepta sencera
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun search_listener(){
        searchButton.setOnClickListener {
            pedir_data(searchEdit.text.toString())
        }
    }

    fun init_recycler(){
        /*val llista = ingredientsList

        llista.hasFixedSize()
        llista.layoutManager = LinearLayoutManager(this);*/
        ingredientsList.layoutManager = LinearLayoutManager(this)
        ingredientsList.hasFixedSize()

        var llista_incial : ArrayList<Ingredient> = arrayListOf()
        llista_incial.add(Ingredient(Aliment("-1","Encara no hi ha cap ingredient"),0, "gram"))

        ingredientList_adapter = llista_ingredients_adapter(llista_incial, this, object : OnItemClickListener {
                override fun onItemCLick(o: Ingredient) {
                    var dialog = BottomSheetDialog(this@NewRecipe)
                    var view_layout = layoutInflater.inflate(R.layout.bottom_sheet_dialog,null)
                    view_layout.texte.text = "Aliment seleccionat: " + o.aliment.nom



                    val unitats = arrayOf("g", "Kg", "peçes")
                    view_layout.picker_unitats.displayedValues = unitats
                    view_layout.picker_unitats.minValue = 0
                    view_layout.picker_unitats.maxValue = unitats.size -1

                    view_layout.picker_qty.minValue = 0
                    view_layout.picker_qty.maxValue = 1000

                    dialog.setContentView(view_layout)


                    dialog.show()

                    view_layout.boton_guarda.setOnClickListener {
                        nova_Recepta.getIngredient_ID(o.aliment.codi)?.qty = view_layout.picker_qty.value
                        nova_Recepta.getIngredient_ID(o.aliment.codi)?.unitat = unitats[view_layout.picker_unitats.value]

                        ingredientList_adapter.update_ingredient(o.aliment.codi,view_layout.picker_qty.value, unitats[view_layout.picker_unitats.value])
                        dialog.dismiss()
                    }
                }
            })
        ingredientsList.adapter = ingredientList_adapter


        ingredientsList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

    }



    fun pedir_data(query: String): ArrayList<Aliment>{

        // Instantiate the cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap
        var llista: ArrayList<Aliment> = arrayListOf()
        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val url = "https://api.nal.usda.gov/fdc/v1/foods/search?api_key=WegyBxbDgsKNbAlSnCerYOogsyuwetZtuNolIz28&query=$query&pageSize=20&pageNumber=1&dataType=SR Legacy&sortBy=dataType.keyword&sortOrder=asc"



        /*val url2 = "https://api.nal.usda.gov/fdc/v1/food/470794?api_key=WegyBxbDgsKNbAlSnCerYOogsyuwetZtuNolIz28"
        val url3 =  "https://api.nal.usda.gov/fdc/v1/foods/search?api_key=WegyBxbDgsKNbAlSnCerYOogsyuwetZtuNolIz28"
        var jsonBody =  JSONObject();

        jsonBody.put("query", "apple")
        jsonBody.put("dataType", "Foundation")
        jsonBody.put("pageSize", 15)
        jsonBody.put("pageNumber", 3)*/

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                //Log.e("sd", response.toString(2))


                var sub = response.getJSONArray("foods")
                for(i in 0..sub.length()-1){
                    var subb = sub.getJSONObject(i)
                    //Log.e("sd ", subb.getString("description") + " "+ subb.getString("fdcId") + " "+ subb.getString("dataType")
                    //+ " " + subb.getString("brandOwner"))
                    //llista.add(subb.getString("description") + " "+ subb.getString("dataType"))
                    llista.add(Aliment(subb.getString("fdcId"),subb.getString("description")))

                }

                show_alertdialog(llista)

            },
            Response.ErrorListener { error ->
                // TODO: Handle error
            }
        )

        requestQueue.add(jsonObjectRequest)


        return llista
    }

    private fun show_alertdialog(llista: ArrayList<Aliment>){
        val builder :AlertDialog.Builder = AlertDialog.Builder(this)



        //val list = arrayOf("itme1" , "idw3" , "feif3")
        //val lista: Array<String> = llista.toArray() as Array<String>


        //val list = llista.toTypedArray()

        val list = llista.map { it.nom}
        val l = list.toTypedArray()

        builder.setTitle("Results").setItems(l){dialog, which ->
            Toast.makeText(this, list[which],Toast.LENGTH_LONG).show()
            dialog.dismiss()

            val nou_ingredient = Ingredient(Aliment(llista.get(which).codi,list[which]),0, "grams")
            nova_Recepta.addIngredient(nou_ingredient)
            (ingredientsList.adapter as llista_ingredients_adapter).add_Ingredient(nou_ingredient)}
        builder.show()
    }

    //canviem la foto de la recepta:
    private fun recipePic_listener(){
        recipePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type =  "image/*"
            startActivityForResult(intent, 0)
        }
    }
    //Mètode que és crida amb startActivityForResult:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            recipePic.setImageBitmap(bitmap)
        }
    }

    //Penjem la nova recepta a firebase
    private fun post_listener(){
        post.setOnClickListener(){
            //TODO: Penjar altres parts de la recepta a firebase
            //Penjem la foto de la recepta al nostre Storage de Firebase:
            uploadImageToFirebaseStorage()
            Toast.makeText(this, "Cal omplir totes les dades", Toast.LENGTH_LONG).show()
        }
    }
    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return
        receptaUUID = UUID.randomUUID().toString()
        auth = FirebaseAuth.getInstance()
        val ref = FirebaseStorage.getInstance().getReference("/receptes/$receptaUUID")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                saveRecipeToFirebaseDatabase(it.toString())
            }.addOnFailureListener {
                    Toast.makeText(this, "Error al penjar la imatge", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveRecipeToFirebaseDatabase(profileImageUrl: String){
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("receptes")
        val updates = HashMap<String,Any>()
        var nomAutor = ""
        if(!auth.currentUser!!.displayName.equals(null)){
            nomAutor = auth.currentUser!!.displayName.toString()
        }
        //TODO: Asegurar que el titol no està buit
        val nomRecepta = recipeTitle.text.toString()
        updates.put(receptaUUID, recepta_model(nomRecepta, nomAutor, profileImageUrl));
        databaseRef.updateChildren(updates)
    }
}
