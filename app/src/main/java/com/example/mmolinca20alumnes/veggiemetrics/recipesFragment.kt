package com.example.mmolinca20alumnes.veggiemetrics

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_receptes_Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_recipes.*
import kotlinx.android.synthetic.main.recepta_concreta.*
import models.recepta_model

/**
 * A simple [Fragment] subclass.
 */
class recipesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    //base de dades a firebase:
    private lateinit var databaseReference: DatabaseReference
    private var llistaReceptes = ArrayList<recepta_model>()

    private var filter_per = "nom"
    private var show_per = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_recipes, container, false)
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
        progress_barRV.visibility = View.VISIBLE
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                        val foto = recipe.child("foto").getValue().toString()
                        var tipus = tradueixDieta(recipe.child("tipus").getValue().toString())
                        var carac = ""
                        for(p in recipe.child("puntsforts").children){
                            carac += "#"+p.child("nom").value.toString() + " "
                            Log.e("puntsforts",p.child("nom").value.toString())
                        }

                        val uuid = recipe.key.toString()
                        var r =recepta_model(nom, autor, foto, uuid, carac, tipus)

                        llistaReceptes.add(r)
                    }
                }

                progress_barRV.visibility = View.INVISIBLE
                llista.layoutManager = LinearLayoutManager(activity)
                llista.adapter = llista_receptes_Adapter(llistaReceptes, activity!!)
                activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        })
        //SearchView per les receptes:
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (llista.adapter as llista_receptes_Adapter).filter.filter(newText?.toLowerCase())
                return false
            }

        })

        listener_layout_filtra()
    }//onViewCreated

    fun setUser(userloged : FirebaseAuth){
        auth = userloged
    }

    private fun listener_layout_filtra(){
        filtra.setOnClickListener {
            hidden.visibility =  View.VISIBLE
            overlay.visibility = View.VISIBLE
            llista.alpha = 0.3F


            conf_filtr.setOnClickListener {
                filter_per = view?.findViewById<RadioButton>(radiogroup.checkedRadioButtonId)?.text.toString()
                show_per = view?.findViewById<RadioButton>(radiogroup2.checkedRadioButtonId)?.text.toString()
                hidden.visibility =  View.GONE
                overlay.visibility = View.GONE
                llista.alpha = 1F
                set_param_filtre(filter_per)
                set_show_options(show_per)
            }
        }
    }


    private fun set_param_filtre(s: String){
        if(s.equals("Nom")){
            (llista.adapter as llista_receptes_Adapter).set_param_filtre(0)
        }
        else if(s.equals("Micronutrient")){
            (llista.adapter as llista_receptes_Adapter).set_param_filtre(1)
        }
    }

    private fun set_show_options(s: String){
        (llista.adapter as llista_receptes_Adapter).set_quines_mostro(s)
    }
}