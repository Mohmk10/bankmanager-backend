# 🏦 BankManager Backend - API REST

Application backend de gestion bancaire développée avec **Spring Boot 3** et **Java 21**.

## 🎯 Fonctionnalités

### 🔐 Authentification JWT
- Inscription (Register)
- Connexion (Login)
- Tokens JWT sécurisés

### 👥 Gestion des Clients
- CRUD complet
- Recherche par nom/email
- Soft delete (désactivation)

### 💳 Gestion des Comptes
- **Compte Épargne** : Avec période de blocage
- **Compte Chèque** : Avec frais de transaction (0.8%)
- Génération automatique des numéros (Format: C + Année + 8 chiffres)

### 💸 Gestion des Transactions
- Dépôts et Retraits
- Validation des règles métier
- Calcul automatique des frais
- Génération automatique des IDs (Format: T + Année + 6 chiffres)

### 📊 Dashboard
- Statistiques en temps réel
- Comptes et transactions récents

---

## 🛠️ Technologies

- **Java** 21
- **Spring Boot** 3.5.7
- **Spring Security** + JWT
- **Spring Data JPA** + Hibernate
- **PostgreSQL** 14+
- **Maven** 3.9+
- **Lombok**
- **Validation**

---

## 📋 Prérequis

- Java 21+
- Maven 3.9+
- PostgreSQL 14+

---

## 🚀 Installation

### 1. Cloner le projet
```bash
git clone https://github.com/votre-username/bankmanager-backend.git
cd bankmanager-backend
```

### 2. Créer la base de données
```sql
CREATE DATABASE bankmanager;
```

### 3. Configuration

Créer `src/main/resources/application.properties` :
```properties
spring.application.name=BankManager
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/bankmanager
spring.datasource.username=votre_username
spring.datasource.password=votre_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

jwt.secret=BankManagerSecretKeyForJWT2025MustBeLongEnoughForHS512Algorithm
jwt.expiration=86400000

logging.level.com.bankmanager=DEBUG
```

### 4. Lancer l'application
```bash
./mvnw spring-boot:run
```

L'API sera accessible sur `http://localhost:8080`

---

## 📡 API Endpoints

### Authentication
```
POST /api/auth/register - Inscription
POST /api/auth/login    - Connexion
```

### Clients
```
POST   /api/clients           - Créer un client
GET    /api/clients           - Liste des clients
GET    /api/clients/{id}      - Détails d'un client
PUT    /api/clients/{id}      - Modifier un client
DELETE /api/clients/{id}      - Supprimer un client (soft delete)
GET    /api/clients?search=   - Rechercher des clients
```

### Comptes
```
POST   /api/comptes                  - Créer un compte
GET    /api/comptes                  - Liste des comptes
GET    /api/comptes/{id}             - Détails d'un compte
GET    /api/comptes/{id}/details     - Détails complets avec stats
GET    /api/comptes/client/{clientId} - Comptes d'un client
DELETE /api/comptes/{id}             - Supprimer un compte
```

### Transactions
```
POST /api/transactions              - Créer une transaction
GET  /api/transactions              - Liste des transactions
GET  /api/transactions/{id}         - Détails d'une transaction
GET  /api/transactions/compte/{id}  - Transactions d'un compte
GET  /api/transactions/recent?days= - Transactions récentes
```

### Dashboard
```
GET /api/dashboard - Statistiques globales
```

---

## 🏗️ Architecture
```
src/main/java/com/bankmanager/
├── config/              # Configuration (Security, JWT, CORS)
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
│   ├── request/        # Requêtes
│   └── response/       # Réponses
├── entity/             # Entités JPA
│   └── enums/          # Énumérations
├── exception/          # Gestion des erreurs
├── repository/         # Repositories Spring Data
├── security/           # JWT & Security
├── service/            # Logique métier
└── util/               # Utilitaires
```

---

## 🎯 Principes & Patterns

### Design Patterns
- ✅ Repository Pattern
- ✅ Service Layer Pattern
- ✅ DTO Pattern
- ✅ Factory Pattern (générateurs)
- ✅ Strategy Pattern (types de comptes)
- ✅ Filter Pattern (JWT)

### Principes SOLID
- ✅ Single Responsibility
- ✅ Open/Closed
- ✅ Liskov Substitution
- ✅ Interface Segregation
- ✅ Dependency Inversion

---

## 🧪 Tests avec Postman

Import la collection Postman disponible dans `/postman/BankManager_Collection.json`

Environnement :
```
base_url = http://localhost:8080
token = (auto-généré après login)
```

Ordre de test :
1. Register → Login
2. Create Client
3. Create Compte
4. Create Transaction
5. Dashboard

---

## 🔒 Sécurité

- **JWT** pour l'authentification
- **BCrypt** pour le hachage des mots de passe
- **Spring Security** pour la protection des endpoints
- **CORS** configuré pour le frontend Angular

---

## 📦 Build & Déploiement

### Build
```bash
./mvnw clean package
```

Le JAR sera généré dans `target/backend-0.0.1-SNAPSHOT.jar`

### Lancer le JAR
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

---

## 🐳 Docker (Optionnel)
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
```bash
docker build -t bankmanager-backend .
docker run -p 8080:8080 bankmanager-backend
```

---

## 👨‍💻 Auteur

**Votre Nom**
- GitHub: [@votre-username](https://github.com/votre-username)
- LinkedIn: [Votre Profil](https://linkedin.com/in/votre-profil)

---

## 📄 Licence

MIT License - voir [LICENSE](LICENSE)

---

## 🚀 Frontend

Le frontend Angular est disponible sur : [bankmanager-frontend](https://github.com/votre-username/bankmanager-frontend)