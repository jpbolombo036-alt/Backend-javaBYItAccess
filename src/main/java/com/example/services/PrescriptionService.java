package com.example.services;

import com.example.entities.*;
import com.example.enums.PrescriptionStatus;
import com.example.repositories.PrescriptionRepository;
import com.example.repositories.CommissionRepository;
import com.example.repositories.VirtualWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private VirtualWalletRepository virtualWalletRepository;

    @Transactional
    public Prescription createPrescription(Prescription prescription) {
        // Save the prescription first
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        
        // Automatically create a commission (5% of total amount)
        Commission commission = new Commission();
        commission.setPrescription(savedPrescription);
        commission.setDoctor(savedPrescription.getDoctor());
        commission.setBaseAmount(savedPrescription.getTotalAmount());
        commission.setRate(BigDecimal.valueOf(0.05)); // 5% default rate
        
        // Save the commission
        Commission savedCommission = commissionRepository.save(commission);
        
        // Update the prescription with the commission
        savedPrescription.setCommission(savedCommission);
        
        // Update doctor's virtual wallet
        VirtualWallet wallet = virtualWalletRepository.findByDoctorId(savedPrescription.getDoctor().getId())
                .orElse(new VirtualWallet(savedPrescription.getDoctor()));
        wallet.addCommission(savedCommission.getCommissionAmount());
        virtualWalletRepository.save(wallet);
        
        return prescriptionRepository.save(savedPrescription);
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    public List<Prescription> getPrescriptionsByDoctor(UUID doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }

    public List<Prescription> getPrescriptionsByStatus(PrescriptionStatus status) {
        return prescriptionRepository.findByStatus(status);
    }

    @Transactional
    public Prescription updatePrescriptionStatus(UUID prescriptionId, PrescriptionStatus status) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        
        prescription.setStatus(status);
        return prescriptionRepository.save(prescription);
    }

    public Prescription getPrescriptionById(UUID prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
    }
}
