package com.example.TpGestionLivre.controller

import com.example.TpGestionLivre.domain.usecase.GestionLivreUseCase
import com.example.TpGestionLivre.dto.BookDTO
import com.example.TpGestionLivre.dto.ReservationDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val gestionLivreUseCase: GestionLivreUseCase) {

    @GetMapping
    fun listerLivres(): List<BookDTO> {
        return gestionLivreUseCase.listerLivres()
            .map { BookDTO(id = it.id, titre = it.titre, auteur = it.auteur, disponible = it.estDisponible) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun ajouterLivre(@RequestBody book: BookDTO) {
        gestionLivreUseCase.ajouterLivre(book.titre, book.auteur)
    }

    @PostMapping("/{id}/reserver")
    @ResponseStatus(HttpStatus.OK)
    fun reserverLivre(@PathVariable id: Int, @RequestBody reservation: ReservationDTO) {
        gestionLivreUseCase.reserverLivre(id, reservation.reservePar)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(e: IllegalArgumentException): Map<String, String> {
        return mapOf("error" to (e.message ?: "Requête invalide"))
    }
}
