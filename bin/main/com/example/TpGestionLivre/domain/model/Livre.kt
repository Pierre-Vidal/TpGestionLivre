package com.example.TpGestionLivre.domain.model

data class Livre(
    val id: Int? = null,
    val titre: String,
    val auteur: String,
    val reservePar: String? = null
) {
    val estDisponible: Boolean get() = reservePar == null
}
