package com.example.mmolinca20alumnes.veggiemetrics

import android.Manifest
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_fav_receptes_Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import models.recepta_model


/**
 * A simple [Fragment] subclass.
 */
class homeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    //base de dades a firebase:
    private lateinit var databaseReference: DatabaseReference
    private var llistaReceptes = ArrayList<recepta_model>()

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
        setFavs()
    }

    fun setGUIuser(){
        if(auth.currentUser?.displayName.equals(null)){
            profileName.text = auth.currentUser!!.email
        }
        else{
            profileName.text = auth.currentUser!!.displayName
        }
    }

    fun setusuari(userloged : FirebaseAuth){
        auth = userloged

    }

    private fun setFavs(){
        progress_barFav.visibility = View.VISIBLE
        activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        
        auth = FirebaseAuth.getInstance()
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
                        llistaReceptes.add(recepta_model(nomRecepta, nomAutor))
                        //Toast.makeText(activity,nomRecepta, Toast.LENGTH_LONG).show()
                    }
                    //Visualitzar les receptes preferides:
                    progress_barFav.visibility = View.INVISIBLE
                    ListFavorites.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                    ListFavorites.adapter = llista_fav_receptes_Adapter(llistaReceptes)
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }else{
                    progress_barFav.visibility = View.INVISIBLE
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    //TODO: Afegir missatge de "Encara no tens receptes preferides"
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