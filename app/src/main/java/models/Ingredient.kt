package models

class Ingredient(aliment: Aliment, quantitat: Int, uni: String) {
    val aliment: Aliment
    var qty: Int //en grams
    var unitat: String

    init {
        this.aliment = aliment
        this.qty = quantitat
        this.unitat = uni

    }
}