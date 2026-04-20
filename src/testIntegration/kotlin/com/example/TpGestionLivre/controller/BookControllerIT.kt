package com.example.TpGestionLivre.controller

import com.example.TpGestionLivre.domain.model.Livre
import com.example.TpGestionLivre.domain.usecase.GestionLivreUseCase
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(BookController::class)
class BookControllerIT(
    private val mockMvc: MockMvc,
    @MockkBean val gestionLivreUseCase: GestionLivreUseCase
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        test("GET /books devrait retourner la liste des livres avec leur disponibilité") {
            // Arrange
            every { gestionLivreUseCase.listerLivres() } returns listOf(
                Livre(id = 1, titre = "Harry Potter", auteur = "J.K Rowling"),
                Livre(id = 2, titre = "Les Misérables", auteur = "Victor Hugo", reservePar = "Hermione Granger")
            )

            // Act & Assert
            mockMvc.get("/books") {
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json("""[{"id":1,"titre":"Harry Potter","auteur":"J.K Rowling","disponible":true},{"id":2,"titre":"Les Misérables","auteur":"Victor Hugo","disponible":false}]""") }
            }
        }

        test("POST /books devrait créer un livre et retourner 201") {
            // Arrange
            every { gestionLivreUseCase.ajouterLivre("Harry Potter", "J.K Rowling") } returns Unit

            // Act & Assert
            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"titre":"Harry Potter","auteur":"J.K Rowling"}"""
            }.andExpect {
                status { isCreated() }
            }

            // Assert
            verify { gestionLivreUseCase.ajouterLivre("Harry Potter", "J.K Rowling") }
        }

        test("POST /books avec un titre vide devrait retourner 400") {
            // Arrange
            every { gestionLivreUseCase.ajouterLivre("", "J.K Rowling") } throws
                IllegalArgumentException("Le titre ne peut pas être vide")

            // Act & Assert
            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"titre":"","auteur":"J.K Rowling"}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }

        test("POST /books quand le domaine lance une exception inattendue devrait propager l'erreur") {
            // Arrange
            every { gestionLivreUseCase.ajouterLivre("Harry Potter", "J.K Rowling") } throws
                RuntimeException("Erreur interne")

            // Act & Assert
            shouldThrow<Exception> {
                mockMvc.post("/books") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{"titre":"Harry Potter","auteur":"J.K Rowling"}"""
                }
            }
        }

        // --- Réservation ---

        test("POST /books/{id}/reserver devrait réserver le livre et retourner 200") {
            // Arrange
            every { gestionLivreUseCase.reserverLivre(1, "Hermione Granger") } returns Unit

            // Act & Assert
            mockMvc.post("/books/1/reserver") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"reservePar":"Hermione Granger"}"""
            }.andExpect {
                status { isOk() }
            }

            // Assert
            verify { gestionLivreUseCase.reserverLivre(1, "Hermione Granger") }
        }

        test("POST /books/{id}/reserver sur un livre déjà réservé devrait retourner 400") {
            // Arrange
            every { gestionLivreUseCase.reserverLivre(1, "Ron Weasley") } throws
                IllegalArgumentException("Le livre est déjà réservé")

            // Act & Assert
            mockMvc.post("/books/1/reserver") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"reservePar":"Ron Weasley"}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }

        test("POST /books/{id}/reserver avec un nom vide devrait retourner 400") {
            // Arrange
            every { gestionLivreUseCase.reserverLivre(1, "") } throws
                IllegalArgumentException("Le nom du réservant ne peut pas être vide")

            // Act & Assert
            mockMvc.post("/books/1/reserver") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"reservePar":""}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }
}
