# 📁 Entities Package

Ce package contient toutes les entités JPA qui représentent les tables de la base de données du système de gestion des prescriptions médicales.

## 🏗️ Structure des Entités

### 👤 User.java
**Entité de base pour les utilisateurs du système**
- **Rôle** : Fondation de l'authentification
- **Champs** : id, name, email, passwordHash, role, createdAt, active
- **Relations** : OneToOne avec Doctor
- **Utilité** : Gestion des comptes (admin/médecin)

### 👨‍⚕️ Doctor.java  
**Informations spécifiques aux médecins**
- **Rôle** : Extension des utilisateurs médecins
- **Champs** : id, user, fullName, specialty, licenseNo, phone, isActive
- **Relations** : OneToOne avec User et VirtualWallet
- **Utilité** : Données professionnelles des médecins

### 🧑‍⚕️ Patient.java
**Informations sur les patients**
- **Rôle** : Gestion des patients
- **Champs** : id, firstName, lastName, dateOfBirth, gender, phone, address
- **Relations** : Plusieurs prescriptions
- **Utilité** : Base de données patients

### 💊 Prescription.java
**Prescriptions médicales avec cycle de vie**
- **Rôle** : Cœur du système de prescriptions
- **Champs** : id, doctor, patient, processedBy, prescriptionDate, totalAmount, status, notes, createdAt
- **Relations** : ManyToOne avec Doctor/Patient/User, OneToOne avec Commission
- **Utilité** : Gestion des prescriptions et génération automatique des commissions

### 💰 Commission.java
**Commissions générées automatiquement (5%)**
- **Rôle** : Calcul et suivi des commissions
- **Champs** : id, prescription, doctor, amount, status, createdAt, paidAt
- **Relations** : OneToOne avec Prescription, ManyToOne avec Doctor
- **Utilité** : Suivi des revenus des médecins

### 💳 VirtualWallet.java
**Portefeuilles virtuels des médecins**
- **Rôle** : Gestion financière des médecins
- **Champs** : id, doctor, balance, lastUpdated, createdAt
- **Relations** : OneToOne avec Doctor
- **Utilité** : Accumulation et suivi des commissions

### 💸 Payment.java
**Paiements des commissions**
- **Rôle** : Traitement des paiements
- **Champs** : id, doctor, amount, method, reference, note, paidBy, paidAt
- **Relations** : ManyToOne avec Doctor et User
- **Utilité** : Historique des paiements

### 🔗 PaymentCommission.java
**Table de jonction paiements-commissions**
- **Rôle** : Lien entre paiements et commissions
- **Champs** : id, payment, commission, amount
- **Relations** : ManyToOne avec Payment et Commission
- **Utilité** : Traçabilité détaillée des paiements

## 🔄 Flux de Données

1. **Création Prescription** → Génération Commission (5%)
2. **Commission** → Créditation Portefeuille Virtuel
3. **Paiement** → Mise à jour Statut Commission
4. **Paiement** → Réinitialisation Solde Portefeuille

## 🔗 Relations Clés

- **User ↔ Doctor** : OneToOne (utilisateur médecin)
- **Doctor ↔ VirtualWallet** : OneToOne (portefeuille personnel)
- **Prescription ↔ Commission** : OneToOne (commission unique)
- **Payment ↔ PaymentCommission** : OneToMany (détails paiement)

## 📊 Contraintes et Validation

- **Email** unique dans User
- **LicenseNo** unique dans Doctor
- **Statuts** validés par enums
- **Relations** en cascade pour la cohérence
- **UUID** comme identifiants primaires

## 🎯 Points d'Attention

- **Lazy Loading** optimisé pour les performances
- **Cascade** pour la suppression automatique
- **Fetch Strategies** adaptées aux cas d'usage
- **Annotations JPA** complètes et cohérentes
