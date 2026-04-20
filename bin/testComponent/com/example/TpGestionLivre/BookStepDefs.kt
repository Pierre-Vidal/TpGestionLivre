package com.example.TpGestionLivre

import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class BookStepDefs {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    private lateinit var lastResponse: Response

    @Before
    fun setup() {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        jdbcTemplate.update("DELETE FROM livre", mapOf<String, Any>())
    }

    @Given("l'utilisateur crée le livre {string} de {string}")
    fun createBook(titre: String, auteur: String) {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"titre": "$titre", "auteur": "$auteur"}""")
            .`when`()
            .post("/books")
            .then()
            .statusCode(201)
    }

    @When("l'utilisateur récupère tous les livres")
    fun getAllBooks() {
        lastResponse = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
            .extract()
            .response()
    }

    @Then("la liste contient les livres suivants")
    fun shouldContainBooks(payload: List<Map<String, String>>) {
        val livres = lastResponse.jsonPath().getList<Map<String, String>>("")
        livres.size shouldBe payload.size
        payload.forEach { expected ->
            livres.any { it["titre"] == expected["titre"] && it["auteur"] == expected["auteur"] } shouldBe true
        }
    }

    // --- Réservation ---

    @When("l'utilisateur réserve le livre {string} au nom de {string}")
    fun reserveBook(titre: String, reservePar: String) {
        val livres = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList<Map<String, Any>>("")
        val id = livres.first { it["titre"] == titre }["id"] as Int
        lastResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"reservePar": "$reservePar"}""")
            .`when`()
            .post("/books/$id/reserver")
            .then()
            .extract()
            .response()
    }

    @Then("le livre {string} est indisponible dans la liste")
    fun bookShouldBeUnavailable(titre: String) {
        val livres = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList<Map<String, Any>>("")
        val livre = livres.first { it["titre"] == titre }
        livre["disponible"] shouldBe false
    }

    @When("l'utilisateur tente de réserver le livre {string} au nom de {string}")
    fun tryReserveBook(titre: String, reservePar: String) {
        val livres = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList<Map<String, Any>>("")
        val id = livres.first { it["titre"] == titre }["id"] as Int
        lastResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"reservePar": "$reservePar"}""")
            .`when`()
            .post("/books/$id/reserver")
            .then()
            .extract()
            .response()
    }

    @Then("la réservation est refusée avec le code {int}")
    fun reservationShouldBeRefused(statusCode: Int) {
        lastResponse.statusCode shouldBe statusCode
    }
}
