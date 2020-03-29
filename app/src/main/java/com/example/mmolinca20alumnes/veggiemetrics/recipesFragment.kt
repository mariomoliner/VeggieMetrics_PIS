package com.example.mmolinca20alumnes.veggiemetrics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.example.mmolinca20alumnes.veggiemetrics.adapters.llista_receptes_Adapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_recipes.*
import models.recepta_model

/**
 * A simple [Fragment] subclass.
 */
class recipesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private val mNicolasCageMovies = listOf(
        recepta_model("Risoto", "Mario Moliner"),
        recepta_model("Macarrons", "Pere"),
        recepta_model("tofu", "Aurelio"),
        recepta_model("paella", "Aurelio"),
        recepta_model("fideua", "unknown"),
        recepta_model("schnitzel", "Aurelio")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        llista.layoutManager = LinearLayoutManager(activity);
        //llista.hasFixedSize()
        llista.adapter = llista_receptes_Adapter((ArrayList(mNicolasCageMovies)))

        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (llista.adapter as llista_receptes_Adapter).filter.filter(newText)
                return false
            }

        })
    }

    fun setUser(userloged : FirebaseAuth){
        auth = userloged
    }

}
