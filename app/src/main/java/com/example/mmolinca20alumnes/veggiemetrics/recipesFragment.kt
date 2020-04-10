package com.example.mmolinca20alumnes.veggiemetrics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_receptes_Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_recipes.*
import models.recepta_model

/**
 * A simple [Fragment] subclass.
 */
class recipesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    //base de dades a firebase:
    private lateinit var databaseReference: DatabaseReference

    private var llistaReceptes = ArrayList<recepta_model>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codi per pujar a firebase receptes de proba:
        /*databaseReference = FirebaseDatabase.getInstance().getReference("receptes")
        val updates = HashMap<String,Any>()
        updates.put("recepta_01", recepta_model("Risoto", "Mario"));
        updates.put("recepta_02", recepta_model("Macarrons", "Pere"));
        updates.put("recepta_03", recepta_model("Tofu", "Aurelio"));
        updates.put("recepta_04", recepta_model("Paella", "Aurelio"));
        updates.put("recepta_05", recepta_model("Fideua", "Unknown"));
        updates.put("recepta_06", recepta_model("Schnitzel", "Aurelio"));
        databaseReference.updateChildren(updates)*/

        llistaReceptes = arrayListOf()
        databaseReference = FirebaseDatabase.getInstance().getReference("receptes")
        //carregar la llista de receptes del db firebase:
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (recipe in p0.children) {
                        val nom = recipe.child("recepta").getValue().toString()
                        val autor = recipe.child("autor").getValue().toString()
                        llistaReceptes.add(recepta_model(nom, autor))
                    }
                }
                llista.layoutManager = LinearLayoutManager(activity)
                llista.adapter = llista_receptes_Adapter(llistaReceptes)
            }
        })
        //SearchView per les receptes:
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (llista.adapter as llista_receptes_Adapter).filter.filter(newText)
                return false
            }

        })

    }//onViewCreated

    fun setUser(userloged : FirebaseAuth){
        auth = userloged
    }

    private fun addToFavs(){

    }

}
