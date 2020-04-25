package models

class Aliment(Codialiment: String, Nomaliment: String, l: ArrayList<Unitat>?) {

    val nom: String
    val codi: String
    val units: ArrayList<Unitat>?


    init {
        this.nom = Nomaliment
        this.codi = Codialiment
        this.units = l
    }


}