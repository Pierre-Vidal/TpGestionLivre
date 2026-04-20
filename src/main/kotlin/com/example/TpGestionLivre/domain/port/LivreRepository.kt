package com.example.TpGestionLivre.domain.port

import com.example.TpGestionLivre.domain.model.Livre

interface LivreRepository {
    fun save(livre: Livre)
    fun findAll(): List<Livre>
    fun findById(id: Int): Livre?
    fun reserver(id: Int, reservePar: String)
}
