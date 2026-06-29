package com.example.entities;

import com.example.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant un utilisateur du système de gestion médicale
 * 
 * Cette classe est la base de l'authentification et de la gestion des rôles
 * dans l'application. Chaque utilisateur peut être soit un administrateur, soit un médecin.
 * 
 * Les utilisateurs médecins sont liés à une entité Doctor qui contient
 * les informations spécifiques à la pratique médicale.
 * 
 * @author Medical Prescription System
 * @version 1.0
 */
@Entity
@Table(name = "users")
public class User {
    
    /**
     * Identifiant unique de l'utilisateur généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nom complet de l'utilisateur
     */
    @Column(nullable = false)
    private String name;

    /**
     * Email de l'utilisateur (doit être unique dans le système)
     * Utilisé pour l'authentification
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Mot de passe hashé de l'utilisateur
     * Stocké sous forme cryptée pour des raisons de sécurité
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Rôle de l'utilisateur dans le système
     * Définit les permissions et l'accès aux fonctionnalités
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * Date et heure de création du compte utilisateur
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Statut d'activation du compte
     * true = compte actif, false = compte désactivé
     */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Relation OneToOne avec l'entité Doctor
     * Un utilisateur médecin est lié à une entité Doctor contenant
     * les informations spécifiques à la pratique médicale
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Doctor doctor;

    /**
     * Constructeur par défaut
     * Initialise la date de création à l'heure actuelle
     */
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres pour créer un utilisateur
     * 
     * @param name Nom de l'utilisateur
     * @param email Email de l'utilisateur
     * @param passwordHash Mot de passe hashé
     * @param role Rôle de l'utilisateur (ADMIN ou DOCTOR)
     */
    public User(String name, String email, String passwordHash, UserRole role) {
        this();
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ==================== GETTERS ET SETTERS ====================
    
    /**
     * @return L'identifiant unique de l'utilisateur
     */
    public UUID getId() { return id; }
    
    /**
     * @param id Le nouvel identifiant de l'utilisateur
     */
    public void setId(UUID id) { this.id = id; }

    /**
     * @return Le nom de l'utilisateur
     */
    public String getName() { return name; }
    
    /**
     * @param name Le nouveau nom de l'utilisateur
     */
    public void setName(String name) { this.name = name; }

    /**
     * @return L'email de l'utilisateur
     */
    public String getEmail() { return email; }
    
    /**
     * @param email Le nouvel email de l'utilisateur
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * @return Le mot de passe hashé de l'utilisateur
     */
    public String getPasswordHash() { return passwordHash; }
    
    /**
     * @param passwordHash Le nouveau mot de passe hashé
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * @return Le rôle de l'utilisateur (ADMIN ou DOCTOR)
     */
    public UserRole getRole() { return role; }
    
    /**
     * @param role Le nouveau rôle de l'utilisateur
     */
    public void setRole(UserRole role) { this.role = role; }

    /**
     * @return La date de création du compte
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    /**
     * @param createdAt La nouvelle date de création
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * @return L'entité Doctor associée (si l'utilisateur est un médecin)
     */
    public Doctor getDoctor() { return doctor; }
    
    /**
     * @param doctor L'entité Doctor à associer
     */
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    /**
     * @return Le statut d'activation du compte
     */
    public boolean isActive() { return active; }
    
    /**
     * @param active Le nouveau statut d'activation
     */
    public void setActive(boolean active) { this.active = active; }
}
