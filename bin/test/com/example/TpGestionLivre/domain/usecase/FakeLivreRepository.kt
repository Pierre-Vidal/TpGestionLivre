package com.example.TpGestionLivre.domain.usecase

import com.example.TpGestionLivre.domain.model.Livre
import com.example.TpGestionLivre.domain.port.LivreRepository

class FakeLivreRepository : LivreRepository {
    private val livres = mutableListOf<Livre>()
    private var nextId = 1

    override fun save(livre: Livre) { livres.add(livre.copy(id = nextId++)) }
    override fun findAll(): List<Livre> = livres.toList()
    override fun findById(id: Int): Livre? = livres.find { it.id == id }
    override fun reserver(id: Int, reservePar: String) {
        val index = livres.indexOfFirst { it.id == id }
        if (index != -1) livres[index] = livres[index].copy(reservePar = reservePar)
    }
}
