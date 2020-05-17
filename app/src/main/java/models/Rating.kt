package models

class Rating(valoracio: Float, idUsuari:String, idRecepta:String, id:String) {
    val valoracio_recepta: Float
    val idUsuari_rating: String
    val idRecepta_rating: String
    val id_rating: String
    /*val data_rating: Int*/

    init {
        valoracio_recepta = valoracio
        idUsuari_rating = idUsuari
        idRecepta_rating = idRecepta
        id_rating = id
        /*data_rating = data*/
    }
}
