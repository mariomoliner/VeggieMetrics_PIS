package models

class recepta_model{
    private var recepta: String
    private var autor: String

    constructor(recepta: String, autor: String){
        this.recepta = recepta
        this.autor = autor
    }
    //Getters:
    //OBS: El nom d'aquestes funcions determina els noms de les subcareptes del db firebase
    fun getRecepta(): String{
        return this.recepta
    }
    fun getAutor(): String{
        return this.autor
    }
}