# 📁 Enums Package

Ce package contient toutes les énumérations qui définissent les états et rôles constants du système.

## 🏷️ Types d'Énumérations

### 👤 UserRole.java
**Définit les rôles des utilisateurs dans le système**

```java
public enum UserRole {
    ADMIN,  // Administrateur - accès complet au système
    DOCTOR  // Médecin - accès limité à ses propres données
}
```

**Utilisation :**
- Contrôle d'accès via Spring Security
- Validation des permissions dans les contrôleurs
- Filtrage des données selon le rôle

**Impact sur le système :**
- **ADMIN** : Accès à toutes les prescriptions, commissions, paiements
- **DOCTOR** : Accès uniquement à ses prescriptions et commissions

---

### 📋 PrescriptionStatus.java
**Définit les états du cycle de vie d'une prescription**

```java
public enum PrescriptionStatus {
    PENDING,    // En attente de validation
    VALIDATED,  // Prescription validée et active
    CANCELLED   // Prescription annulée
}
```

**Flux de travail :**
1. **PENDING** → Création initiale
2. **VALIDATED** → Validation par l'administrateur
3. **CANCELLED** → Annulation (retour possible à PENDING)

**Impact sur les commissions :**
- Seules les prescriptions VALIDATED génèrent des commissions
- Les prescriptions CANCELLED n'affectent pas les finances

---

### 💰 CommissionStatus.java
**Définit les états de paiement des commissions**

```java
public enum CommissionStatus {
    PENDING,  // Commission en attente de paiement
    PAID      // Commission payée au médecin
}
```

**Cycle de vie :**
1. **PENDING** → Création automatique lors de la prescription
2. **PAID** → Traitement du paiement via PaymentService

**Impact financier :**
- **PENDING** : Comptabilisée dans le portefeuille virtuel
- **PAID** : Retirée du portefeuille, historique conservé

---

## 🔄 Interactions entre Énumérations

### Prescription → Commission
```
Prescription.VALIDATED → Commission.PENDING
Prescription.CANCELLED → Aucune commission
```

### Commission → Paiement
```
Commission.PENDING → Paiement → Commission.PAID
```

### Rôle → Permissions
```
UserRole.ADMIN → Accès complet
UserRole.DOCTOR → Accès filtré (ses données uniquement)
```

## 🛡️ Validation et Sécurité

### Contrôles dans le code
- **Validation des statuts** dans les services
- **Vérification des rôles** dans les contrôleurs
- **Transition d'états** contrôlée

### Exemples de validation
```java
// Seul un admin peut valider une prescription
if (user.getRole() == UserRole.ADMIN) {
    prescription.setStatus(PrescriptionStatus.VALIDATED);
}

// Paiement uniquement sur commissions pending
if (commission.getStatus() == CommissionStatus.PENDING) {
    processPayment(commission);
}
```

## 📊 Impact sur la Base de Données

### Stockage
- **Enums en String** : Stockage lisible dans la base
- **Indexation** : Performance optimale des requêtes
- **Intégrité** : Pas de valeurs invalides possibles

### Requêtes typiques
```sql
-- Trouver les commissions en attente
SELECT * FROM commissions WHERE status = 'PENDING';

-- Prescriptions validées par médecin
SELECT * FROM prescriptions WHERE status = 'VALIDATED';
```

## 🎯 Bonnes Pratiques

### Utilisation
- **Toujours vérifier** les valeurs avant utilisation
- **Utiliser switch** pour les traitements par cas
- **Documenter** les transitions autorisées

### Évolution
- **Ajout de statuts** : Réfléchir à l'impact sur le flux
- **Modification de rôles** : Mettre à jour la sécurité
- **Tests unitaires** : Couvrir tous les cas

## 🔮 Extensions Possibles

### Nouveaux rôles
```java
public enum UserRole {
    ADMIN,
    DOCTOR,
    PHARMACIST,    // Futur rôle pharmacien
    NURSE         // Futur rôle infirmier
}
```

### Nouveaux statuts
```java
public enum PrescriptionStatus {
    PENDING,
    VALIDATED,
    CANCELLED,
    EXPIRED,      // Prescription expirée
    RENEWED       // Prescription renouvelée
}
```
