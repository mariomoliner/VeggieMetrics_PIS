package com.example.mmolinca20alumnes.veggiemetrics.adapters


import com.example.mmolinca20alumnes.veggiemetrics.R
import android.R.layout
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmolinca20alumnes.veggiemetrics.MainActivity
import com.example.mmolinca20alumnes.veggiemetrics.recipe
import com.example.mmolinca20alumnes.veggiemetrics.recipesFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_recipe.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.recepta_concreta.view.*
import kotlinx.android.synthetic.main.recepta_concreta.view.autor
import models.recepta_model
import java.util.*
import kotlin.collections.ArrayList


class llista_receptes_Adapter  ( val recipesList: ArrayList<recepta_model>, c: Context) : RecyclerView.Adapter<llista_receptes_Adapter.ViewHolder>(), Filterable {


    private lateinit var auth: FirebaseAuth
    //base de dades a firebase:
    private lateinit var databaseReference: DatabaseReference
    private var c: Context

    var llista_receptes_filtrada = ArrayList<recepta_model>()

    init {
        llista_receptes_filtrada = recipesList
        this.c = c
    }

    var selectedPosition = -1

    class ViewHolder(itemView: View, c: Context) : RecyclerView.ViewHolder(itemView) {
        private lateinit var auth: FirebaseAuth
        private var c: Context
        //base de dades a firebase:
        private lateinit var databaseReference: DatabaseReference

        init {
            this.c  = c
        }

        fun bindItems(item: recepta_model) {
            //Omplim cada fila del recyclerview:
            auth = FirebaseAuth.getInstance()
            databaseReference = FirebaseDatabase.getInstance().getReference("users-data")
                .child(auth.currentUser!!.uid).child("preferides")
            //omplim nom, autor, uuid i url de la foto:
            itemView.name.text=item.getRecepta()
            itemView.autor.text=item.getAutor()
            itemView.uuid_recepta.text = item.getId()
            itemView.url_recepta.text = item.getFoto()
            itemView.caracteristiques.text = item.getCaracteristiques()
            //Afegim foto
            Glide.with(c).load(item.getFoto()).centerCrop().into(itemView.foto_recepta)
            //Picasso.get().load(item.getFoto()).into(itemView.foto_recepta)
            //marquem les receptes preferides:
            val uuidRecepta = itemView.uuid_recepta.text.toString()
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                //carreguem les dades del db: alçada, pes, dieta i sexe_
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(uuidRecepta).exists())
                        itemView.fav.setImageResource(R.drawable.ic_fav_picked)
                    else
                        itemView.fav.setImageResource(R.drawable.ic_fav_to_pick)
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recepta_concreta, parent, false)
        return ViewHolder(v, c)
    }

    override fun getItemCount(): Int {
        return llista_receptes_filtrada.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(llista_receptes_filtrada[position])

        /*if(selectedPosition==position)
            holder.itemView.setBackgroundColor(Color.parseColor("#000000"));
        else
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        */

        //Botó per anar a la recepta escollida:
        holder.itemView.setOnClickListener {
            selectedPosition=position
            notifyDataSetChanged()
            val intent = Intent(holder.itemView.getContext(), recipe::class.java)

            System.out.println(llista_receptes_filtrada[position].getRecepta())
            intent.putExtra("nom_recepta",llista_receptes_filtrada[position].getRecepta())
            intent.putExtra("idrecept",llista_receptes_filtrada[position].getId())
            holder.itemView.context.startActivity(intent)
        }
        //Botó per afegir recepta a "preferits":
        holder.itemView.fav.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            databaseReference = FirebaseDatabase.getInstance().getReference("users-data")
                .child(auth.currentUser!!.uid).child("preferides")

            val nomRecepta = holder.itemView.name.text.toString()
            val autorRecepta = holder.itemView.autor.text.toString()
            val uuidRecepta = holder.itemView.uuid_recepta.text.toString()
            val urlRecepta = holder.itemView.url_recepta.text.toString()
            val caracteristiques = holder.itemView.caracteristiques.text.toString()
            val recepta = recepta_model(nomRecepta, autorRecepta, urlRecepta, uuidRecepta, caracteristiques)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                //carreguem les dades del db: alçada, pes, dieta i sexe_
                override fun onDataChange(p0: DataSnapshot) {
                    //Si ja estava a preferits, la borrem
                    if (p0.child(uuidRecepta).exists()) {
                        //borrem de la db "preferits" la recepta
                        databaseReference.child(uuidRecepta).removeValue()
                        //canviem l'icona
                        holder.itemView.fav.setImageResource(R.drawable.ic_fav_to_pick)
                        //Toast.makeText(holder.itemView.context,nomRecepta, Toast.LENGTH_LONG).show()
                    }else{
                        //afegim recepta a la db "preferits"
                        val childUpdates = HashMap<String, Any>()

                        childUpdates.put(uuidRecepta, recepta)
                        databaseReference.updateChildren(childUpdates)
                        //canviem l'icona
                        holder.itemView.fav.setImageResource(R.drawable.ic_fav_picked)
                    }
                }
            })
        }
    }//onBindViewHolder

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterPattern = constraint.toString().toLowerCase()
                if (constraint?.isEmpty()!!) {
                    llista_receptes_filtrada = recipesList
                } else {
                    //System.out.println("valores filtrados")
                    val resultList = ArrayList<recepta_model>()
                    for (row in recipesList) {
                        if (constraint in row.getRecepta().toLowerCase(Locale.ROOT))  {
                            resultList.add(row)
                            //System.out.println(row.get_autor())
                        }
                    }
                    llista_receptes_filtrada = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = llista_receptes_filtrada
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                llista_receptes_filtrada = results?.values as ArrayList<recepta_model>
                notifyDataSetChanged()
            }

        }
    }

}