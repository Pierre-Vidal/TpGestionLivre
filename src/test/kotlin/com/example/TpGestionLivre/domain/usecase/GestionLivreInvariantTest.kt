package com.example.TpGestionLivre.domain.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class GestionLivreInvariantTest : FunSpec({

    test("Invariant 1 : Contenu - chaque livre ajouté est présent dans la liste retournée") {
        checkAll(Arb.string().filter { it.isNotBlank() }, Arb.string().filter { it.isNotBlank() }) { titre, auteur ->
            // Arrange
            val repository = FakeLivreRepository()
            val useCase = GestionLivreUseCase(repository)

            // Act
            useCase.ajouterLivre(titre, auteur)
            val livres = useCase.listerLivres()

            // Assert
            livres.any { it.titre == titre && it.auteur == auteur } shouldBe true
        }
    }

    test("Invariant 2 : Taille - le nombre de livres retournés est égal au nombre de livres ajoutés") {
        checkAll(Arb.list(Arb.string().filter { it.isNotBlank() }, 0..10)) { titres ->
            // Arrange
            val repository = FakeLivreRepository()
            val useCase = GestionLivreUseCase(repository)

            // Act
            titres.forEach { titre -> useCase.ajouterLivre(titre, "Auteur") }
            val livres = useCase.listerLivres()

            // Assert
            livres.size shouldBe titres.size
        }
    }

    test("Invariant 3 : Tri - la liste retournée est toujours triée par titre") {
        checkAll(Arb.list(Arb.string().filter { it.isNotBlank() }, 1..10)) { titres ->
            // Arrange
            val repository = FakeLivreRepository()
            val useCase = GestionLivreUseCase(repository)

            // Act
            titres.forEach { titre -> useCase.ajouterLivre(titre, "Auteur") }
            val livres = useCase.listerLivres()

            // Assert
            livres shouldBeSortedWith compareBy { it.titre }
        }
    }

    test("Invariant 4 : Retrouvable - un livre ajouté peut être retrouvé par titre dans la liste") {
        checkAll(Arb.string().filter { it.isNotBlank() }, Arb.string().filter { it.isNotBlank() }) { titre, auteur ->
            // Arrange
            val repository = FakeLivreRepository()
            val useCase = GestionLivreUseCase(repository)

            // Act
            useCase.ajouterLivre(titre, auteur)
            val livres = useCase.listerLivres()

            // Assert
            livres.find { it.titre == titre }?.auteur shouldBe auteur
        }
    }

    // --- Réservation ---

    test("Invariant 5 : Idempotence négative - un livre réservé ne peut jamais être réservé à nouveau") {
        checkAll(
            Arb.string().filter { it.isNotBlank() },
            Arb.string().filter { it.isNotBlank() }
        ) { nom1, nom2 ->
            // Arrange
            val repository = FakeLivreRepository()
            val useCase = GestionLivreUseCase(repository)
            useCase.ajouterLivre("Harry Potter", "J.K Rowling")
            val id = repository.findAll().first().id!!
            useCase.reserverLivre(id, nom1)

            // Act & Assert
            shouldThrow<IllegalArgumentException> {
                useCase.reserverLivre(id, nom2)
            }
        }
    }

    test("Invariant 6 : Disponibilité - après réservation le livre est toujours indisponible") {
        checkAll(Arb.string().filter { it.isNotBlank() }) { nom ->
            // Arrange
            val repository = FakeLivreRepository()
            val useCase = GestionLivreUseCase(repository)
            useCase.ajouterLivre("Les Misérables", "Victor Hugo")
            val id = repository.findAll().first().id!!

            // Act
            useCase.reserverLivre(id, nom)

            // Assert
            repository.findById(id)!!.estDisponible shouldBe false
        }
    }

    test("Invariant 7 : Intégrité du réservant - un nom blank est toujours refusé") {
        checkAll(Arb.string().filter { it.isBlank() }) { nomBlank ->
            // Arrange
            val repository = FakeLivreRepository()
            val useCase = GestionLivreUseCase(repository)
            useCase.ajouterLivre("Le Petit Prince", "Antoine de Saint-Exupéry")
            val id = repository.findAll().first().id!!

            // Act & Assert
            shouldThrow<IllegalArgumentException> {
                useCase.reserverLivre(id, nomBlank)
            }
        }
    }

})
