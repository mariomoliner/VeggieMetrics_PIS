package models

class Rating(valoracio:Int,idUsuari:String,idRecepta:String) {
    val valoracio_recepta: Int
    val idUsuari_rating: String
    val idRecepta_rating: String

    init {
        valoracio_recepta = valoracio
        idUsuari_rating = idUsuari
        idRecepta_rating = idRecepta
    }
}
