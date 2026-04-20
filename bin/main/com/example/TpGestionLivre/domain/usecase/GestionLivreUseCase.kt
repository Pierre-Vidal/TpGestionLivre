package com.example.TpGestionLivre.domain.usecase

import com.example.TpGestionLivre.domain.model.Livre
import com.example.TpGestionLivre.domain.port.LivreRepository

class GestionLivreUseCase(private val repository: LivreRepository) {

    fun ajouterLivre(titre: String, auteur: String) {
        require(titre.isNotBlank()) { "Le titre ne peut pas être vide" }
        require(auteur.isNotBlank()) { "L'auteur ne peut pas être vide" }
        repository.save(Livre(titre = titre, auteur = auteur))
    }

    fun listerLivres(): List<Livre> {
        return repository.findAll().sortedBy { it.titre }
    }

    fun reserverLivre(id: Int, reservePar: String) {
        require(reservePar.isNotBlank()) { "Le nom du réservant ne peut pas être vide" }
        val livre = repository.findById(id) ?: error("Livre introuvable")
        require(livre.estDisponible) { "Le livre est déjà réservé" }
        repository.reserver(id, reservePar)
    }

}
