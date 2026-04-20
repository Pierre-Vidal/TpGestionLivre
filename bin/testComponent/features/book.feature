Feature: Gestion des livres

  Scenario: l'utilisateur crée deux livres et les retrouve dans la liste
    Given l'utilisateur crée le livre "Harry Potter" de "J.K Rowling"
    And l'utilisateur crée le livre "Les Misérables" de "Victor Hugo"
    When l'utilisateur récupère tous les livres
    Then la liste contient les livres suivants
      | titre          | auteur      |
      | Harry Potter   | J.K Rowling |
      | Les Misérables | Victor Hugo |

  Scenario: l'utilisateur réserve un livre disponible
    Given l'utilisateur crée le livre "Le Petit Prince" de "Antoine de Saint-Exupéry"
    When l'utilisateur réserve le livre "Le Petit Prince" au nom de "Hermione Granger"
    Then le livre "Le Petit Prince" est indisponible dans la liste

  Scenario: l'utilisateur ne peut pas réserver un livre déjà réservé
    Given l'utilisateur crée le livre "Harry Potter" de "J.K Rowling"
    And l'utilisateur réserve le livre "Harry Potter" au nom de "Hermione Granger"
    When l'utilisateur tente de réserver le livre "Harry Potter" au nom de "Ron Weasley"
    Then la réservation est refusée avec le code 400
