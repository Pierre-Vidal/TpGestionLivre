package com.example.TpGestionLivre.infrastructure.driven.postgres

import com.example.TpGestionLivre.domain.model.Livre
import com.example.TpGestionLivre.domain.port.LivreRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : LivreRepository {

    override fun save(livre: Livre) {
        namedParameterJdbcTemplate.update(
            "INSERT INTO livre (titre, auteur) VALUES (:titre, :auteur)",
            mapOf(
                "titre" to livre.titre,
                "auteur" to livre.auteur
            )
        )
    }

    override fun findAll(): List<Livre> {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM livre",
            MapSqlParameterSource()
        ) { rs, _ ->
            Livre(
                titre = rs.getString("titre"),
                auteur = rs.getString("auteur")
            )
        }
    }
}
