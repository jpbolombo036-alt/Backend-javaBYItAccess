# 📁 Services Package

Ce package contient toute la logique métier de l'application de gestion des prescriptions médicales.

## 🏗️ Architecture des Services

### 🎯 Principe de Conception
Les services implémentent la logique métier complexe et assurent :
- **Séparation des responsabilités** entre contrôleurs et données
- **Gestion des transactions** automatique
- **Validation des règles métier**
- **Orchestration des opérations complexes**

---

### 📋 Liste des Services

#### 💊 PrescriptionService.java
**Gestion des prescriptions et création automatique des commissions**

**Responsabilités clés :**
- Création de prescriptions
- Validation des statuts
- **Génération automatique** des commissions (5%)
- Mise à jour des portefeuilles virtuels

**Flux de création :**
```java
@Transactional
public Prescription createPrescription(Prescription prescription) {
    // 1. Validation des données
    validatePrescriptionData(prescription);
    
    // 2. Sauvegarde de la prescription
    Prescription saved = prescriptionRepository.save(prescription);
    
    // 3. Génération automatique de la commission (5%)
    Commission commission = generateCommission(saved);
    
    // 4. Créditation du portefeuille virtuel
    updateVirtualWallet(saved.getDoctor(), commission.getAmount());
    
    return saved;
}
```

**Règles métier implémentées :**
- **Validation** : Montant positif, médecin et patient valides
- **Commission** : 5% automatique du montant total
- **Portefeuille** : Créditation immédiate du montant de commission

---

#### 💰 CommissionService.java
**Calcul et gestion des commissions**

**Responsabilités principales :**
- Calcul des commissions (taux fixe de 5%)
- Suivi des statuts de paiement
- Agrégations financières par médecin
- Reporting des commissions

**Méthodes critiques :**
```java
// Calcul de commission
public BigDecimal calculateCommissionAmount(BigDecimal totalAmount) {
    return totalAmount.multiply(COMMISSION_RATE); // 0.05
}

// Total des commissions en attente pour un médecin
public BigDecimal getPendingCommissionTotalByDoctor(UUID doctorId) {
    return commissionRepository.sumPendingCommissionsByDoctor(doctor);
}

// Résumé des commissions par médecin (pour admin)
public List<CommissionSummaryDTO> getPendingCommissionTotalsByAllDoctors() {
    return commissionRepository.getPendingCommissionTotalsByAllDoctors()
        .stream()
        .map(this::convertToSummaryDTO)
        .collect(Collectors.toList());
}
```

**Règles de calcul :**
- **Taux** : 5% fixe du montant de la prescription
- **Arrondi** : 2 décimales (standard monétaire)
- **Conditions** : Uniquement pour prescriptions VALIDATED

---

#### 💸 PaymentService.java
**Traitement des paiements et réinitialisation des portefeuilles**

**Responsabilités complexes :**
- Traitement des paiements de commissions
- Mise à jour des statuts de commissions
- **Réinitialisation des soldes** des portefeuilles virtuels
- Création des liens paiement-commissions

**Workflow de paiement :**
```java
@Transactional
public Payment processFullPaymentForDoctor(UUID doctorId, String method, 
                                          String reference, String note, User paidBy) {
    // 1. Récupération des commissions en attente
    List<Commission> pendingCommissions = commissionService.getPendingCommissionsByDoctor(doctorId);
    
    // 2. Calcul du montant total
    BigDecimal totalAmount = calculateTotalAmount(pendingCommissions);
    
    // 3. Création du paiement
    Payment payment = createPayment(doctorId, totalAmount, method, reference, note, paidBy);
    
    // 4. Mise à jour des statuts de commissions
    updateCommissionsStatus(pendingCommissions);
    
    // 5. Réinitialisation du portefeuille virtuel
    resetVirtualWallet(doctorId);
    
    // 6. Création des liens paiement-commissions
    createPaymentCommissions(payment, pendingCommissions);
    
    return payment;
}
```

**Logique métier critique :**
- **Validation** : Présence de commissions en attente
- **Atomicité** : Toutes les opérations ou aucune
- **Réinitialisation** : Solde du portefeuille mis à 0 après paiement
- **Traçabilité** : Lien complet entre paiement et commissions

