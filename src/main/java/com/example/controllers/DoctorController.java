package com.example.controllers;

import com.example.entities.Commission;
import com.example.entities.Payment;
import com.example.entities.Prescription;
import com.example.entities.VirtualWallet;
import com.example.services.CommissionService;
import com.example.services.PaymentService;
import com.example.services.PrescriptionService;
import com.example.services.VirtualWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "*")
@Tag(name = "Doctor API", description = "API pour les médecins - consultation de leurs prescriptions, commissions et paiements")
public class DoctorController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VirtualWalletService virtualWalletService;

    // Prescription endpoints for doctor
    @GetMapping("/prescriptions")
    @Operation(summary = "Lister mes prescriptions", description = "Récupère la liste de mes prescriptions")
    @ApiResponse(responseCode = "200", description = "Liste des prescriptions récupérée avec succès")
    public ResponseEntity<List<Prescription>> getMyPrescriptions(
            @Parameter(description = "ID du médecin") @RequestParam UUID doctorId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctor(doctorId));
    }

    @GetMapping("/prescriptions/{id}")
    public ResponseEntity<Prescription> getPrescription(@PathVariable UUID id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    // Commission endpoints for doctor
    @GetMapping("/commissions")
    @Operation(summary = "Lister mes commissions", description = "Récupère la liste de mes commissions")
    @ApiResponse(responseCode = "200", description = "Liste des commissions récupérée avec succès")
    public ResponseEntity<List<Commission>> getMyCommissions(
            @Parameter(description = "ID du médecin") @RequestParam UUID doctorId) {
        return ResponseEntity.ok(commissionService.getCommissionsByDoctor(doctorId));
    }

    @GetMapping("/commissions/pending")
    @Operation(summary = "Lister mes commissions en attente", description = "Récupère la liste de mes commissions en attente de paiement")
    @ApiResponse(responseCode = "200", description = "Liste des commissions en attente récupérée avec succès")
    public ResponseEntity<List<Commission>> getMyPendingCommissions(
            @Parameter(description = "ID du médecin") @RequestParam UUID doctorId) {
        return ResponseEntity.ok(commissionService.getPendingCommissionsByDoctor(doctorId));
    }

    @GetMapping("/commissions/total-pending")
    @Operation(summary = "Total de mes commissions en attente", description = "Calcule le montant total de mes commissions en attente")
    @ApiResponse(responseCode = "200", description = "Montant total calculé avec succès")
    public ResponseEntity<Double> getMyPendingCommissionTotal(
            @Parameter(description = "ID du médecin") @RequestParam UUID doctorId) {
        return ResponseEntity.ok(commissionService.getPendingCommissionTotalByDoctor(doctorId).doubleValue());
    }

    // Payment endpoints for doctor
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getMyPayments(@RequestParam UUID doctorId) {
        return ResponseEntity.ok(paymentService.getPaymentsByDoctor(doctorId));
    }

    // Virtual wallet endpoint for doctor
    @GetMapping("/wallet")
    @Operation(summary = "Consulter mon portefeuille virtuel", description = "Récupère les informations de mon portefeuille virtuel")
    @ApiResponse(responseCode = "200", description = "Portefeuille virtuel récupéré avec succès")
    public ResponseEntity<VirtualWallet> getMyWallet(
            @Parameter(description = "ID du médecin") @RequestParam UUID doctorId) {
        return ResponseEntity.ok(virtualWalletService.getWalletByDoctor(doctorId));
    }
}
