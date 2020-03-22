package models

class recepta_model{
    private val nom: String
    private val autor: String

    constructor(nom: String, autor: String){
        this.nom = nom
        this.autor = autor
    }

    fun get_recepta(): String{
        return this.nom
    }

    fun get_autor(): String{
        return this.autor
    }

}