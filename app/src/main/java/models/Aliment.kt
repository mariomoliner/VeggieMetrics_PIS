package models

class Aliment(Codialiment : String, Nomaliment: String) {

    val nom: String
    val codi: String


    init {
        this.nom = Nomaliment
        this.codi = Codialiment
    }


}