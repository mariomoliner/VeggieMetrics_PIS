package com.example.mmolinca20alumnes.veggiemetrics.adapters


import com.example.mmolinca20alumnes.veggiemetrics.R
import android.R.layout
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mmolinca20alumnes.veggiemetrics.MainActivity
import com.example.mmolinca20alumnes.veggiemetrics.recipe
import kotlinx.android.synthetic.main.recepta_concreta.view.*
import models.recepta_model
import java.util.*
import kotlin.collections.ArrayList


class llista_receptes_Adapter  (val recipesList: ArrayList<recepta_model>) : RecyclerView.Adapter<llista_receptes_Adapter.ViewHolder>(), Filterable {

    var llista_receptes_filtrada = ArrayList<recepta_model>()

    init {
        llista_receptes_filtrada = recipesList
    }

    var selectedPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: recepta_model) {
            itemView.name.text=item.getRecepta()
            itemView.autor.text="Autor: " + item.getAutor()
            //if(item.is_fav())
                //itemView.fav.setImageResource(R.drawable.ic_fav_picked)
            //else
                itemView.fav.setImageResource(R.drawable.ic_fav_to_pick)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recepta_concreta, parent, false)
        return ViewHolder(v)
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
            holder.itemView.context.startActivity(intent)
        }
        //Botó per afegir recepta a "preferits":
        holder.itemView.fav.setOnClickListener {
            holder.itemView.fav.setImageResource(R.drawable.ic_fav_picked)
        }
    }

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