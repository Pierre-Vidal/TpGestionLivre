package com.example.TpGestionLivre.domain.usecase

import com.example.TpGestionLivre.domain.model.Livre
import com.example.TpGestionLivre.domain.port.LivreRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class GestionLivreUseCaseTest : FunSpec({

    test("ajouter Harry Potter devrait sauvegarder le livre dans le repository") {
        // Arrange
        val repository = mockk<LivreRepository>(relaxed = true)
        val useCase = GestionLivreUseCase(repository)
        val livre = Livre(titre = "Harry Potter", auteur = "J.K Rowling")

        // Act
        useCase.ajouterLivre("Harry Potter", "J.K Rowling")

        // Assert
        verify { repository.save(livre) }
    }

    test("ajouter un livre avec un titre vide devrait lancer une IllegalArgumentException") {
        // Arrange
        val repository = mockk<LivreRepository>()
        val useCase = GestionLivreUseCase(repository)

        // Act & Assert
        shouldThrow<IllegalArgumentException> {
            useCase.ajouterLivre("", "J.K Rowling")
        }
    }

    test("ajouter un livre avec un auteur vide devrait lancer une IllegalArgumentException") {
        // Arrange
        val repository = mockk<LivreRepository>()
        val useCase = GestionLivreUseCase(repository)

        // Act & Assert
        shouldThrow<IllegalArgumentException> {
            useCase.ajouterLivre("Harry Potter", "")
        }
    }

    test("lister les livres devrait retourner la liste triée par titre") {
        // Arrange
        val repository = mockk<LivreRepository>()
        val useCase = GestionLivreUseCase(repository)
        every { repository.findAll() } returns listOf(
            Livre(titre = "Les Misérables", auteur = "Victor Hugo"),
            Livre(titre = "Harry Potter", auteur = "J.K Rowling")
        )

        // Act
        val livres = useCase.listerLivres()

        // Assert
        livres[0].titre shouldBe "Harry Potter"
        livres[1].titre shouldBe "Les Misérables"
    }

    test("lister les livres quand le repository est vide devrait retourner une liste vide") {
        // Arrange
        val repository = mockk<LivreRepository>()
        val useCase = GestionLivreUseCase(repository)
        every { repository.findAll() } returns emptyList()

        // Act
        val livres = useCase.listerLivres()

        // Assert
        livres shouldBe emptyList()
    }

    test("réserver Harry Potter devrait appeler reserver sur le repository") {
        // Arrange
        val repository = mockk<LivreRepository>(relaxed = true)
        val useCase = GestionLivreUseCase(repository)
        every { repository.findById(1) } returns Livre(id = 1, titre = "Harry Potter", auteur = "J.K Rowling")

        // Act
        useCase.reserverLivre(1, "Hermione Granger")

        // Assert
        verify { repository.reserver(1, "Hermione Granger") }
    }

    test("réserver un livre déjà réservé devrait lancer une IllegalArgumentException") {
        // Arrange
        val repository = mockk<LivreRepository>()
        val useCase = GestionLivreUseCase(repository)
        every { repository.findById(1) } returns Livre(id = 1, titre = "Harry Potter", auteur = "J.K Rowling", reservePar = "Hermione Granger")

        // Act & Assert
        shouldThrow<IllegalArgumentException> {
            useCase.reserverLivre(1, "Ron Weasley")
        }
    }

    test("réserver un livre avec un nom vide devrait lancer une IllegalArgumentException") {
        // Arrange
        val repository = mockk<LivreRepository>()
        val useCase = GestionLivreUseCase(repository)

        // Act & Assert
        shouldThrow<IllegalArgumentException> {
            useCase.reserverLivre(1, "")
        }
    }

    test("réserver un livre introuvable devrait lancer une IllegalStateException") {
        // Arrange
        val repository = mockk<LivreRepository>()
        val useCase = GestionLivreUseCase(repository)
        every { repository.findById(99) } returns null

        // Act & Assert
        shouldThrow<IllegalStateException> {
            useCase.reserverLivre(99, "Hermione Granger")
        }
    }

})
