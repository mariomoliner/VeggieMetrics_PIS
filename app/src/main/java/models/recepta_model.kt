package models

class recepta_model{
    private var recepta: String
    private var autor: String
    private var fotoRecepta: String
    private var id: String
    private var caracteristiques: String
    private var tipus: String
    private var valoracio_mitjana: String
/*
    constructor(recepta: kotlin.String, autor: kotlin.String, fotoRecepta: kotlin.String, id: kotlin.String, receptaDetall: models.recepta_detall, s: String){
        this.recepta = recepta
        this.autor = autor
        this.fotoRecepta = fotoRecepta
        this.id = id
    }*/

    constructor(recepta: String, autor: String, fotoRecepta: String, id: String, caracteristiques: String, tipus: String){
        this.recepta = recepta
        this.autor = autor
        this.fotoRecepta = fotoRecepta
        this.id = id
        this.caracteristiques = caracteristiques
        this.tipus = tipus
        this.valoracio_mitjana = ""
    }

    constructor(recepta: String, autor: String, fotoRecepta: String, id: String, caracteristiques: String){
        this.recepta = recepta
        this.autor = autor
        this.fotoRecepta = fotoRecepta
        this.id = id
        this.caracteristiques = caracteristiques
        this.tipus = ""
        this.valoracio_mitjana = ""
    }

    constructor(recepta: String, autor: String, fotoRecepta: String, id: String){
        this.recepta = recepta
        this.autor = autor
        this.fotoRecepta = fotoRecepta
        this.id = id
        this.caracteristiques = ""
        this.tipus = ""
        this.valoracio_mitjana =""
    }
    constructor(recepta: String, autor: String, fotoRecepta: String){
        this.recepta = recepta
        this.autor = autor
        this.fotoRecepta = fotoRecepta
        this.id = ""
        this.caracteristiques = ""
        this.tipus = ""
        this.valoracio_mitjana =""
    }
    /*constructor(recepta: String, autor: String, fotoRecepta: String, valoracio_mitjana:String){
        this.recepta = recepta
        this.autor = autor
        this.fotoRecepta = fotoRecepta
        this.id = ""
        this.caracteristiques = ""
        this.tipus = ""
        this.valoracio_mitjana =valoracio_mitjana
    }*/
    constructor(recepta: String, autor: String){
        this.recepta = recepta
        this.autor = autor
        this.fotoRecepta = ""
        this.id = ""
        this.caracteristiques = ""
        this.tipus = ""
        this.valoracio_mitjana=""

    }

    //Getters:
    //OBS: El nom d'aquestes funcions determina els noms de les subcareptes del db firebase
    fun getRecepta(): String{
        return this.recepta
    }
    fun getAutor(): String{
        return this.autor
    }
    fun getFoto(): String{
        return this.fotoRecepta
    }
    fun getId(): String{
        return this.id
    }
    fun getCaracteristiques(): String{
        return this.caracteristiques
    }

    fun getTipus(): String{
        return tipus
    }
    fun getValoracio():String{
        return valoracio_mitjana
    }
}