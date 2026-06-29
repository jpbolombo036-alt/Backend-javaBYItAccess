package com.example.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) pour le résumé des commissions par médecin
 * 
 * Cette classe est utilisée pour transférer des informations agrégées sur les commissions
 * d'un médecin vers l'interface utilisateur, notamment pour le dashboard administrateur.
 * Elle permet d'optimiser les réponses API en évitant de transférer toutes les entités
 * Commission complètes lorsque seule une vue d'ensemble est nécessaire.
 * 
 * Utilisation principale :
 * - Dashboard administrateur pour voir l'état des commissions
 * - Reporting financier par médecin
 * - Vue agrégée des revenus en attente
 * 
 * @author Medical Prescription System
 * @version 1.0
 */
public class CommissionSummaryDTO {
    
    /**
     * Identifiant unique du médecin
     * Permet de lier ce résumé à l'entité Doctor correspondante
     * Utilisé pour les actions de navigation ou de filtrage
     */
    private UUID doctorId;
    
    /**
     * Nom complet du médecin pour affichage dans l'interface
     * Champ calculé pour éviter les requêtes supplémentaires côté client
     * Format typique : "Dr. Nom Prénom"
     */
    private String doctorName;
    
    /**
     * Montant total des commissions en attente de paiement pour ce médecin
     * Calculé en sommant toutes les commissions avec statut PENDING
     * Utilise BigDecimal pour une précision monétaire exacte
     * Format : 2 décimales, ex: 1250.50
     */
    private BigDecimal totalPendingCommissions;
    
    /**
     * Nombre de commissions en attente pour ce médecin
     * Permet de donner un contexte sur le volume d'activité
     * Utilisé pour les statistiques et le reporting
     * Exemple : 3 commissions en attente
     */
    private int pendingCommissionCount;

    /**
     * Constructeur par défaut
     * Requis par certains frameworks (Jackson, JPA, etc.)
     */
    public CommissionSummaryDTO() {}

    /**
     * Constructeur avec tous les paramètres
     * Permet de créer facilement un DTO à partir des résultats de requêtes agrégées
     * 
     * @param doctorId Identifiant unique du médecin
     * @param doctorName Nom complet du médecin pour affichage
     * @param totalPendingCommissions Montant total des commissions en attente
     * @param pendingCommissionCount Nombre de commissions en attente
     */
    public CommissionSummaryDTO(UUID doctorId, String doctorName, BigDecimal totalPendingCommissions, int pendingCommissionCount) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.totalPendingCommissions = totalPendingCommissions;
        this.pendingCommissionCount = pendingCommissionCount;
    }

    // ==================== GETTERS ET SETTERS ====================
    
    /**
     * @return L'identifiant unique du médecin
     */
    public UUID getDoctorId() { return doctorId; }
    
    /**
     * @param doctorId Le nouvel identifiant du médecin
     */
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }

    /**
     * @return Le nom complet du médecin pour affichage
     */
    public String getDoctorName() { return doctorName; }
    
    /**
     * @param doctorName Le nouveau nom du médecin
     */
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    /**
     * @return Le montant total des commissions en attente
     */
    public BigDecimal getTotalPendingCommissions() { return totalPendingCommissions; }
    
    /**
     * @param totalPendingCommissions Le nouveau montant total des commissions en attente
     */
    public void setTotalPendingCommissions(BigDecimal totalPendingCommissions) { this.totalPendingCommissions = totalPendingCommissions; }

    /**
     * @return Le nombre de commissions en attente
     */
    public int getPendingCommissionCount() { return pendingCommissionCount; }
    
    /**
     * @param pendingCommissionCount Le nouveau nombre de commissions en attente
     */
    public void setPendingCommissionCount(int pendingCommissionCount) { this.pendingCommissionCount = pendingCommissionCount; }
}
