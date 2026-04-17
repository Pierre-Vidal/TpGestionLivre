package com.example.TpGestionLivre.infrastructure.driven.postgres

import com.example.TpGestionLivre.domain.model.Livre
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIT : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var bookDAO: BookDAO

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }
    }

    init {
        beforeEach {
            jdbcTemplate.update("DELETE FROM livre", mapOf<String, Any>())
        }

        afterSpec {
            container.stop()
        }

        test("save devrait insérer un livre dans la base de données") {
            // Arrange
            val livre = Livre(titre = "Harry Potter", auteur = "J.K Rowling")

            // Act
            bookDAO.save(livre)

            // Assert
            val livres = bookDAO.findAll()
            livres.size shouldBe 1
            livres[0].titre shouldBe "Harry Potter"
            livres[0].auteur shouldBe "J.K Rowling"
        }

        test("findAll devrait retourner tous les livres de la base de données") {
            // Arrange
            bookDAO.save(Livre(titre = "Harry Potter", auteur = "J.K Rowling"))
            bookDAO.save(Livre(titre = "Les Misérables", auteur = "Victor Hugo"))

            // Act
            val livres = bookDAO.findAll()

            // Assert
            livres.size shouldBe 2
        }

        test("findAll quand la base est vide devrait retourner une liste vide") {
            // Act
            val livres = bookDAO.findAll()

            // Assert
            livres shouldBe emptyList()
        }
    }
}
