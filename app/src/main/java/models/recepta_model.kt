package models

class recepta_model{
    private val nom: String
    private val autor: String
    private val favorite: Boolean

    constructor(nom: String, autor: String, favorite: Boolean){
        this.nom = nom
        this.autor = autor
        this.favorite = favorite
    }
    constructor(nom: String, autor: String){
        this.nom = nom
        this.autor = autor
        this.favorite = false;
    }

    fun get_recepta(): String{
        return this.nom
    }

    fun get_autor(): String{
        return this.autor
    }

    fun is_fav(): Boolean{
        return this.favorite
    }

}