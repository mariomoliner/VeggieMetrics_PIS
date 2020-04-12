package models

class recepta_fav_model {
    private val nom: String
    private val autor: String
    private val image: Int

    constructor(nom: String, autor: String, image: Int){
        this.nom = nom
        this.autor = autor
        this.image = image
    }

    fun get_recepta(): String{
        return this.nom
    }

    fun get_autor(): String{
        return this.autor
    }

    fun get_image(): Int{
        return this.image
    }
}