---

#### 💳 VirtualWalletService.java
**Gestion des portefeuilles virtuels**

**Responsabilités :**
- Suivi des soldes des médecins
- Créditation des commissions
- Réinitialisation après paiement
- Consultation des soldes

**Opérations principales :**
```java
// Ajout de commission au portefeuille
@Transactional
public void addCommissionToWallet(Doctor doctor, BigDecimal amount) {
    VirtualWallet wallet = getWalletByDoctor(doctor);
    wallet.setBalance(wallet.getBalance().add(amount));
    wallet.setLastUpdated(LocalDateTime.now());
    virtualWalletRepository.save(wallet);
}

// Réinitialisation après paiement
@Transactional
public void resetWalletAfterPayment(Doctor doctor) {
    VirtualWallet wallet = getWalletByDoctor(doctor);
    wallet.setBalance(BigDecimal.ZERO);
    wallet.setLastUpdated(LocalDateTime.now());
    virtualWalletRepository.save(wallet);
}
```

**Règles de gestion :**
- **Unicité** : Un portefeuille par médecin
- **Non-négatif** : Le solde ne peut être négatif
- **Traçabilité** : Date de dernière mise à jour

---

## 🔄 Interactions entre Services

### 📊 Flux de Prescription → Paiement
```
PrescriptionService.createPrescription()
    ↓
CommissionService.generateCommission()
    ↓
VirtualWalletService.addCommissionToWallet()
    ↓
PaymentService.processFullPaymentForDoctor()
    ↓
VirtualWalletService.resetWalletAfterPayment()
```

### 🔄 Dépendances Circulaires Évitées
- **PrescriptionService** → CommissionService, VirtualWalletService
- **PaymentService** → CommissionService, VirtualWalletService
- **CommissionService** → Aucune dépendance circulaire

---

## 🛡️ Gestion des Transactions

### 🔄 Propagation
```java
@Transactional  // Par défaut : REQUIRED
public Prescription createPrescription(Prescription prescription) {
    // Toutes les opérations dans une seule transaction
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void logOperation(OperationLog log) {
    // Transaction indépendante pour le logging
}
```

### 🚨 Gestion des Erreurs
```java
@Transactional
public Payment processPayment(...) {
    try {
        // Opérations de paiement
        return payment;
    } catch (InsufficientFundsException e) {
        // Rollback automatique
        throw new PaymentException("Fonds insuffisants", e);
    }
}
```

---

## 📈 Optimisations

### ⚡ Performance
- **Batch operations** pour les mises à jour multiples
- **Lazy loading** pour les relations non essentielles
- **Cache** pour les données fréquemment accédées

### 🔄 Concurrency
```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public void updateWalletBalance(UUID doctorId, BigDecimal amount) {
    // Évite les conflits de mise à jour concurrente
}
```

### 📊 Monitoring
- **Logging** des opérations critiques
- **Métriques** de performance
- **Alertes** sur les erreurs

---

## 🧪 Tests

### 🎯 Tests Unitaires
```java
@Test
void testCommissionCalculation() {
    BigDecimal amount = new BigDecimal("1000");
    BigDecimal commission = commissionService.calculateCommissionAmount(amount);
    assertEquals(new BigDecimal("50.00"), commission);
}
```

### 🔄 Tests d'Intégration
```java
@SpringBootTest
@Transactional
class PaymentServiceIntegrationTest {
    @Test
    void testFullPaymentWorkflow() {
        // Test complet du flux de paiement
    }
}
```

---

## 🎯 Bonnes Pratiques

### 📝 Conventions
- **@Transactional** sur les méthodes modifiant les données
- **Validation** des paramètres en début de méthode
- **Exceptions** spécifiques pour chaque type d'erreur

### 🔒 Sécurité
- **Vérification des permissions** dans les services
- **Audit trail** des opérations sensibles
- **Sanitization** des entrées utilisateur

### 📊 Évolutivité
- **Interface-based** design pour faciliter les tests
- **Configuration externalisée** (taux de commission)
- **Extensibilité** pour nouveaux types de paiements
