package com.example.mmolinca20alumnes.veggiemetrics

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.renderscript.Sampler
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
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
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
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
    lateinit var imagepicked: Uri
    lateinit var list_sex: List<String>
    lateinit var list_dietas: List<String>
    lateinit var list_allergy: List<String>
    //base de dades a firebase:
    private lateinit var database: DatabaseReference// ...

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    //Funció que obté els resultats del test
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==TEST_REQUEST) {
            if (resultCode== Activity.RESULT_OK) {
                val results = data!!.getStringArrayListExtra("Resultats")
                proteines.text = results[0]
                ferro.text = results[1]
                omega.text = results[2]
                calci.text = results[3]
                comentaris.text = results[4]
            }
        }

        //Conflicte
        if(requestCode == PICK_PHOTO && resultCode ==  Activity.RESULT_OK && data != null){
            imagepicked = data.data
            //profilePic.setImageURI(imagepicked)
            Glide.with(this).load(imagepicked).centerCrop().into(profilePic)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference

        list_dietas = resources.getStringArray(R.array.Dietes).toList()
        list_sex = resources.getStringArray(R.array.Sex).toList()
        list_allergy = resources.getStringArray(R.array.Allergy).toList()
        sex.adapter = ArrayAdapter(activity,android.R.layout.simple_spinner_item,list_sex)
        diet.adapter = ArrayAdapter(activity,android.R.layout.simple_spinner_item,list_dietas)
        allergy.adapter = ArrayAdapter(activity,android.R.layout.simple_spinner_item,list_allergy)


        /*val adapter = ArrayAdapter(activity,
            android.R.layout.simple_spinner_item, llista_dietas)
        diet.adapter = adapter
        diet.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, llista_sexes)*/

        //profileMail.text = auth.currentUser!!.email


        setGUIuser()

        listener_canvis()
        photo_listener()
        listener_botoTest()
    }

    fun setUser(userloged : FirebaseAuth){
        auth = userloged
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
            //carreguem les dades del db: alçada, pes, dieta i sexe_
            override fun onDataChange(p0: DataSnapshot) {
                height.setText(p0.child("height").getValue().toString())
                weight.setText(p0.child("weight").getValue().toString())
                when(p0.child("Diet").getValue()){
                    null -> diet.setSelection(0);
                    else -> diet.setSelection(get_Selector_int(p0.child("Diet").getValue().toString(),
                        list_dietas as ArrayList<String>
                    ))
                }
                when(p0.child("Gender").getValue()){
                    null -> diet.setSelection(0);
                    else -> diet.setSelection(get_Selector_int(p0.child("Gender").getValue().toString(),
                        list_sex as ArrayList<String>
                    ))
                }
                progress_bar.visibility = View.INVISIBLE
                activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

        })

    }
    //Listener del botó pel test setmanal:
    private fun listener_botoTest(){
        test_button.setOnClickListener{
            val intent = Intent(activity, testSetmanal::class.java)
            intent.putExtra("weight", weight.text.toString().toDouble())
            intent.putExtra("sex", sex.selectedItem.toString())
            intent.putExtra("diet", diet.selectedItem.toString())
            /* intent.putExtra("age", height.text.toString().toInt())   //Canviar més endavant
            intent.putExtra("pregnant", diet.selectedItem.toString())   //Canviar més endavant  */

            /*intent.putExtra("weight", 65.0)
            intent.putExtra("sex", "Home")
            intent.putExtra("diet", "Flexitarià")*/
            intent.putExtra("age", 22)
            intent.putExtra("pregnant","No")
            startActivityForResult(intent, TEST_REQUEST)
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
                    childUpdates["height"] = height.text.toString()
                    childUpdates["weight"] = weight.text.toString()
                    childUpdates["Gender"] = sex.selectedItem.toString()
                    childUpdates["Diet"] = diet.selectedItem.toString()
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
                Toast.makeText(activity, "No hi han noves dades a actualitzar", Toast.LENGTH_LONG).show()
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
