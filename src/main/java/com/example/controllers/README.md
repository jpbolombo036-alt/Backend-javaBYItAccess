# 📁 Controllers Package

Ce package contient tous les contrôleurs REST qui exposent les API de l'application de gestion des prescriptions médicales.

## 🌐 Architecture des Contrôleurs

### 🎯 Principe de Conception
Les contrôleurs implémentent l'interface REST de l'application et assurent :
- **Exposition des endpoints** HTTP/REST
- **Validation des requêtes** entrantes
- **Séparation par rôle** (Admin vs Doctor)
- **Documentation Swagger** intégrée
- **Gestion des erreurs** HTTP appropriées

---

### 📋 Liste des Contrôleurs

#### 👤 UserController.java
**API pour la gestion des utilisateurs (admin/médecin)**

**Endpoints disponibles :**
```http
POST   /api/users/admin                    # Créer un administrateur
POST   /api/users/doctor                   # Créer un médecin
GET    /api/users/all                      # Lister tous les utilisateurs
GET    /api/users/{id}                     # Récupérer un utilisateur
GET    /api/users/doctors                   # Lister tous les médecins
GET    /api/users/doctor/{id}               # Récupérer un médecin
PUT    /api/users/{id}/status               # Activer/désactiver un utilisateur
```

**Fonctionnalités clés :**
- **Création admin** : `POST /api/users/admin`
  - Paramètres : name, email, password
  - Validation : email unique obligatoire
  - Sécurité : hashage automatique du mot de passe

- **Création médecin** : `POST /api/users/doctor`
  - Paramètres : name, email, password, specialty, licenseNumber
  - Automatique : création du portefeuille virtuel
  - Validation : licence unique obligatoire

**Exemples d'utilisation :**
```bash
# Créer un administrateur
curl -X POST "http://localhost:8080/api/users/admin?name=Admin&email=admin@med.com&password=admin123"

# Créer un médecin
curl -X POST "http://localhost:8080/api/users/doctor?name=Dr. Smith&email=smith@med.com&password=doc123&specialty=Cardiologie&licenseNumber=DOC001"
```

---

#### 🏛️ AdminController.java
**API pour les administrateurs (accès complet au système)**

**Endpoints disponibles :**
```http
# PRESCRIPTIONS
POST   /api/admin/prescriptions             # Créer une prescription
GET    /api/admin/prescriptions             # Lister toutes les prescriptions
GET    /api/admin/prescriptions/{id}         # Récupérer une prescription
PUT    /api/admin/prescriptions/{id}/status  # Mettre à jour le statut

# COMMISSIONS
GET    /api/admin/commissions/pending       # Commissions en attente
GET    /api/admin/commissions/summary       # Résumé par médecin
GET    /api/admin/commissions/doctor/{id}   # Commissions d'un médecin

# PAIEMENTS
POST   /api/admin/payments/doctor/{id}/full # Payer toutes les commissions

# PORTEFEUILLES
GET    /api/admin/wallets                   # Tous les portefeuilles virtuels
GET    /api/admin/wallets/doctor/{id}       # Portefeuille d'un médecin
```

**Fonctionnalités principales :**
- **Gestion complète** des prescriptions
- **Suivi des commissions** de tous les médecins
- **Traitement des paiements** en masse
- **Supervision financière** globale

**Workflow typique :**
```java
// 1. Créer une prescription
POST /api/admin/prescriptions
{
  "doctorId": "uuid-doctor",
  "patientId": "uuid-patient", 
  "totalAmount": 1000.00,
  "notes": "Prescription médicale"
}

// 2. Valider la prescription
PUT /api/admin/prescriptions/{id}/status?status=VALIDATED

// 3. Payer les commissions du médecin
POST /api/admin/payments/doctor/{id}/full
{
  "method": "VIREMENT",
  "reference": "REF-001",
  "note": "Paiement mensuel"
}
```

---

#### 👨‍⚕️ DoctorController.java
**API pour les médecins (accès limité à leurs propres données)**

**Endpoints disponibles :**
```http
# PRESCRIPTIONS
GET    /api/doctor/prescriptions             # Mes prescriptions
GET    /api/doctor/prescriptions/{id}         # Détail d'une prescription

# COMMISSIONS
GET    /api/doctor/commissions               # Mes commissions
GET    /api/doctor/commissions/pending       # Mes commissions en attente
GET    /api/doctor/commissions/total-pending # Total de mes commissions en attente

# PAIEMENTS
GET    /api/doctor/payments                  # Mes paiements

# PORTEFEUILLE
GET    /api/doctor/wallet                    # Mon portefeuille virtuel
```

**Fonctionnalités spécifiques :**
- **Vue filtrée** : uniquement les données du médecin connecté
- **Consultation seule** : pas de modification possible
- **Financières** : suivi des revenus et paiements

