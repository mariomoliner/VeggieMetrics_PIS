package com.example.mmolinca20alumnes.veggiemetrics

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_fav_receptes_Adapter
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_receptes_Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_recipes.*
import models.recepta_model


/**
 * A simple [Fragment] subclass.
 */
class homeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    //base de dades a firebase:
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userDatabaseReference: DatabaseReference
    private var llistaReceptes = ArrayList<recepta_model>()
    private var llistaRecomanacions = ArrayList<recepta_model>()
    private var iron = ""
    private var omega = ""
    private var calcium = ""
    private var diet = 4
    private var test = false
    private lateinit var topDatabaseReference: DatabaseReference
    private var llistaTop = ArrayList<recepta_model>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        llistaReceptes.clear()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this!!.activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                111)
        }
        setGUIuser()
        setTop()
        setFavs()
        setRecom()
    }

    fun setGUIuser(){
        if(auth.currentUser?.displayName.equals(null)){
            profileName.text = auth.currentUser!!.email
        }
        else{
            profileName.text = auth.currentUser!!.displayName
        }
    }

    fun setUser(userloged : FirebaseAuth){
        auth = userloged
    }

    private fun setTop(){
        progress_barFav.visibility = View.VISIBLE
        activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        topDatabaseReference = FirebaseDatabase.getInstance().getReference("receptes")
        val top10 = topDatabaseReference.orderByChild("valoracio_mitjana").limitToLast(10)
        llistaTop = arrayListOf()
        top10.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (recipe in p0.children) {
                        val nomRecepta = recipe.child("recepta").getValue().toString()
                        val nomAutor = recipe.child("autor").getValue().toString()
                        val foto = recipe.child("foto").getValue().toString()
                        val tipus = recipe.child("tipus").getValue().toString()
                        var carac = ""
                        for(p in recipe.child("puntsforts").children){
                            carac += "#"+p.child("nom").value.toString() + " "
                            Log.e("puntsforts",p.child("nom").value.toString())
                        }
                        val uuid = recipe.key.toString()

                        llistaTop.add(recepta_model(nomRecepta, nomAutor, foto, uuid, carac, tipus ))
                        //Toast.makeText(activity,nomRecepta, Toast.LENGTH_LONG).show()
                    }
                    //Visualitzar les receptes top//
                    progress_barTop.visibility = View.INVISIBLE
                    ListTopReceptes.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                    ListTopReceptes.adapter = llista_fav_receptes_Adapter(llistaTop)
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }else{
                    progress_barFav.visibility = View.INVISIBLE
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    //TODO: Afegir missatge de "Encara no tens receptes preferides"
                }
            }
        })
    }

    private fun setFavs(){
        progress_barFav.visibility = View.VISIBLE
        activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        databaseReference = FirebaseDatabase.getInstance().getReference("users-data")
            .child(auth.currentUser!!.uid).child("preferides")
        llistaReceptes = arrayListOf()
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (recipe in p0.children) {
                        val nomRecepta = recipe.child("recepta").getValue().toString()
                        val nomAutor = recipe.child("autor").getValue().toString()
                        val foto = recipe.child("foto").getValue().toString()
                        val tipus = recipe.child("tipus").getValue().toString()
                        var carac = ""
                        for(p in recipe.child("puntsforts").children){
                            carac += "#"+p.child("nom").value.toString() + " "
                            Log.e("puntsforts",p.child("nom").value.toString())
                        }
                        val uuid = recipe.key.toString()

                        llistaReceptes.add(recepta_model(nomRecepta, nomAutor, foto, uuid, carac, tipus ))
                        //Toast.makeText(activity,nomRecepta, Toast.LENGTH_LONG).show()
                    }
                    //Visualitzar les receptes preferides:
                    progress_barFav.visibility = View.INVISIBLE
                    ListFavorites.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                    ListFavorites.adapter = llista_fav_receptes_Adapter(llistaReceptes)
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }else{
                    progress_barFav.visibility = View.INVISIBLE
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //TODO: Afegir missatge de "Encara no tens receptes preferides"
                }
            }
        })
    }

    private fun tradueixDieta(d: String): Int {
        var codi = 4
        if (d.equals(getActivity()?.getString(R.string.vegana)))
            codi = 0
        else if (d.equals(getActivity()?.getString(R.string.vegetariana)))
            codi = 1
        else if (d.equals(getActivity()?.getString(R.string.piscivegetariana)))
            codi = 2
        else if (d.equals(getActivity()?.getString(R.string.flexitariana)))
            codi = 3
        return codi
    }

    private fun recoverData() {
        userDatabaseReference = FirebaseDatabase.getInstance().reference.child("users-data").child(auth.currentUser!!.uid)

        //Carregar la llista de receptes del db firebase:
        userDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    if (p0.child("iron").exists()) {
                        diet = tradueixDieta(p0.child("Diet").getValue().toString())
                        iron = p0.child("iron").getValue().toString()
                        omega = p0.child("omega").getValue().toString()
                        calcium = p0.child("calcium").getValue().toString()
                        test = true
                        //Toast.makeText(activity, "", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun setRecom(){
        llistaRecomanacions = arrayListOf()

        progress_barRec.visibility = View.VISIBLE
        activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        recoverData()

        /* val childUpdates = HashMap<String, Any>()
       childUpdates["recomanades"] = llistaRecomanacions
       //actualitzem la info de l'usuari actual:
       databaseReference.child("users-data").child(auth.currentUser!!.uid).updateChildren(childUpdates)
       databaseReference = FirebaseDatabase.getInstance().getReference("users-data")
           .child(auth.currentUser!!.uid).child("recomanades") */

        databaseReference = FirebaseDatabase.getInstance().getReference("receptes")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists() && test) {
                    for (recipe in p0.children) {
                        val nomRecepta = recipe.child("recepta").getValue().toString()
                        val nomAutor = recipe.child("autor").getValue().toString()
                        val foto = recipe.child("foto").getValue().toString()
                        val dieta = recipe.child("tipus").getValue().toString().toInt()
                        val nutrients = ArrayList<String>()
                        for (p in recipe.child("puntsforts").children) {
                            nutrients.add(p.child("nom").value.toString())
                        }

                        // Recomanació principal
                        if (diet >= dieta) {
                            if (iron.equals("Not good") || iron.equals("Bad")) {
                                if ("Iron" in nutrients)
                                    llistaRecomanacions.add(
                                        recepta_model(
                                            nomRecepta,
                                            nomAutor,
                                            foto
                                        )
                                    )
                            }
                            if (omega.equals("Not good") || omega.equals("Bad")) {
                                if ("Omega" in nutrients)
                                    llistaRecomanacions.add(
                                        recepta_model(
                                            nomRecepta,
                                            nomAutor,
                                            foto
                                        )
                                    )
                            }
                            if (calcium.equals("Not good") || calcium.equals("Bad")) {
                                if ("Calcium" in nutrients)
                                    llistaRecomanacions.add(
                                        recepta_model(
                                            nomRecepta,
                                            nomAutor,
                                            foto
                                        )
                                    )
                            }
                        }
                    }

                    // Recomanació secundària
                    if (llistaRecomanacions.size == 0) {
                        for (recipe in p0.children) {
                            val nomRecepta = recipe.child("recepta").getValue().toString()
                            val nomAutor = recipe.child("autor").getValue().toString()
                            val foto = recipe.child("foto").getValue().toString()
                            val dieta = recipe.child("tipus").getValue().toString().toInt()
                            if (diet >= dieta) {
                                llistaRecomanacions.add(
                                    recepta_model(
                                        nomRecepta,
                                        nomAutor,
                                        foto
                                    )
                                )
                            }
                        }
                    }

                    //Visualitzar les receptes recomanades:
                    progress_barRec.visibility = View.INVISIBLE
                    ListRecomended.layoutManager =
                        LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                    ListRecomended.adapter = llista_fav_receptes_Adapter(llistaRecomanacions)
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    progress_barRec.visibility = View.INVISIBLE
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    RecomendedText.visibility = View.VISIBLE
                }
            }
        })
    }
    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.actionbar_home, menu)
    }*/
/*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Benvingut"
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actionbar_home, menu)
    }*/




}