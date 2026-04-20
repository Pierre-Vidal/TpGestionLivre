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
                id = rs.getInt("id"),
                titre = rs.getString("titre"),
                auteur = rs.getString("auteur"),
                reservePar = rs.getString("reserve_par")
            )
        }
    }

    override fun findById(id: Int): Livre? {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM livre WHERE id = :id",
            mapOf("id" to id)
        ) { rs, _ ->
            Livre(
                id = rs.getInt("id"),
                titre = rs.getString("titre"),
                auteur = rs.getString("auteur"),
                reservePar = rs.getString("reserve_par")
            )
        }.firstOrNull()
    }

    override fun reserver(id: Int, reservePar: String) {
        namedParameterJdbcTemplate.update(
            "UPDATE livre SET reserve_par = :reservePar WHERE id = :id",
            mapOf("id" to id, "reservePar" to reservePar)
        )
    }
}
