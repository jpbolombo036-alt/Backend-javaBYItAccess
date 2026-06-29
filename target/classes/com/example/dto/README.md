# 📁 DTO Package

Ce package contient les objets de transfert de données (DTO) utilisés pour structurer les réponses API et optimiser les communications client-serveur.

## 🎯 Objectif des DTOs

### 📦 Pourquoi utiliser des DTOs ?
- **Séparation des responsabilités** : Isoler la présentation des entités
- **Optimisation des réponses** : Ne transférer que les données nécessaires
- **Sécurité** : Éviter l'exposition de données sensibles
- **Flexibilité** : Adapter les réponses selon les besoins du client
- **Performance** : Réduire la taille des payloads JSON

---

## 📋 Liste des DTOs

### 💰 CommissionSummaryDTO.java
**Résumé des commissions par médecin**

**Utilisation principale :**
- Dashboard administrateur
- Reporting financier
- Vue agrégée des revenus

**Structure :**
```java
public class CommissionSummaryDTO {
    private UUID doctorId;           // Identifiant du médecin
    private String doctorName;       // Nom du médecin (pour affichage)
    private BigDecimal totalAmount;   // Montant total des commissions en attente
    private int commissionCount;      // Nombre de commissions en attente
}
```

**Cas d'usage typique :**
```java
// Dans CommissionService
public List<CommissionSummaryDTO> getPendingCommissionTotalsByAllDoctors() {
    return commissionRepository.getPendingCommissionTotalsByAllDoctors()
        .stream()
        .map(result -> new CommissionSummaryDTO(
            (UUID) result[0],           // doctorId
            (String) result[1],          // doctorName  
            (BigDecimal) result[2],      // totalAmount
            ((Number) result[3]).intValue() // commissionCount
        ))
        .collect(Collectors.toList());
}
```

**Exemple de réponse JSON :**
```json
[
  {
    "doctorId": "550e8400-e29b-41d4-a716-446655440000",
    "doctorName": "Dr. Smith",
    "totalAmount": 1500.00,
    "commissionCount": 3
  },
  {
    "doctorId": "660e8400-e29b-41d4-a716-446655440001", 
    "doctorName": "Dr. Johnson",
    "totalAmount": 750.00,
    "commissionCount": 2
  }
]
```

---

### 💊 PrescriptionDTO.java
**Format de réponse pour les prescriptions**

**Utilisation principale :**
- API de consultation des prescriptions
- Réponses optimisées pour les clients mobiles/web
- Masquage des données sensibles

**Structure :**
```java
public class PrescriptionDTO {
    private UUID id;                    // Identifiant de la prescription
    private String doctorName;          // Nom du médecin (calculé)
    private String patientName;         // Nom du patient (calculé)
    private LocalDate prescriptionDate;  // Date de prescription
    private BigDecimal totalAmount;      // Montant total
    private PrescriptionStatus status;   // Statut actuel
    private String notes;               // Notes médicales
    private LocalDateTime createdAt;     // Date de création
    
    // Champs calculés pour optimiser les requêtes
    private BigDecimal commissionAmount; // Montant de commission (5%)
    private String statusDisplay;        // Libellé du statut
}
```

**Conversion depuis l'entité :**
```java
public static PrescriptionDTO fromEntity(Prescription prescription) {
    PrescriptionDTO dto = new PrescriptionDTO();
    dto.setId(prescription.getId());
    dto.setDoctorName(prescription.getDoctor().getFullName());
    dto.setPatientName(prescription.getPatient().getFirstName() + " " + 
                       prescription.getPatient().getLastName());
    dto.setPrescriptionDate(prescription.getPrescriptionDate());
    dto.setTotalAmount(prescription.getTotalAmount());
    dto.setStatus(prescription.getStatus());
    dto.setNotes(prescription.getNotes());
    dto.setCreatedAt(prescription.getCreatedAt());
    
    // Champs calculés
    dto.setCommissionAmount(prescription.getTotalAmount().multiply(new BigDecimal("0.05")));
    dto.setStatusDisplay(getStatusDisplay(prescription.getStatus()));
    
    return dto;
}
```

**Exemple de réponse JSON :**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "doctorName": "Dr. Smith",
  "patientName": "John Doe", 
  "prescriptionDate": "2026-05-05",
  "totalAmount": 1000.00,
  "status": "VALIDATED",
  "notes": "Prescription pour traitement cardiaque",
  "createdAt": "2026-05-05T16:00:00",
  "commissionAmount": 50.00,
  "statusDisplay": "Validée"
}
```

---

## 🔄 Patterns d'Utilisation

### 📦 Conversion Entity → DTO
```java
// Pattern statique pour la conversion
public static PrescriptionDTO fromEntity(Prescription prescription) {
    // Logique de mapping
}

