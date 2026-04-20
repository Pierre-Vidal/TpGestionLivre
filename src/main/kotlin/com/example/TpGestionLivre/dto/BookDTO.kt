package com.example.TpGestionLivre.dto

data class BookDTO(
    val id: Int?,
    val titre: String,
    val auteur: String,
    val disponible: Boolean
)

data class ReservationDTO(
    val reservePar: String
)
