package com.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entité représentant un médecin dans le système de gestion médicale
 * 
 * Cette classe contient les informations spécifiques à la pratique médicale
 * qui ne sont pas présentes dans l'entité User de base. Chaque médecin
 * est lié à un utilisateur de rôle DOCTOR.
 * 
 * Le médecin dispose automatiquement d'un portefeuille virtuel pour
 * recevoir les commissions générées par les prescriptions.
 * 
 * @author Medical Prescription System
 * @version 1.0
 */
@Entity
@Table(name = "doctors")
public class Doctor {
    
    /**
     * Identifiant unique du médecin généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Relation OneToOne avec l'entité User
     * Chaque médecin est associé à un utilisateur de rôle DOCTOR
     * La relation est en LAZY pour optimiser les performances
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    /**
     * Nom complet du médecin
     * Peut être différent du nom dans l'entité User si nécessaire
     */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /**
     * Spécialité médicale du médecin
     * Ex: Cardiologie, Pédiatrie, Généraliste, etc.
     */
    @Column(nullable = false)
    private String specialty;

    /**
     * Numéro de licence médicale
     * Unique et obligatoire pour l'identification légale
     */
    @Column(name = "license_no", nullable = false, unique = true)
    private String licenseNo;

    /**
     * Numéro de téléphone professionnel du médecin
     * Utilisé pour les communications administratives
     */
    @Column(nullable = false)
    private String phone;

    /**
     * Statut d'activité du médecin
     * true = médecin actif, false = médecin inactif
     * Permet de suspendre temporairement un médecin
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Relation OneToOne avec l'entité VirtualWallet
     * Chaque médecin dispose d'un portefeuille virtuel pour recevoir
     * les commissions générées par les prescriptions
     * La relation est en LAZY pour optimiser les performances
     */
    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private VirtualWallet virtualWallet;

    /**
     * Constructeur par défaut
     */
    public Doctor() {}

    /**
     * Constructeur avec paramètres pour créer un médecin
     * 
     * @param user Utilisateur associé (de rôle DOCTOR)
     * @param fullName Nom complet du médecin
     * @param specialty Spécialité médicale
     * @param licenseNo Numéro de licence médicale
     * @param phone Numéro de téléphone professionnel
     */
    public Doctor(User user, String fullName, String specialty, String licenseNo, String phone) {
        this.user = user;
        this.fullName = fullName;
        this.specialty = specialty;
        this.licenseNo = licenseNo;
        this.phone = phone;
    }

    // ==================== GETTERS ET SETTERS ====================
    
    /**
     * @return L'identifiant unique du médecin
     */
    public UUID getId() { return id; }
    
    /**
     * @param id Le nouvel identifiant du médecin
     */
    public void setId(UUID id) { this.id = id; }

    /**
     * @return L'utilisateur associé au médecin
     */
    public User getUser() { return user; }
    
    /**
     * @param user Le nouvel utilisateur associé
     */
    public void setUser(User user) { this.user = user; }

    /**
     * @return Le nom complet du médecin
     */
    public String getFullName() { return fullName; }
    
    /**
     * @param fullName Le nouveau nom complet
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * @return La spécialité médicale du médecin
     */
    public String getSpecialty() { return specialty; }
    
    /**
     * @param specialty La nouvelle spécialité médicale
     */
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    /**
     * @return Le numéro de licence médicale
     */
    public String getLicenseNo() { return licenseNo; }
    
    /**
     * @param licenseNo Le nouveau numéro de licence
     */
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

    /**
     * @return Le numéro de téléphone professionnel
     */
    public String getPhone() { return phone; }
    
    /**
     * @param phone Le nouveau numéro de téléphone
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * @return Le statut d'activité du médecin
     */
    public Boolean getIsActive() { return isActive; }
    
    /**
     * @param isActive Le nouveau statut d'activité
     */
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    /**
     * @return Le portefeuille virtuel associé au médecin
     */
    public VirtualWallet getVirtualWallet() { return virtualWallet; }
    
    /**
     * @param virtualWallet Le nouveau portefeuille virtuel
     */
    public void setVirtualWallet(VirtualWallet virtualWallet) { this.virtualWallet = virtualWallet; }
}
