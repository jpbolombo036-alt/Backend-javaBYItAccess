# 📁 Repositories Package

Ce package contient toutes les interfaces Spring Data JPA pour l'accès aux données de l'application.

## 🗄️ Architecture des Repositories

### 🏛️ Principe de Base
Tous les repositories étendent **JpaRepository** et fournissent :
- **Opérations CRUD** automatiques
- **Requêtes personnalisées** via @Query
- **Pagination et tri** intégrés
- **Gestion des transactions** automatique

---

### 📋 Liste des Repositories

#### 👤 UserRepository.java
**Accès aux données des utilisateurs**

**Méthodes fournies :**
- `findByEmail(String email)` - Recherche par email (connexion)
- `findByRole(UserRole role)` - Liste par rôle
- `findByActive(boolean active)` - Utilisateurs actifs/inactifs

**Utilisation typique :**
```java
// Authentification
Optional<User> user = userRepository.findByEmail(email);

// Liste des médecins actifs
List<User> doctors = userRepository.findByRoleAndActive(UserRole.DOCTOR, true);
```

---

#### 👨‍⚕️ DoctorRepository.java
**Accès aux données des médecins**

**Méthodes personnalisées :**
- `findByUser(User user)` - Médecin par utilisateur
- `findByLicenseNo(String licenseNo)` - Recherche par licence
- `findByIsActive(boolean active)` - Médecins actifs
- `findBySpecialty(String specialty)` - Par spécialité

**Requêtes complexes :**
```java
@Query("SELECT d FROM Doctor d WHERE d.specialty = ?1 AND d.isActive = true")
List<Doctor> findActiveDoctorsBySpecialty(String specialty);
```

---

#### 🧑‍⚕️ PatientRepository.java
**Accès aux données des patients**

**Méthodes utiles :**
- `findByFirstNameAndLastName(String firstName, String lastName)`
- `findByPhone(String phone)` - Recherche par téléphone
- `findByDateOfBirth(LocalDate date)` - Par date de naissance

**Recherche avancée :**
```java
@Query("SELECT p FROM Patient p WHERE p.firstName LIKE %?1% OR p.lastName LIKE %?1%")
List<Patient> searchByName(String name);
```

---

#### 💊 PrescriptionRepository.java
**Accès aux données des prescriptions**

**Méthodes critiques :**
- `findByDoctor(Doctor doctor)` - Prescriptions d'un médecin
- `findByPatient(Patient patient)` - Prescriptions d'un patient
- `findByStatus(PrescriptionStatus status)` - Par statut
- `findByPrescriptionDateBetween(LocalDate start, LocalDate end)` - Période

**Requêtes métier :**
```java
@Query("SELECT p FROM Prescription p WHERE p.doctor = ?1 AND p.status = 'VALIDATED'")
List<Prescription> findValidatedPrescriptionsByDoctor(Doctor doctor);

@Query("SELECT COUNT(p) FROM Prescription p WHERE p.status = ?1")
Long countByStatus(PrescriptionStatus status);
```

---

#### 💰 CommissionRepository.java
**Accès aux données des commissions**

**Méthodes financières :**
- `findByDoctor(Doctor doctor)` - Commissions d'un médecin
- `findByStatus(CommissionStatus status)` - Par statut
- `findByCreatedAtBetween(LocalDateTime start, LocalDateTime end)` - Période

**Agrégations importantes :**
```java
@Query("SELECT COALESCE(SUM(c.amount), 0) FROM Commission c WHERE c.doctor = ?1 AND c.status = 'PENDING'")
BigDecimal sumPendingCommissionsByDoctor(Doctor doctor);

@Query("SELECT c.doctor.id, SUM(c.amount) FROM Commission c WHERE c.status = 'PENDING' GROUP BY c.doctor.id")
List<Object[]> getPendingCommissionTotalsByAllDoctors();
```

---

