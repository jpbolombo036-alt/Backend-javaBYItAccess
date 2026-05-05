package com.example.controllers;

import com.example.entities.*;
import com.example.enums.PrescriptionStatus;
import com.example.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VirtualWalletService virtualWalletService;

    // Prescription endpoints
    @PostMapping("/prescriptions")
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        Prescription created = prescriptionService.createPrescription(prescription);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/prescriptions/{id}")
    public ResponseEntity<Prescription> getPrescription(@PathVariable UUID id) {
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
    public ResponseEntity<Payment> processFullPaymentForDoctor(
            @PathVariable UUID doctorId,
            @RequestBody Map<String, String> paymentDetails) {
        
        // Create a dummy user for now (in real app, get from authentication)
        User paidBy = new User(); // This should come from security context
        
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
