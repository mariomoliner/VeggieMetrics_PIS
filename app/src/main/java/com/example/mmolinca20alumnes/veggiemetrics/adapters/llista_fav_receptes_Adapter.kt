package com.example.mmolinca20alumnes.veggiemetrics.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mmolinca20alumnes.veggiemetrics.R
import com.example.mmolinca20alumnes.veggiemetrics.recipe
import kotlinx.android.synthetic.main.recepta_fav_concreta.view.*
import models.recepta_fav_model
import models.recepta_model
import java.util.*
import kotlin.collections.ArrayList

class llista_fav_receptes_Adapter (val itemsFav: ArrayList<recepta_model>) : RecyclerView.Adapter<llista_fav_receptes_Adapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //enllacem les receptes:
        fun bindItems(model: recepta_model) {
            itemView.receptaFav.text = model.getRecepta()
            itemView.autorFav.text ="Autor: "+model.getAutor()
            //TODO:cal afegir imatge
        }
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recepta_fav_concreta, parent, false)
        )
    }

    // Gets the number of recipes in the fav list
    override fun getItemCount(): Int {
        return itemsFav.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(itemsFav[position])
        /*
        holder.itemView.setOnClickListener {
            selectedPosition=position
            notifyDataSetChanged()
            val intent = Intent(holder.itemView.getContext(), recipe::class.java)
            System.out.println(itemsFav[position].get_recepta())
            intent.putExtra("nom_recepta",itemsFav[position].get_recepta())
            holder.itemView.context.startActivity(intent)
        }*/
    }
}
