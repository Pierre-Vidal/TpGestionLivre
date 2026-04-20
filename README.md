# TpGestionLivre

Application de gestion de livres développée en Kotlin avec Spring Boot, dans le cadre du cours de tests Ynov M2.
L'objectif est de mettre en pratique les différents niveaux de tests : unitaires, d'intégration, de composant et de performance.

## Ce que fait l'application

L'application expose une API REST pour gérer une bibliothèque de livres :
- Ajouter un livre
- Lister tous les livres (triés par titre)
- Réserver un livre — un livre déjà réservé ne peut pas être réservé à nouveau


## Prérequis

- Java 21
- Docker (pour la base de données)

## Lancer l'application

Démarrer PostgreSQL avec Docker :

```bash
docker compose up -d
```

Puis lancer l'application :

```bash
./gradlew bootRun
```

## Lancer les tests

### Tests unitaires

Les tests du domaine pur — use case et invariants de propriété. Pas besoin de Docker.

```bash
./gradlew test
```

### Tests d'intégration

Deux types : les tests du controller (MockMVC) et les tests de la base de données (Testcontainers).
Docker doit être lancé pour les tests BDD.

```bash
./gradlew testIntegration
```

### Tests de composant

Tests end-to-end écrits en Gherkin (Cucumber). L'application démarre complètement avec une vraie base via Testcontainers.

```bash
./gradlew testComponent
```

### Tests d'architecture

Vérifient que les couches ne se mélangent pas (ex: le domaine n'importe pas Spring).

```bash
./gradlew testArchitecture
```

### Linter (Detekt)

Analyse statique du code Kotlin.

```bash
./gradlew detekt
```

### Tout lancer d'un coup

```bash
./gradlew test testIntegration testComponent testArchitecture detekt
```

### Tests de performance (k6)

Nécessite que l'application tourne (`bootRun`) et que [k6](https://k6.io/) soit installé.

```bash
k6 run src/testPerformance/index.js --config src/testPerformance/config.json
```

Les scénarios simulent 50 requêtes/seconde pendant 10s sur la création et la liste des livres.