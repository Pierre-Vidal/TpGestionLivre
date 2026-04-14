package com.example.TpGestionLivre.domain.usecase

import com.example.TpGestionLivre.domain.model.Livre
import com.example.TpGestionLivre.domain.port.LivreRepository

class FakeLivreRepository : LivreRepository {
    private val livres = mutableListOf<Livre>()
    override fun save(livre: Livre) { livres.add(livre) }
    override fun findAll(): List<Livre> = livres.toList()
}
