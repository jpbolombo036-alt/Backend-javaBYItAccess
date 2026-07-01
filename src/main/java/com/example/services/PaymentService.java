package com.example.services;

import com.example.config.WebSocketNotificationService;
import com.example.entities.*;
import com.example.enums.CommissionStatus;
import com.example.repositories.PaymentRepository;
import com.example.repositories.PaymentCommissionRepository;
import com.example.repositories.CommissionRepository;
import com.example.repositories.VirtualWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    private WebSocketNotificationService notificationService;

    @Transactional
    public Payment processPayment(Payment payment, List<UUID> commissionIds) {
        VirtualWallet wallet = virtualWalletRepository.findByDoctorId(payment.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("Virtual wallet not found for doctor"));

        BigDecimal totalCommissionAmount = commissionIds.stream()
                .map(id -> commissionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Commission not found: " + id)))
                .map(Commission::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (payment.getAmount().compareTo(totalCommissionAmount) > 0) {
            throw new RuntimeException("Payment amount exceeds total commission amount");
        }

        if (wallet.getBalance().compareTo(payment.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance in virtual wallet");
        }

        Payment savedPayment = paymentRepository.save(payment);

        for (UUID commissionId : commissionIds) {
            Commission commission = commissionRepository.findById(commissionId)
                    .orElseThrow(() -> new RuntimeException("Commission not found: " + commissionId));

            PaymentCommission paymentCommission = new PaymentCommission();
            paymentCommission.setPayment(savedPayment);
            paymentCommission.setCommission(commission);
            paymentCommissionRepository.save(paymentCommission);

            commission.setStatus(CommissionStatus.PAID);
            commissionRepository.save(commission);
        }

        wallet.processPayment(savedPayment.getAmount());
        virtualWalletRepository.save(wallet);

        notificationService.broadcastPaymentProcessed(savedPayment, savedPayment.getDoctor().getId());

        return savedPayment;
    }

    @Transactional
    public Payment processFullPaymentForDoctor(UUID doctorId, String method, String reference, String note, User paidBy) {
        List<Commission> pendingCommissions = commissionRepository.findPendingCommissionsByDoctor(doctorId);

        if (pendingCommissions.isEmpty()) {
            throw new RuntimeException("No pending commissions found for doctor");
        }

        BigDecimal totalAmount = pendingCommissions.stream()
                .map(Commission::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Doctor doctor = pendingCommissions.get(0).getDoctor();

        Payment payment = new Payment();
        payment.setDoctor(doctor);
        payment.setPaidBy(paidBy);
        payment.setAmount(totalAmount);
        payment.setMethod(method);
        payment.setReference(reference);
        payment.setNote(note);

        Payment result = processPayment(payment, pendingCommissions.stream()
                .map(Commission::getId)
                .toList());

        return result;
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