**Exemples d'utilisation :**
```bash
# Voir mes prescriptions
curl -X GET "http://localhost:8080/api/doctor/prescriptions?doctorId=uuid-doctor"

# Voir mes commissions en attente
curl -X GET "http://localhost:8080/api/doctor/commissions/pending?doctorId=uuid-doctor"

# Consulter mon portefeuille
curl -X GET "http://localhost:8080/api/doctor/wallet?doctorId=uuid-doctor"
```

---

## 🔐 Sécurité et Contrôle d'Accès

### 🛡️ Configuration Spring Security
```java
.authorizeHttpRequests(authz -> authz
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/doctor/**").hasRole("DOCTOR") 
    .requestMatchers("/api/users/**").permitAll()  // Création publique
    .anyRequest().authenticated()
)
```

### 🎭 Rôles et Permissions

#### ADMIN (Administrateur)
- ✅ Créer/consulter toutes les prescriptions
- ✅ Voir toutes les commissions
- ✅ Traiter tous les paiements
- ✅ Gérer tous les utilisateurs
- ✅ Accès financier complet

#### DOCTOR (Médecin)
- ✅ Voir ses prescriptions uniquement
- ✅ Voir ses commissions uniquement
- ✅ Consulter ses paiements
- ✅ Voir son portefeuille virtuel
- ❌ Pas de modification des données

---

## 📊 Réponses API

### ✅ Succès (200 OK)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 1000.00,
  "status": "VALIDATED",
  "prescriptionDate": "2026-05-05",
  "createdAt": "2026-05-05T16:00:00"
}
```

### ❌ Erreurs
```json
// 400 Bad Request - Données invalides
{
  "timestamp": "2026-05-05T16:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email déjà utilisé"
}

// 404 Not Found - Ressource non trouvée
{
  "timestamp": "2026-05-05T16:00:00", 
  "status": 404,
  "error": "Not Found",
  "message": "Prescription non trouvée"
}

// 403 Forbidden - Accès refusé
{
  "timestamp": "2026-05-05T16:00:00",
  "status": 403, 
  "error": "Forbidden",
  "message": "Accès réservé aux administrateurs"
}
```

---

## 📖 Documentation Swagger

### 🌐 Accès
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs

### 📝 Annotations Utilisées
```java
@Tag(name = "Admin API", description = "API pour les administrateurs")
@Operation(summary = "Créer une prescription", description = "...")
@ApiResponse(responseCode = "200", description = "Prescription créée")
@Parameter(description = "ID du médecin") @PathVariable UUID id
```

### 🎯 Fonctionnalités Swagger
- **Documentation interactive** de tous les endpoints
- **Test direct** des API depuis l'interface
- **Exemples de requêtes** et réponses
- **Validation des schémas** automatique

---

## 🔄 Patterns d'Utilisation

### 📡 Requêtes Typiques

#### Création avec validation
```java
@PostMapping("/prescriptions")
public ResponseEntity<Prescription> createPrescription(
        @Valid @RequestBody Prescription prescription) {
    return ResponseEntity.ok(prescriptionService.createPrescription(prescription));
}
```

#### Recherche avec paramètres
```java
@GetMapping("/prescriptions")
public ResponseEntity<List<Prescription>> getMyPrescriptions(
        @RequestParam UUID doctorId) {
    return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctor(doctorId));
}
```

#### Gestion d'erreurs
```java
@ExceptionHandler(EmailAlreadyExistsException.class)
public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException e) {
    return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
}
```

---

## ⚡ Optimisations

### 🚀 Performance
- **Pagination** pour les grandes listes
- **Lazy loading** des relations
- **DTOs** pour optimiser les réponses

### 📊 Monitoring
- **Logging** des requêtes importantes
- **Métriques** d'utilisation des API
- **Alertes** sur les erreurs

---

## 🧪 Tests

### 🎯 Tests d'API
```java
@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerTest {
    
    @Test
    void testCreateDoctor() {
        // Test de création de médecin
        mockMvc.perform(post("/api/users/doctor")
                .param("name", "Dr. Test")
                .param("email", "test@med.com")
                .param("password", "password123")
                .param("specialty", "Généraliste")
                .param("licenseNumber", "TEST001"))
                .andExpect(status().isOk());
    }
}
```

### 🔄 Tests d'Intégration
```java
@Test
void testFullWorkflow() {
    // 1. Créer médecin
    // 2. Créer prescription  
    // 3. Générer commission
    // 4. Traiter paiement
    // 5. Vérifier portefeuille
}
```

---

## 🎯 Bonnes Pratiques

### 📝 Conventions REST
- **GET** : Consultation de données
- **POST** : Création de ressources
- **PUT** : Mise à jour complète
- **PATCH** : Mise à jour partielle

### 🔒 Sécurité
- **Validation** des entrées
- **Sanitization** des paramètres
- **Rate limiting** pour éviter les abus

### 📊 Évolutivité
- **Versioning** des API
- **Backward compatibility**
- **Extensibilité** pour nouveaux endpoints