#### 💳 VirtualWalletRepository.java
**Accès aux portefeuilles virtuels**

**Méthodes principales :**
- `findByDoctor(Doctor doctor)` - Portefeuille d'un médecin
- `findByBalanceGreaterThan(BigDecimal amount)` - Soldes supérieurs à
- `findByLastUpdatedBefore(LocalDateTime date)` - Inactivité

**Mises à jour optimisées :**
```java
@Query("UPDATE VirtualWallet vw SET vw.balance = vw.balance + ?1 WHERE vw.doctor = ?2")
@Modifying
void addBalance(BigDecimal amount, Doctor doctor);
```

---

#### 💸 PaymentRepository.java
**Accès aux données des paiements**

**Méthodes de suivi :**
- `findByDoctor(Doctor doctor)` - Paiements d'un médecin
- `findByPaidAtBetween(LocalDateTime start, LocalDateTime end)` - Période
- `findByMethod(String method)` - Par méthode de paiement

**Statistiques :**
```java
@Query("SELECT COUNT(p) FROM Payment p WHERE p.paidAt BETWEEN ?1 AND ?2")
Long countPaymentsInPeriod(LocalDateTime start, LocalDateTime end);

@Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.doctor = ?1")
BigDecimal totalPaymentsByDoctor(Doctor doctor);
```

---

#### 🔗 PaymentCommissionRepository.java
**Accès aux relations paiements-commissions**

**Méthodes de traçabilité :**
- `findByPayment(Payment payment)` - Commissions d'un paiement
- `findByCommission(Commission commission)` - Paiements d'une commission
- `findByPaymentAndCommission(Payment payment, Commission commission)` - Lien spécifique

**Requêtes d'audit :**
```java
@Query("SELECT pc FROM PaymentCommission pc JOIN FETCH pc.commission WHERE pc.payment = ?1")
List<PaymentCommission> findWithCommissionsByPayment(Payment payment);
```

---

## 🔄 Patterns d'Utilisation

### 🔍 Recherche Simple
```java
// Repository standard
Optional<User> user = userRepository.findById(id);
List<User> users = userRepository.findAll();
```

### 🎯 Recherche Personnalisée
```java
// Avec méthode personnalisée
List<Prescription> prescriptions = prescriptionRepository.findByDoctor(doctor);
```

### 📊 Agrégations
```java
// Calculs financiers
BigDecimal total = commissionRepository.sumPendingCommissionsByDoctor(doctor);
```

### 🔄 Transactions
```java
// Mises à jour atomiques
@Transactional
public void updateWalletBalance(Doctor doctor, BigDecimal amount) {
    VirtualWallet wallet = virtualWalletRepository.findByDoctor(doctor);
    wallet.setBalance(wallet.getBalance().add(amount));
    virtualWalletRepository.save(wallet);
}
```

---

## ⚡ Optimisations

### 🚀 Performance
- **Lazy Loading** pour les relations
- **Indexation** sur les champs de recherche fréquents
- **Fetch Joins** pour éviter N+1 queries

### 📈 Scalabilité
- **Pagination** pour les grandes listes
- **Caching** des données fréquemment accédées
- **Batch operations** pour les mises à jour multiples

### 🔧 Maintenance
- **Tests unitaires** pour chaque requête personnalisée
- **Logging** des requêtes lentes
- **Monitoring** des performances

---

## 🎯 Bonnes Pratiques

### 📝 Conventions de nommage
- **findBy** + Champ : Recherche simple
- **countBy** + Champ : Comptage
- **existsBy** + Champ : Existence
- **deleteBy** + Champ : Suppression

### 🔒 Sécurité
- **Validation** des paramètres
- **Sanitization** des entrées utilisateur
- **Audit trail** des modifications

### 🧪 Tests
- **Repository tests** avec @DataJpaTest
- **Testcontainers** pour l'intégration
- **Assertions** sur les résultats
