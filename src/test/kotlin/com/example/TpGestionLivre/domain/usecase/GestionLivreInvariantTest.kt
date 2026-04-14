package com.example.TpGestionLivre.domain.usecase

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
            livres.find { it.titre == titre } shouldBe com.example.TpGestionLivre.domain.model.Livre(titre = titre, auteur = auteur)
        }
    }

})
