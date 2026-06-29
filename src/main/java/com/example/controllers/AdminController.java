package com.example.controllers;

import com.example.entities.*;
import com.example.enums.PrescriptionStatus;
import com.example.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Admin API", description = "API pour les administrateurs - gestion complète des prescriptions, commissions et paiements")
public class AdminController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VirtualWalletService virtualWalletService;

    @Autowired
    private com.example.repositories.UserRepository userRepository;

    // Prescription endpoints
    @PostMapping("/prescriptions")
    @Operation(summary = "Créer une nouvelle prescription", description = "Crée une prescription et génère automatiquement une commission de 5%")
    @ApiResponse(responseCode = "200", description = "Prescription créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<Prescription> createPrescription(
            @Parameter(description = "Détails de la prescription à créer") @RequestBody Prescription prescription) {
        Prescription created = prescriptionService.createPrescription(prescription);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/prescriptions")
    @Operation(summary = "Lister toutes les prescriptions", description = "Récupère la liste de toutes les prescriptions")
    @ApiResponse(responseCode = "200", description = "Liste des prescriptions récupérée avec succès")
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/prescriptions/{id}")
    @Operation(summary = "Récupérer une prescription par ID", description = "Récupère les détails d'une prescription spécifique")
    @ApiResponse(responseCode = "200", description = "Prescription trouvée")
    @ApiResponse(responseCode = "404", description = "Prescription non trouvée")
    public ResponseEntity<Prescription> getPrescription(
            @Parameter(description = "ID de la prescription") @PathVariable UUID id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @PutMapping("/prescriptions/{id}/status")
    public ResponseEntity<Prescription> updatePrescriptionStatus(
            @PathVariable UUID id, 
            @RequestParam PrescriptionStatus status) {
        return ResponseEntity.ok(prescriptionService.updatePrescriptionStatus(id, status));
    }

    // Commission endpoints
    @GetMapping("/commissions/pending")
    public ResponseEntity<List<Commission>> getPendingCommissions() {
        return ResponseEntity.ok(commissionService.getAllPendingCommissions());
    }

    @GetMapping("/commissions/summary")
    public ResponseEntity<List<Object[]>> getCommissionSummary() {
        return ResponseEntity.ok(commissionService.getPendingCommissionTotalsByAllDoctors());
    }

    @GetMapping("/commissions/doctor/{doctorId}")
    public ResponseEntity<List<Commission>> getCommissionsByDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(commissionService.getCommissionsByDoctor(doctorId));
    }

    // Payment endpoints
    @PostMapping("/payments/doctor/{doctorId}/full")
    @Operation(summary = "Payer toutes les commissions d'un médecin", description = "Traite le paiement complet pour toutes les commissions en attente d'un médecin")
    @ApiResponse(responseCode = "200", description = "Paiement traité avec succès")
    @ApiResponse(responseCode = "400", description = "Aucune commission en attente")
    public ResponseEntity<Payment> processFullPaymentForDoctor(
            @Parameter(description = "ID du médecin") @PathVariable UUID doctorId,
            @Parameter(description = "Détails du paiement") @RequestBody Map<String, String> paymentDetails) {

        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        User paidBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur authentifié introuvable"));

        Payment payment = paymentService.processFullPaymentForDoctor(
            doctorId,
            paymentDetails.get("method"),
            paymentDetails.get("reference"),
            paymentDetails.get("note"),
            paidBy
        );
        
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/payments/doctor/{doctorId}")
    public ResponseEntity<List<Payment>> getPaymentsByDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(paymentService.getPaymentsByDoctor(doctorId));
    }

    // Virtual Wallet endpoints
    @GetMapping("/wallets")
    public ResponseEntity<List<VirtualWallet>> getAllWallets() {
        return ResponseEntity.ok(virtualWalletService.getAllWallets());
    }

    @GetMapping("/wallets/doctor/{doctorId}")
    public ResponseEntity<VirtualWallet> getWalletByDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(virtualWalletService.getWalletByDoctor(doctorId));
    }
}
