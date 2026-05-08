# Ticketeer — Backend Spring Boot

Système de billetterie ferroviaire numérique.

## Prérequis

- Java 21 (JDK)
- Maven 3.9+
- MySQL 8+ (ou MariaDB)
- IntelliJ IDEA (recommandé)

## Démarrage rapide

### 1. Créer la base de données MySQL

```sql
CREATE DATABASE ticketeer_db;
```

### 2. Configurer les identifiants BDD

Dans `src/main/resources/application.properties` :
```properties
spring.datasource.username=TON_USER
spring.datasource.password=TON_MOT_DE_PASSE
```

### 3. Lancer le backend

```bash
mvn spring-boot:run
```

Le serveur démarre sur **http://localhost:8080**

Les tables sont créées automatiquement par Hibernate (`ddl-auto=update`).
Des données de test (10 villes, trains, voyageurs, 1 agent) sont injectées au premier démarrage.

---

## Endpoints disponibles

| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/auth/login` | Connexion agent → retourne JWT |
| GET | `/api/itineraires/search?depart=Paris&arrivee=Lyon` | Rechercher un trajet |
| POST | `/api/billets/create` | Créer un billet |
| GET | `/api/billets/{uuid}` | Récupérer un billet |
| GET | `/api/billets/{uuid}/qr` | Données QR Code |
| POST | `/api/validations/validate` | Valider un billet (agent) |
| GET | `/api/validations/history/{uuid}` | Historique validations |
| POST | `/api/admin/villes` | Ajouter une ville |
| GET | `/api/admin/villes` | Lister les villes |
| POST | `/api/admin/trains` | Ajouter un train |
| POST | `/api/admin/segments` | Ajouter un segment |
| GET | `/api/admin/tracabilite/{uuid}` | Traçabilité d'un billet |

---

## Structure du projet

```
src/main/java/com/ticketeer/
├── TicketeerApplication.java      ← point d'entrée
├── entity/                        ← entités JPA (tables BDD)
│   ├── Voyageur.java
│   ├── Billet.java                ← entité centrale
│   ├── Itineraire.java
│   ├── SegmentTrajet.java
│   ├── AgentControle.java
│   ├── Validation.java
│   ├── Token.java
│   ├── Ville.java
│   └── Train.java
├── enums/
│   ├── EtatBillet.java            ← VALIDE / UTILISE / INVALIDE
│   └── ResultatValidation.java    ← ACCEPTEE / REFUSEE
├── repository/
│   └── Repositories.java          ← accès BDD automatique
├── service/
│   └── Services.java              ← logique métier
├── controller/
│   └── Controllers.java           ← endpoints REST
└── config/
    ├── SecurityConfig.java         ← Spring Security + CORS
    └── DataInitializer.java        ← données de test au démarrage
```

---

## Données de test (injectées automatiquement)

**Voyageurs :**
- alice@test.com / pass123
- bob@test.com / pass123
- clara@test.com / pass123
- david@test.com / pass123

**Agent de contrôle :**
- login : `agent1` / mdp : `agent123`

**Villes :** Paris, Lyon, Marseille, Bordeaux, Lille, Strasbourg, Nantes, Toulouse, Nice, Montpellier

---

## TODO (à compléter)

- [ ] Logique de recherche avec correspondances (S2)
- [ ] Vérification segment/train/date/heure dans ValidationService
- [ ] Intégration JWT réel (JwtUtil avec jjwt)
- [ ] BCrypt pour les mots de passe
- [ ] Sécuriser /api/admin et /api/validations avec le filtre JWT

## Organisation Git

```bash
git checkout -b feature/backend-entites    # Sarah Badsi
git checkout -b feature/backend-services   # Anyas
git checkout -b feature/backend-controllers # Sarah Abdelli
```
