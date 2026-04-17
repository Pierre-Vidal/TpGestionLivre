package com.example.TpGestionLivre

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import io.kotest.core.spec.style.FunSpec

class ArchitectureTest : FunSpec({

    val basePackage = "com.example.TpGestionLivre"

    val importedClasses = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages(basePackage)

    test("il devrait respecter l'architecture hexagonale") {
        val rule = layeredArchitecture().consideringAllDependencies()
            .layer("Domain").definedBy("$basePackage.domain..")
            .layer("Infrastructure").definedBy("$basePackage.infrastructure..")
            .layer("Controller").definedBy("$basePackage.controller..")
            .layer("Application").definedBy("$basePackage.application..")
            .layer("Standard API").definedBy("java..", "kotlin..", "kotlinx..", "org.jetbrains.annotations..")
            .withOptionalLayers(true)
            .whereLayer("Domain").mayOnlyAccessLayers("Standard API")
            .whereLayer("Infrastructure").mayOnlyAccessLayers("Domain", "Standard API")
            .whereLayer("Controller").mayOnlyAccessLayers("Domain", "Standard API")

        rule.check(importedClasses)
    }
})
