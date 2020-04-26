package com.example.mmolinca20alumnes.veggiemetrics.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mmolinca20alumnes.veggiemetrics.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.ingredient_row.view.*
import models.Aliment
import models.Ingredient

class llista_ingredients_adapter(llista: ArrayList<Ingredient>, c : Context, list: OnItemClickListener): RecyclerView.Adapter<llista_ingredients_adapter.ViewHolder>() {

    private var llista_interna: ArrayList<Ingredient>
    private var context: Context
    private var layoutinf :LayoutInflater
    private var list_intern: OnItemClickListener

    interface OnItemClickListener {
        fun onItemCLick(o: Ingredient)
    }

    init {
        llista_interna = llista
        context = c
        layoutinf = LayoutInflater.from(context)
        list_intern = list
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        var view : View

        init {
            this.view = v
        }

        fun bindItems(item: Ingredient){
            view.text_a_mostrar.text = item.aliment.nom
            view.qty.text = item.qty.toString()
            view.unitats.text = item.unitat
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): llista_ingredients_adapter.ViewHolder {
        val v = layoutinf.inflate(R.layout.ingredient_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return llista_interna.size
    }

    override fun onBindViewHolder(holder: llista_ingredients_adapter.ViewHolder, position: Int) {
        holder.bindItems(llista_interna[position])

        holder.view.setOnClickListener {
            /*var dialog = BottomSheetDialog(context)
            var view_layout = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, holder.view as ViewGroup ,false)
            view_layout.texte.text = "Aliment seleccionat: " + llista_interna[position].nom

            val unitats = arrayOf("g", "Kg", "peçes")
            view_layout.picker_unitats.displayedValues = unitats
            view_layout.picker_unitats.minValue = 0
            view_layout.picker_unitats.maxValue = unitats.size -1

            view_layout.picker_qty.minValue = 0
            view_layout.picker_qty.maxValue = 1000

            dialog.setContentView(view_layout)
            dialog.show()*/

            list_intern.onItemCLick(llista_interna[position])

        }
    }

    fun add_Ingredient(a: Ingredient){
        if(llista_interna.get(0).aliment.nom.equals(context.getString(R.string.sense_ingredients))){
            llista_interna.removeAt(0)
        }
        llista_interna.add(a)
        notifyDataSetChanged()
    }

    fun update_ingredient(id: String, qty: Int, unitats: String){
        for (i in llista_interna){
            if(i.aliment.codi.equals(id)){
                i.unitat = unitats
                i.qty = qty
            }
        }
        notifyDataSetChanged()
    }

    fun update(){
        notifyDataSetChanged()
    }


}