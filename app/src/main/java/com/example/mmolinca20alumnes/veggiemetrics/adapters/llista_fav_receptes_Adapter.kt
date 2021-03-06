package com.example.mmolinca20alumnes.veggiemetrics.adapters

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mmolinca20alumnes.veggiemetrics.R
import com.example.mmolinca20alumnes.veggiemetrics.recipe
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recepta_fav_concreta.view.*
import models.recepta_model
import kotlin.collections.ArrayList

class llista_fav_receptes_Adapter (val itemsFav: ArrayList<recepta_model>) : RecyclerView.Adapter<llista_fav_receptes_Adapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //enllacem les receptes:
        fun bindItems(model: recepta_model) {
            //Afegim foto
            Picasso.get().load(model.getFoto()).into(itemView.imatgeFav)
            //Afegim el títol de la recepta
            itemView.receptaFav.text = model.getRecepta()
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

        //Botó per anar a la recepta escollida:
        holder.itemView.setOnClickListener {
            selectedPosition=position
            notifyDataSetChanged()
            val intent = Intent(holder.itemView.context, recipe::class.java)

            //Debug lines:
            Log.d("THE ID IS: ", itemsFav[position].getId())
            Log.d("RECIPE NAME:", itemsFav[position].getRecepta())

            intent.putExtra("nom_recepta",itemsFav[position].getRecepta())
            intent.putExtra("idrecept",itemsFav[position].getId())
            holder.itemView.context.startActivity(intent)
        }
    }
}