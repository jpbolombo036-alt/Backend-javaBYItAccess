# Package com.example

Ce package contient l'ensemble du code source de l'application de gestion des prescriptions médicales.

## 📁 Structure du Package

### 📂 entities/
Contient toutes les entités JPA qui représentent les tables de la base de données :
- **User.java** - Entité de base pour les utilisateurs (admin/médecin)
- **Doctor.java** - Informations spécifiques aux médecins
- **Patient.java** - Informations sur les patients
- **Prescription.java** - Prescriptions médicales avec statuts
- **Commission.java** - Commissions générées automatiquement (5%)
- **VirtualWallet.java** - Portefeuilles virtuels des médecins
- **Payment.java** - Paiements des commissions
- **PaymentCommission.java** - Table de jonction paiements-commissions

### 📂 enums/
Contient les énumérations du système :
- **UserRole.java** - Rôles des utilisateurs (ADMIN, DOCTOR)
- **PrescriptionStatus.java** - Statuts des prescriptions (PENDING, VALIDATED, CANCELLED)
- **CommissionStatus.java** - Statuts des commissions (PENDING, PAID)

### 📂 repositories/
Contient les interfaces Spring Data JPA pour l'accès aux données :
- **UserRepository.java** - Accès aux utilisateurs
- **DoctorRepository.java** - Accès aux médecins
- **PatientRepository.java** - Accès aux patients
- **PrescriptionRepository.java** - Accès aux prescriptions
- **CommissionRepository.java** - Accès aux commissions
- **VirtualWalletRepository.java** - Accès aux portefeuilles virtuels
- **PaymentRepository.java** - Accès aux paiements
- **PaymentCommissionRepository.java** - Accès aux relations paiements-commissions

### 📂 services/
Contient la logique métier de l'application :
- **PrescriptionService.java** - Gestion des prescriptions et création automatique des commissions
- **CommissionService.java** - Calcul et gestion des commissions
- **PaymentService.java** - Traitement des paiements et réinitialisation des portefeuilles
- **VirtualWalletService.java** - Gestion des portefeuilles virtuels

### 📂 controllers/
Contient les contrôleurs REST qui exposent les API :
- **AdminController.java** - API pour les administrateurs (accès complet)
- **DoctorController.java** - API pour les médecins (accès limité à leurs données)
- **UserController.java** - API pour la gestion des utilisateurs (création admin/médecin)

### 📂 dto/
Contient les objets de transfert de données pour les réponses API :
- **CommissionSummaryDTO.java** - Résumé des commissions par médecin
- **PrescriptionDTO.java** - Format de réponse pour les prescriptions

### 📂 config/
Contient les classes de configuration de l'application :
- **SecurityConfig.java** - Configuration Spring Security et contrôle d'accès
- **DataInitializer.java** - Initialisation des données de démonstration

### 📄 Main.java
Point d'entrée de l'application Spring Boot.

## 🔄 Flux de Travail

1. **Création d'utilisateurs** via UserController
2. **Création de prescriptions** via AdminController
3. **Génération automatique** des commissions (5% du montant)
4. **Créditation** du portefeuille virtuel du médecin
5. **Traitement des paiements** via PaymentService
6. **Réinitialisation** du solde du portefeuille après paiement

## 🔐 Sécurité

- Accès par rôle (ADMIN/DOCTOR)
- Hashage des mots de passe
- Endpoints publics pour la création d'utilisateurs
- Endpoints protégés selon le rôle

## 📊 Base de Données

8 tables interconnectées avec relations JPA :
- users (utilisateurs de base)
- doctors (informations médicales)
- patients (informations patients)
- prescriptions (prescriptions médicales)
- commissions (commissions automatiques)
- virtual_wallets (portefeuilles virtuels)
- payments (paiements effectués)
- payment_commissions (jonction paiements-commissions)
