package com.example.services;

import com.example.entities.*;
import com.example.enums.CommissionStatus;
import com.example.repositories.PaymentRepository;
import com.example.repositories.PaymentCommissionRepository;
import com.example.repositories.CommissionRepository;
import com.example.repositories.VirtualWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentCommissionRepository paymentCommissionRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private VirtualWalletRepository virtualWalletRepository;

    @Transactional
    public Payment processPayment(Payment payment, List<UUID> commissionIds) {
        // Save the payment
        Payment savedPayment = paymentRepository.save(payment);

        // Get the doctor's virtual wallet
        VirtualWallet wallet = virtualWalletRepository.findByDoctorId(payment.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("Virtual wallet not found for doctor"));

        // Process each commission
        for (UUID commissionId : commissionIds) {
            Commission commission = commissionRepository.findById(commissionId)
                    .orElseThrow(() -> new RuntimeException("Commission not found: " + commissionId));

            // Create payment-commission relationship
            PaymentCommission paymentCommission = new PaymentCommission();
            paymentCommission.setPayment(savedPayment);
            paymentCommission.setCommission(commission);
            paymentCommissionRepository.save(paymentCommission);

            // Update commission status to PAID
            commission.setStatus(CommissionStatus.PAID);
            commissionRepository.save(commission);
        }

        // Process payment from wallet (deduct from balance)
        wallet.processPayment(savedPayment.getAmount());
        virtualWalletRepository.save(wallet);

        return savedPayment;
    }

    @Transactional
    public Payment processFullPaymentForDoctor(UUID doctorId, String method, String reference, String note, User paidBy) {
        // Get all pending commissions for the doctor
        List<Commission> pendingCommissions = commissionRepository.findPendingCommissionsByDoctor(doctorId);
        
        if (pendingCommissions.isEmpty()) {
            throw new RuntimeException("No pending commissions found for doctor");
        }

        // Calculate total amount
        double totalAmount = pendingCommissions.stream()
                .mapToDouble(c -> c.getCommissionAmount().doubleValue())
                .sum();

        // Get doctor entity
        Doctor doctor = pendingCommissions.get(0).getDoctor();

        // Create payment
        Payment payment = new Payment();
        payment.setDoctor(doctor);
        payment.setPaidBy(paidBy);
        payment.setAmount(java.math.BigDecimal.valueOf(totalAmount));
        payment.setMethod(method);
        payment.setReference(reference);
        payment.setNote(note);

        // Process payment
        return processPayment(payment, pendingCommissions.stream()
                .map(Commission::getId)
                .toList());
    }

    public List<Payment> getPaymentsByDoctor(UUID doctorId) {
        return paymentRepository.findByDoctorIdOrderByPaymentDateDesc(doctorId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
