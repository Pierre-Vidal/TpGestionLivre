Feature: Gestion des livres

  Scenario: l'utilisateur crée deux livres et les retrouve dans la liste
    Given l'utilisateur crée le livre "Harry Potter" de "J.K Rowling"
    And l'utilisateur crée le livre "Les Misérables" de "Victor Hugo"
    When l'utilisateur récupère tous les livres
    Then la liste contient les livres suivants
      | titre          | auteur      |
      | Harry Potter   | J.K Rowling |
      | Les Misérables | Victor Hugo |
