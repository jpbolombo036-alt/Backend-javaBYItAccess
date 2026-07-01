package com.example.controllers;

import com.example.entities.*;
import com.example.enums.PrescriptionStatus;
import com.example.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class WebSocketMessageController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VirtualWalletService virtualWalletService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private com.example.repositories.UserRepository userRepository;

    @MessageMapping("/prescription/create")
    public Prescription createPrescription(Map<String, Object> payload, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();

        UUID doctorId = UUID.fromString((String) payload.get("doctorId"));
        UUID patientId = UUID.fromString((String) payload.get("patientId"));
        BigDecimal totalAmount = new BigDecimal(payload.get("totalAmount").toString());
        String notes = (String) payload.getOrDefault("notes", "");

        Prescription prescription = new Prescription();
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        prescription.setDoctor(doctor);

        Patient patient = new Patient();
        patient.setId(patientId);
        prescription.setPatient(patient);
        prescription.setTotalAmount(totalAmount);
        prescription.setNotes(notes);
        prescription.setProcessedBy(currentUser);

        Prescription created = prescriptionService.createPrescription(prescription);

        messagingTemplate.convertAndSend("/topic/admin/prescriptions", created);
        messagingTemplate.convertAndSend("/topic/doctor/" + doctorId + "/prescriptions", created);

        return created;
    }

    @MessageMapping("/prescription/list")
    @SendTo("/topic/prescriptions")
    public List<Prescription> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

    @MessageMapping("/prescription/get")
    public Prescription getPrescription(Map<String, String> payload) {
        UUID id = UUID.fromString(payload.get("id"));
        return prescriptionService.getPrescriptionById(id);
    }

    @MessageMapping("/prescription/updateStatus")
    public Prescription updatePrescriptionStatus(Map<String, Object> payload, Authentication auth) {
        UUID id = UUID.fromString((String) payload.get("id"));
        PrescriptionStatus status = PrescriptionStatus.valueOf((String) payload.get("status"));

        Prescription updated = prescriptionService.updatePrescriptionStatus(id, status);
        messagingTemplate.convertAndSend("/topic/admin/prescriptions", updated);
        messagingTemplate.convertAndSend("/topic/doctor/" + updated.getDoctor().getId() + "/prescriptions", updated);

        return updated;
    }

    @MessageMapping("/commission/list")
    public List<Commission> getCommissions(Map<String, Object> payload) {
        if (payload != null && payload.containsKey("doctorId")) {
            UUID doctorId = UUID.fromString((String) payload.get("doctorId"));
            return commissionService.getCommissionsByDoctor(doctorId);
        }
        return commissionService.getAllPendingCommissions();
    }

    @MessageMapping("/commission/summary")
    public List<Object[]> getCommissionSummary() {
        return commissionService.getPendingCommissionTotalsByAllDoctors();
    }

    @MessageMapping("/payment/process")
    public Payment processPayment(Map<String, Object> payload, Authentication auth) {
        User paidBy = (User) auth.getPrincipal();

        UUID doctorId = UUID.fromString((String) payload.get("doctorId"));
        String method = (String) payload.get("method");
        String reference = (String) payload.get("reference");
        String note = (String) payload.get("note");

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Payment payment = paymentService.processFullPaymentForDoctor(
                doctorId, method, reference, note, paidBy
        );

        messagingTemplate.convertAndSend("/topic/admin/payments", payment);
        messagingTemplate.convertAndSend("/topic/doctor/" + doctorId + "/payments", payment);

        return payment;
    }

    @MessageMapping("/payments/list")
    public List<Payment> getPayments(Map<String, Object> payload) {
        if (payload != null && payload.containsKey("doctorId")) {
            UUID doctorId = UUID.fromString((String) payload.get("doctorId"));
            return paymentService.getPaymentsByDoctor(doctorId);
        }
        return paymentService.getAllPayments();
    }

    @MessageMapping("/wallet/get")
    public VirtualWallet getWallet(Map<String, Object> payload) {
        if (payload != null && payload.containsKey("doctorId")) {
            UUID doctorId = UUID.fromString((String) payload.get("doctorId"));
            return virtualWalletService.getWalletByDoctor(doctorId);
        }
        throw new RuntimeException("doctorId is required");
    }

    @MessageMapping("/wallet/list")
    public List<VirtualWallet> getAllWallets() {
        return virtualWalletService.getAllWallets();
    }
}
