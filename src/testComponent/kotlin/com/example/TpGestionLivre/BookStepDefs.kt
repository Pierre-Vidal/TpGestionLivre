package com.example.TpGestionLivre

import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.springframework.boot.test.web.server.LocalServerPort

class BookStepDefs {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var lastResponse: Response

    @Before
    fun setup() {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
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
}