// Pattern builder pour plus de flexibilité  
public PrescriptionDTOBuilder fromEntity(Prescription prescription) {
    return new PrescriptionDTOBuilder()
        .id(prescription.getId())
        .doctorName(prescription.getDoctor().getFullName())
        .build();
}
```

### 🎯 Optimisation des Requêtes
```java
// Éviter le N+1 problem avec des requêtes optimisées
@Query("SELECT p, d.fullName, pt.firstName, pt.lastName " +
       "FROM Prescription p JOIN p.doctor d JOIN p.patient pt " +
       "WHERE p.doctor.id = :doctorId")
List<Object[]> findPrescriptionsWithNames(@Param("doctorId") UUID doctorId);

// Conversion directe vers DTO
return results.stream()
    .map(this::convertToDTO)
    .collect(Collectors.toList());
```

### 🔒 Masquage des Données Sensibles
```java
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private UserRole role;
    // PAS de mot de passe ou données sensibles
}
```

---

## 🚀 Avantages des DTOs

### ⚡ Performance
- **Payload réduit** : Moins de données transférées
- **Requêtes optimisées** : Un seul appel pour récupérer les données nécessaires
- **Cache friendly** : Structure stable pour la mise en cache

### 🔒 Sécurité
- **Masquage automatique** : Données sensibles non exposées
- **Validation intégrée** : Contrôle des données en entrée/sortie
- **Audit trail** : Traçabilité des transferts de données

### 🛠️ Maintenance
- **Contrôle des versions** : Évolution indépendante des entités
- **Réutilisabilité** : Même DTO pour différents endpoints
- **Testabilité** : Isolation des tests de présentation

---

## 📊 Bonnes Pratiques

### 📝 Conventions de Nommage
- **DTO** suffixe explicite : `UserDTO`, `PrescriptionDTO`
- **Response** pour les réponses : `CreateUserResponse`
- **Request** pour les requêtes : `CreatePrescriptionRequest`

### 🔄 Méthodes de Conversion
```java
// Pattern factory
public static PrescriptionDTO from(Prescription entity) { ... }

// Pattern mapper
public class PrescriptionMapper {
    public PrescriptionDTO toDto(Prescription entity) { ... }
    public Prescription toEntity(PrescriptionDTO dto) { ... }
}

// Pattern builder
public class PrescriptionDTOBuilder {
    public PrescriptionDTOBuilder id(UUID id) { ... }
    public PrescriptionDTO build() { ... }
}
```

### 🧪 Tests des DTOs
```java
@Test
void testPrescriptionDTOConversion() {
    // Given
    Prescription prescription = createTestPrescription();
    
    // When  
    PrescriptionDTO dto = PrescriptionDTO.fromEntity(prescription);
    
    // Then
    assertEquals(prescription.getId(), dto.getId());
    assertEquals("Dr. Smith", dto.getDoctorName());
    assertEquals(50.00, dto.getCommissionAmount());
}
```

---

## 🔮 Extensions Possibles

### 📊 Nouveaux DTOs
```java
// Dashboard administrateur
public class AdminDashboardDTO {
    private int totalPrescriptions;
    private int pendingCommissions;
    private BigDecimal totalRevenue;
    private List<CommissionSummaryDTO> topEarners;
}

// Rapport financier
public class FinancialReportDTO {
    private UUID doctorId;
    private String period;
    private BigDecimal totalPaid;
    private BigDecimal totalPending;
    private List<PaymentDTO> payments;
}

// Statistiques médecin
public class DoctorStatsDTO {
    private UUID doctorId;
    private int prescriptionCount;
    private BigDecimal totalEarned;
    private int patientCount;
    private double averageCommission;
}
```

### 🔄 Mapping Avancé
```java
// Avec MapStruct pour le mapping automatique
@Mapper
public interface PrescriptionMapper {
    PrescriptionDTO toDto(Prescription prescription);
    List<PrescriptionDTO> toDtoList(List<Prescription> prescriptions);
}

// Avec ModelMapper pour configuration flexible
@Bean
public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.createTypeMap(Prescription.class, PrescriptionDTO.class)
        .addMapping(src -> src.getDoctor().getFullName(), PrescriptionDTO::setDoctorName);
    return mapper;
}
```

---

## 🎯 Conclusion

Les DTOs sont essentiels pour :
- **Séparer** la logique métier de la présentation
- **Optimiser** les performances des API
- **Sécuriser** les transferts de données
- **Faciliter** l'évolution du système

Ils représentent une couche d'abstraction importante qui assure la robustesse et la maintenabilité de l'architecture REST de l'application.
