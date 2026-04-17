package com.example.TpGestionLivre.application

import com.example.TpGestionLivre.domain.port.LivreRepository
import com.example.TpGestionLivre.domain.usecase.GestionLivreUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {

    @Bean
    fun gestionLivreUseCase(livreRepository: LivreRepository): GestionLivreUseCase {
        return GestionLivreUseCase(livreRepository)
    }
}
