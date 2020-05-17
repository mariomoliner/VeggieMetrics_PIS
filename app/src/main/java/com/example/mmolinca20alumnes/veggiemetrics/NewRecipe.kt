package com.example.mmolinca20alumnes.veggiemetrics

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
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
import com.bumptech.glide.Glide
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_ingredients_adapter
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_ingredients_adapter.OnItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_new_recipe.*
import kotlinx.android.synthetic.main.activity_new_recipe.progress_bar
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import logic_deficiencies.Engine
import models.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class NewRecipe : AppCompatActivity() {

    lateinit var receptaUUID: String //Unic User ID per la recepta
    var selectedPhotoUri: Uri? = null //Uri de la foto de la recepta
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth //Necessitem extreure el nom de l'autor
    private lateinit var ingredientList_adapter: llista_ingredients_adapter
    private lateinit var nova_Recepta: recepta_detall
    private lateinit var report: ArrayList<Engine.report>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = getString(R.string.nova_recepta)
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        nova_Recepta = recepta_detall("sense nom","mario","")


        init_recycler()
        search_listener()
        recipePic_listener() //per canviar la foto de la recepta
        post_listener() // per publicar la recepta sencera
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
        llista_incial.add(Ingredient(Aliment("-1",getString(R.string.sense_ingredients), null),0, getString(R.string.grams)))

        ingredientList_adapter = llista_ingredients_adapter(llista_incial, this, object : OnItemClickListener {
                override fun onItemCLick(o: Ingredient) {
                    var dialog = BottomSheetDialog(this@NewRecipe)
                    var view_layout = layoutInflater.inflate(R.layout.bottom_sheet_dialog,null)
                    view_layout.texte.text = getString(R.string.aliment_seleccionat) + o.aliment.nom

                    val unitats: Array<String>

                    if(o.aliment.codi.equals("-1")){
                        unitats = arrayOf("g", "Kg", getString(R.string.peces))
                    }
                    else{
                        val list = o.aliment.units?.map { it.name}
                        unitats = list?.toTypedArray()!!
                    }

                    //val unitats = arrayOf("g", "Kg", "peces")
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

            override fun onclearClick(p: Int) {
                Toast.makeText(applicationContext, "ingredient eliminat", Toast.LENGTH_LONG).show()

                if(nova_Recepta.llista_ingredients.size > p){
                    nova_Recepta.llista_ingredients.removeAt(p)
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


                var sub = response.getJSONArray("foods")
                for(i in 0..sub.length()-1){
                    var subb = sub.getJSONObject(i)
                    //Log.e("sd ", subb.getString("description") + " "+ subb.getString("fdcId") + " "+ subb.getString("dataType")
                    //+ " " + subb.getString("brandOwner"))
                    //llista.add(subb.getString("description") + " "+ subb.getString("dataType"))
                    llista.add(Aliment(subb.getString("fdcId"),subb.getString("description"),
                        null))

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


            val nou_ingredient = Ingredient(Aliment(llista.get(which).codi,list[which],pedir_data_unitats(llista.get(which).codi)),0, "grams")
            nova_Recepta.addIngredient(nou_ingredient)
            (ingredientsList.adapter as llista_ingredients_adapter).add_Ingredient(nou_ingredient)
            //pedir_data_unitats(171689.toString())
            dialog.dismiss()
        }

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
            /*val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            recipePic.setImageBitmap(bitmap)*/
            Glide.with(this).load(selectedPhotoUri).centerCrop().into(recipePic)
        }
    }

    //Penjem la nova recepta a firebase
    private fun post_listener(){

        //coses a afegir al setonclick

        /*
        post.setOnClickListener(){
            //Penjem la foto de la recepta al nostre Storage de Firebase:
            uploadImageToFirebaseStorage()
            Toast.makeText(this, "Cal omplir totes les dades", Toast.LENGTH_LONG).show()
        }*/


        /*post.setOnClickListener {
            val crida_engine = Engine(nova_Recepta, this, object: Engine.OnFinishListener{
                override fun ONfinish(a: ArrayList<Engine.report>) {
                    for(i in a ){
                        Log.e("printllista", i.nom +" percentatge " + i.qty_percent + "%")
                    }
                    report = a
                    //uploadImageToFirebaseStorage()
                }

            })

            crida_engine.print_rdis()
        }*/


        post.setOnClickListener {
            /*val a = Engine(nova_Recepta, this, object: Engine.OnFinishListener{
                override fun ONfinish(a: ArrayList<Engine.report>) {
                    for(i in a ){
                        Log.e("printllista", i.nom +" percentatge " + i.qty_percent + "%")
                    }
                }

            })
            a.print_rdis()*/

                val crida_engine = Engine(nova_Recepta, this, object: Engine.OnFinishListener{
                    override fun ONfinish(a: ArrayList<Engine.report>) {
                        for(i in a ){
                            Log.e("printllista", i.nom +" percentatge " + i.qty_percent + "%")
                        }
                        report = a

                        uploadImageToFirebaseStorage()
                    }

                })

            if(recipeTitle.text.isEmpty() || stepsEditText.text.isEmpty() || nova_Recepta.llista_ingredients.size == 0 || !isCheckedRadiobutton()){
                Toast.makeText(this,getString(R.string.omplir_dades), Toast.LENGTH_LONG).show()
            }else{
                nova_Recepta.nom_recepta = recipeTitle.text.toString()
                nova_Recepta.description = stepsEditText.text.toString()
                progress_bar.visibility = View.VISIBLE

                this.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                crida_engine.print_rdis()
                //Log.e("fdf", findViewById<RadioButton>(gruptipus.checkedRadioButtonId).text.toString())
            }
        }



    }
    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null){
            Toast.makeText(this,getString(R.string.falta_foto), Toast.LENGTH_LONG).show()
            return
        }
        receptaUUID = UUID.randomUUID().toString()
        auth = FirebaseAuth.getInstance()
        val ref = FirebaseStorage.getInstance().getReference("/receptes/$receptaUUID")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                saveRecipeToFirebaseDatabase(it.toString(), this)
            }.addOnFailureListener {
                    Toast.makeText(this, getString(R.string.error_imatge), Toast.LENGTH_LONG).show()
                progress_bar.visibility = View.INVISIBLE
                this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private fun saveRecipeToFirebaseDatabase(uri_image :String, c: Context){
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("receptes")
        val updates = HashMap<String,Any>()
        var nomAutor = ""
        if(!auth.currentUser!!.displayName.equals(null)){
            nomAutor = auth.currentUser!!.displayName.toString()
        }

        val nomRecepta = recipeTitle.text.toString()
        //updates.put(receptaUUID, nova_Recepta)
        var u = recepta_model(nomRecepta, nomAutor, uri_image)

        var tipus_recept = findViewById<RadioButton>(gruptipus.checkedRadioButtonId).text.toString()

        updates.put("/$receptaUUID/recepta_detall", nova_Recepta)
        updates.put("/$receptaUUID/recepta", u.getRecepta())
        updates.put("/$receptaUUID/autor", u.getAutor())
        updates.put("/$receptaUUID/foto", u.getFoto())
        updates.put("/$receptaUUID/puntsforts", report)
        updates.put("/$receptaUUID/tipus", tipus_recept)
        /*Afageixo valoració mitjana i num_vots*/
        updates.put("/$receptaUUID/valoracio_mitjana",0)
        updates.put("/$receptaUUID/num_vots",0)


        databaseRef.updateChildren(updates, object: DatabaseReference.CompletionListener{
            override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {
                progress_bar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Toast.makeText(c, getString(R.string.foto_ok), Toast.LENGTH_LONG).show()

            }

        })
    }




    private fun pedir_data_unitats(id: String) : ArrayList<Unitat>{
        val url = "https://api.nal.usda.gov/fdc/v1/food/$id?api_key=WegyBxbDgsKNbAlSnCerYOogsyuwetZtuNolIz28"

        var llista: ArrayList<Unitat> = arrayListOf()

        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }

        llista.add(Unitat(getString(R.string.grams),"1"))

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                var sub = response.getJSONArray("foodPortions")
                for(i in 0..sub.length()-1){
                    llista.add(Unitat(sub.getJSONObject(i).getString("modifier"),sub.getJSONObject(i).getString("gramWeight")))

                }
            },
            Response.ErrorListener { error ->
                // TODO: Handle error
            }
        )

        requestQueue.add(jsonObjectRequest)
        return llista
    }

    private fun isCheckedRadiobutton(): Boolean{
        return gruptipus.checkedRadioButtonId != -1
    }


}
