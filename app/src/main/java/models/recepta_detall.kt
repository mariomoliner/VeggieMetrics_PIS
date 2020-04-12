package models

import android.net.Uri

class recepta_detall(nom: String, autor: String) {

    val nom_recepta: String
    val autor_recepta: String

    lateinit var llista_aliments: ArrayList<Ingredient>

    init {
        nom_recepta = nom
        autor_recepta = autor
        llista_aliments = arrayListOf()
    }

    override fun toString(): String {
        return super.toString()
    }

    fun addIngredient(i: Ingredient){
        llista_aliments.add(i)
    }

    fun getIngredient_ID(id: String) : Ingredient?{
        for (i in llista_aliments){
            if(i.aliment.codi.equals(id)){
                return i
            }
        }
        return null
    }
}