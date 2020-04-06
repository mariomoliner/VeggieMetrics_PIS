package com.example.mmolinca20alumnes.veggiemetrics

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_fav_receptes_Adapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import models.recepta_fav_model


/**
 * A simple [Fragment] subclass.
 */
class homeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private val llistaReceptes = ArrayList<recepta_fav_model>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        llistaReceptes.add(recepta_fav_model("Risoto","Mario", R.drawable.recipe_pic))
        llistaReceptes.add(recepta_fav_model("tofu", "Aurelio", R.drawable.recipe_pic))
        llistaReceptes.add(recepta_fav_model("paella", "Aurelio", R.drawable.recipe_pic))
        llistaReceptes.add(recepta_fav_model("fideua", "unknown", R.drawable.recipe_pic))
        llistaReceptes.add(recepta_fav_model("schnitzel", "Aurelio", R.drawable.recipe_pic))


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

        //Visualitzar les receptes preferides:
        ListFavorites.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        ListFavorites.adapter = llista_fav_receptes_Adapter(llistaReceptes)
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