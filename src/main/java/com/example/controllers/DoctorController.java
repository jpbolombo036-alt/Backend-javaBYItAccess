package com.example.controllers;

import com.example.entities.Commission;
import com.example.entities.Payment;
import com.example.entities.Prescription;
import com.example.entities.VirtualWallet;
import com.example.services.CommissionService;
import com.example.services.PaymentService;
import com.example.services.PrescriptionService;
import com.example.services.VirtualWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<List<Prescription>> getMyPrescriptions(@RequestParam UUID doctorId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctor(doctorId));
    }

    @GetMapping("/prescriptions/{id}")
    public ResponseEntity<Prescription> getPrescription(@PathVariable UUID id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    // Commission endpoints for doctor
    @GetMapping("/commissions")
    public ResponseEntity<List<Commission>> getMyCommissions(@RequestParam UUID doctorId) {
        return ResponseEntity.ok(commissionService.getCommissionsByDoctor(doctorId));
    }

    @GetMapping("/commissions/pending")
    public ResponseEntity<List<Commission>> getMyPendingCommissions(@RequestParam UUID doctorId) {
        return ResponseEntity.ok(commissionService.getPendingCommissionsByDoctor(doctorId));
    }

    @GetMapping("/commissions/total-pending")
    public ResponseEntity<Double> getMyPendingCommissionTotal(@RequestParam UUID doctorId) {
        return ResponseEntity.ok(commissionService.getPendingCommissionTotalByDoctor(doctorId).doubleValue());
    }

    // Payment endpoints for doctor
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getMyPayments(@RequestParam UUID doctorId) {
        return ResponseEntity.ok(paymentService.getPaymentsByDoctor(doctorId));
    }

    // Virtual wallet endpoint for doctor
    @GetMapping("/wallet")
    public ResponseEntity<VirtualWallet> getMyWallet(@RequestParam UUID doctorId) {
        return ResponseEntity.ok(virtualWalletService.getWalletByDoctor(doctorId));
    }
}
