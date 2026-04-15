package com.example.TpGestionLivre.controller

import com.example.TpGestionLivre.domain.model.Livre
import com.example.TpGestionLivre.domain.usecase.GestionLivreUseCase
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
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
        test("GET /books devrait retourner la liste des livres") {
            // Arrange
            every { gestionLivreUseCase.listerLivres() } returns listOf(
                Livre(titre = "Harry Potter", auteur = "J.K Rowling"),
                Livre(titre = "Les Misérables", auteur = "Victor Hugo")
            )

            // Act & Assert
            mockMvc.get("/books") {
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json("""[{"titre":"Harry Potter","auteur":"J.K Rowling"},{"titre":"Les Misérables","auteur":"Victor Hugo"}]""") }
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

        test("GET /books quand le domaine lance une exception devrait retourner 500") {
            // Arrange
            every { gestionLivreUseCase.listerLivres() } throws RuntimeException("Erreur interne")

            // Act & Assert
            mockMvc.get("/books") {
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isInternalServerError() }
            }
        }
    }
}
