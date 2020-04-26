package models

import android.net.Uri

class recepta_detall(nom: String, autor: String, d: String) {

    var nom_recepta: String
    var autor_recepta: String
    var description: String

    lateinit var llista_ingredients: ArrayList<Ingredient>

    init {
        this.description = d
        nom_recepta = nom
        autor_recepta = autor
        llista_ingredients = arrayListOf()
    }

    override fun toString(): String {
        return super.toString()
    }

    fun addIngredient(i: Ingredient){
        llista_ingredients.add(i)
    }

    fun getIngredient_ID(id: String) : Ingredient?{
        for (i in llista_ingredients){
            if(i.aliment.codi.equals(id)){
                return i
            }
        }
        return null
    }
}