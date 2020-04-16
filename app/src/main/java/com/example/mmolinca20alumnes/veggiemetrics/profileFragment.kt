package com.example.mmolinca20alumnes.veggiemetrics

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.renderscript.Sampler
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.common.internal.Objects.equal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass.
 */
class profileFragment : Fragment() {

    val TEST_REQUEST = 1

    private lateinit var auth: FirebaseAuth

    //Components del layout modificables:
    val PICK_PHOTO = 1111
    val REQUEST_IMAGE_CAPTURE = 2222
    lateinit var imagepicked: Uri
    lateinit var list_sex: List<String>
    lateinit var list_dietas: List<String>
    lateinit var list_allergy: List<String>
    lateinit var list_pregnant: List<String>
    //base de dades a firebase:
    private lateinit var database: DatabaseReference// ...

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    //Funció que modifica les barres del perfil
    fun testResults(barra: ProgressBar, resultat: String) {
        if (resultat.equals("Ok")) {
            barra.getProgressDrawable().setColorFilter(Color.rgb(0,187,45), android.graphics.PorterDuff.Mode.MULTIPLY)
            barra.progress = 98
        } else if (resultat.equals("Not good")) {
            barra.getProgressDrawable().setColorFilter(Color.rgb(255,255,0), android.graphics.PorterDuff.Mode.MULTIPLY)
            barra.progress = 67
        } else if (resultat.equals("Bad")) {
            barra.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.MULTIPLY)
            barra.progress = 33
        } else {
            barra.progress = 0
        }
    }

    //Funció que obté els resultats del test
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==TEST_REQUEST) {
            if (resultCode== Activity.RESULT_OK) {
                val results = data!!.getStringArrayListExtra("Resultats")
                testResults(proteines, results[0])
                testResults(ferro, results[1])
                testResults(omega, results[2])
                testResults(calci, results[3])
                comentaris.text = results[4]

                val childUpdates = HashMap<String, Any>()

                childUpdates["proteins"] = results[0]
                childUpdates["iron"] = results[1]
                childUpdates["omega"] = results[2]
                childUpdates["calcium"] = results[3]
                childUpdates["comments"] = results[4]

                //actualitzem la info de l'usuari actual:
                database.child("users-data").child(auth.currentUser!!.uid).updateChildren(childUpdates)
            }

        }

        if(requestCode == PICK_PHOTO && resultCode ==  Activity.RESULT_OK && data != null){
            imagepicked = data.data
            //profilePic.setImageURI(imagepicked)
            Glide.with(this).load(imagepicked).centerCrop().into(profilePic)
        }

        // Foto de càmera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
            profilePic.setImageBitmap(imageBitmap)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference

        list_pregnant = resources.getStringArray(R.array.Pregnant).toList()
        list_dietas = resources.getStringArray(R.array.Dietes).toList()
        list_sex = resources.getStringArray(R.array.Sex).toList()
        sex.adapter = ArrayAdapter(activity,android.R.layout.simple_spinner_item,list_sex)
        diet.adapter = ArrayAdapter(activity,android.R.layout.simple_spinner_item,list_dietas)
        pregnant.adapter = ArrayAdapter(activity,android.R.layout.simple_spinner_item,list_pregnant)


        /*val adapter = ArrayAdapter(activity,
            android.R.layout.simple_spinner_item, llista_dietas)
        diet.adapter = adapter
        diet.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, llista_sexes)*/

        //profileMail.text = auth.currentUser!!.email


        setGUIuser()

        listener_canvis()
        photo_listener()
        listener_botoTest()
        listener_sex()
    }

    fun setUser(userloged : FirebaseAuth){
        auth = userloged
    }

    //Càlcul del IMC
    fun imcCalcul() {
        val h = height.text.toString().toDouble()
        val w = weight.text.toString().toDouble()

        if (h*w == 0.0) {
            imc_text.setText("Sense dades")
            imc_number.setText("")
        }
        else {
            val imcResult = w/(h*h)
            imc_number.setText(String.format("%.2f", imcResult).toString())
            if (imcResult < 18.5)
                imc_text.setText("Pes insuficient")
            else if (imcResult < 25)
                imc_text.setText("Pes normal")
            else if (imcResult < 27)
                imc_text.setText("Sobrepès grau I")
            else if (imcResult < 30)
                imc_text.setText("Sobrepès grau II")
            else if (imcResult < 35)
                imc_text.setText("Obesitat grau I")
            else if (imcResult < 40)
                imc_text.setText("Obesitat grau II")
            else if (imcResult < 50)
                imc_text.setText("Obesitat grau III")
            else
                imc_text.setText("Obesitat grau IV")
        }
    }

    fun setGUIuser(){
        progress_bar.visibility = View.VISIBLE
        activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        //mostrem el mail de l'usuari al seu perfil:
        profileMail.text = auth.currentUser!!.email
        if(!auth.currentUser!!.displayName.equals(null)){
            //mostrem el nom de l'usuari al seu perfil:
            profileName.setText(auth.currentUser!!.displayName)
        }

        //mostrem foto de perfil de l'usuari:
        if(auth.currentUser!!.photoUrl != null){
            //profilePic.setImageURI(auth.currentUser!!.photoUrl)
            Glide.with(this).load(auth.currentUser!!.photoUrl).centerCrop().into(profilePic)
        }

        //uid: user identification
        var dataref = database.child("users-data").child(auth.currentUser!!.uid)

        dataref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            //carreguem les dades del db: alçada, pes, edat, dieta, sexe, embaràs, al·lèrgies, l'IMC i la informació nutricional
            override fun onDataChange(p0: DataSnapshot) {
                /* height.setText(p0.child("height").getValue().toString())
                weight.setText(p0.child("weight").getValue().toString()) */

                if (p0.child("height").getValue().toString().equals("null"))
                    height.setText("0.0")
                else
                    height.setText(p0.child("height").getValue().toString())

                if (p0.child("weight").getValue().toString().equals("null"))
                    weight.setText("0.0")
                else
                    weight.setText(p0.child("weight").getValue().toString())

                imcCalcul()


                if (p0.child("age").getValue().toString().equals("null"))
                    age.setText("0")
                else
                    age.setText(p0.child("age").getValue().toString())

                when(p0.child("Diet").getValue()){
                    null -> diet.setSelection(0);
                    else -> diet.setSelection(get_Selector_int(p0.child("Diet").getValue().toString(),
                        list_dietas as ArrayList<String>
                    ))
                }
                when(p0.child("Gender").getValue()){
                    null -> sex.setSelection(0);
                    else -> sex.setSelection(get_Selector_int(p0.child("Gender").getValue().toString(),
                        list_sex as ArrayList<String>
                    ))
                }
                when(p0.child("Pregnant").getValue()){
                    null -> pregnant.setSelection(1);
                    else -> pregnant.setSelection(get_Selector_int(p0.child("Pregnant").getValue().toString(),
                        list_pregnant as ArrayList<String>
                    ))
                }
                when(p0.child("Allergy1").getValue()){
                    null -> allergy1.isChecked = false
                    else -> allergy1.isChecked = p0.child("Allergy1").getValue().toString().toBoolean()
                }
                when(p0.child("Allergy2").getValue()){
                    null -> allergy2.isChecked = false
                    else -> allergy2.isChecked = p0.child("Allergy2").getValue().toString().toBoolean()
                }
                when(p0.child("Allergy3").getValue()){
                    null -> allergy3.isChecked = false
                    else -> allergy3.isChecked = p0.child("Allergy3").getValue().toString().toBoolean()
                }
                when(p0.child("Allergy4").getValue()){
                    null -> allergy4.isChecked = false
                    else -> allergy4.isChecked = p0.child("Allergy4").getValue().toString().toBoolean()
                }
                when(p0.child("Allergy5").getValue()){
                    null -> allergy5.isChecked = false
                    else -> allergy5.isChecked = p0.child("Allergy5").getValue().toString().toBoolean()
                }
                when(p0.child("Allergy6").getValue()){
                    null -> allergy6.isChecked = false
                    else -> allergy6.isChecked = p0.child("Allergy6").getValue().toString().toBoolean()
                }
                when(p0.child("Allergy7").getValue()){
                    null -> allergy7.isChecked = false
                    else -> allergy7.isChecked = p0.child("Allergy7").getValue().toString().toBoolean()
                }
                when(p0.child("Allergy8").getValue()){
                    null -> allergy8.isChecked = false
                    else -> allergy8.isChecked = p0.child("Allergy8").getValue().toString().toBoolean()
                }
                when(p0.child("Allergy9").getValue()){
                    null -> allergy9.isChecked = false
                    else -> allergy9.isChecked = p0.child("Allergy9").getValue().toString().toBoolean()
                }
                when(p0.child("Allergy10").getValue()){
                    null -> allergy10.isChecked = false
                    else -> allergy10.isChecked = p0.child("Allergy10").getValue().toString().toBoolean()
                }
                when(p0.child("proteins").getValue()){
                    null -> testResults(proteines, "")
                    else -> testResults(proteines, p0.child("proteins").getValue().toString())
                }
                when(p0.child("iron").getValue()){
                    null -> testResults(ferro, "")
                    else -> testResults(ferro, p0.child("iron").getValue().toString())
                }
                when(p0.child("omega").getValue()){
                    null -> testResults(omega, "")
                    else -> testResults(omega, p0.child("omega").getValue().toString())
                }
                when(p0.child("calcium").getValue()){
                    null -> testResults(calci, "")
                    else -> testResults(calci, p0.child("calcium").getValue().toString())
                }
                when(p0.child("comments").getValue()){
                    null -> comentaris.setText("")
                    else -> comentaris.setText(p0.child("comments").getValue().toString())
                }

                progress_bar.visibility = View.INVISIBLE
                activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

        })

    }


    // Foto perfil

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    //Listener del botó pel test setmanal:
    private fun listener_botoTest(){
        test_button.setOnClickListener{
            val intent = Intent(activity, testSetmanal::class.java)
            intent.putExtra("weight", weight.text.toString().toDouble())
            intent.putExtra("sex", sex.selectedItem.toString())
            intent.putExtra("diet", diet.selectedItem.toString())
            intent.putExtra("age", age.text.toString().toInt())
            intent.putExtra("pregnant", pregnant.selectedItem.toString())
            startActivityForResult(intent, TEST_REQUEST)
        }
    }

    //Listener del Spinner sexe:
    private fun listener_sex() {
        sex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (sex.selectedItemPosition == 0) {
                    pregnant.isEnabled = false
                    pregnant.setSelection(1)
                } else if (sex.selectedItemPosition == 1)
                    pregnant.isEnabled = true

            }
        }
    }

    //Listener del botó per desar canvis:
    private fun listener_canvis(){
        update.setOnClickListener {

            if(!profileName.text.toString().equals("Sense nom") || ::imagepicked.isInitialized){
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(profileName.text.toString())

                //Podem fer update de la foto:
                if(::imagepicked.isInitialized) {
                    profileUpdates.setPhotoUri(imagepicked);
                }
                if(height.text.toString().isEmpty() or weight.text.toString().isEmpty()){
                    //Missatge d'error, hi ha camps buits:
                    Toast.makeText(activity, "Cal omplir totes les dades", Toast.LENGTH_LONG).show()
                }else{
                    //Podem fer l'update a la database correctament:
                    val childUpdates = HashMap<String, Any>()
                    //Creem hashmap que omplim amb les dades de l'usuari
                    childUpdates["age"] = age.text.toString()
                    childUpdates["height"] = height.text.toString()
                    childUpdates["weight"] = weight.text.toString()
                    childUpdates["Gender"] = sex.selectedItem.toString()
                    childUpdates["Diet"] = diet.selectedItem.toString()
                    childUpdates["Pregnant"] = pregnant.selectedItem.toString()
                    childUpdates["Allergy1"] = allergy1.isChecked
                    childUpdates["Allergy2"] = allergy2.isChecked
                    childUpdates["Allergy3"] = allergy3.isChecked
                    childUpdates["Allergy4"] = allergy4.isChecked
                    childUpdates["Allergy5"] = allergy5.isChecked
                    childUpdates["Allergy6"] = allergy6.isChecked
                    childUpdates["Allergy7"] = allergy7.isChecked
                    childUpdates["Allergy8"] = allergy8.isChecked
                    childUpdates["Allergy9"] = allergy9.isChecked
                    childUpdates["Allergy10"] = allergy10.isChecked

                    imcCalcul()

                    //actualitzem la info de l'usuari actual:
                    database.child("users-data").child(auth.currentUser!!.uid).updateChildren(childUpdates)

                    auth.currentUser?.updateProfile(profileUpdates.build())
                        ?.addOnCompleteListener { task ->

                         if (task.isSuccessful) {
                             Toast.makeText(activity, "Actualització de dades correcta", Toast.LENGTH_LONG).show()
                         }
                    }
                }
            }else{
                Toast.makeText(activity, "Es necessita un nom d'usuari per desar canvis", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Listener per quan presionem la foto per canviar-la:
    private fun photo_listener(){
        profilePic.setOnClickListener {

            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, PICK_PHOTO);
        }
    }

    private fun get_Selector_int(string: String,llista :ArrayList<String>): Int{
        for(i in llista){
            if(i.equals(string)){
                return llista.indexOf(i)
            }
        }
        return -1
    }



}
