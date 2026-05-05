package com.example.entities;

import com.example.enums.CommissionStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "commissions")
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false, unique = true)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "base_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseAmount;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal rate = BigDecimal.valueOf(0.05); // 5% by default

    @Column(name = "commission_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal commissionAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommissionStatus status = CommissionStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "commission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentCommission> paymentCommissions = new ArrayList<>();

    public Commission() {
        this.createdAt = LocalDateTime.now();
    }

    public Commission(Prescription prescription, Doctor doctor, BigDecimal baseAmount) {
        this();
        this.prescription = prescription;
        this.doctor = doctor;
        this.baseAmount = baseAmount;
        this.commissionAmount = baseAmount.multiply(rate);
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Prescription getPrescription() { return prescription; }
    public void setPrescription(Prescription prescription) { this.prescription = prescription; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { 
        this.baseAmount = baseAmount;
        this.commissionAmount = baseAmount.multiply(rate);
    }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { 
        this.rate = rate;
        this.commissionAmount = baseAmount.multiply(rate);
    }

    public BigDecimal getCommissionAmount() { return commissionAmount; }

    public CommissionStatus getStatus() { return status; }
    public void setStatus(CommissionStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<PaymentCommission> getPaymentCommissions() { return paymentCommissions; }
    public void setPaymentCommissions(List<PaymentCommission> paymentCommissions) { this.paymentCommissions = paymentCommissions; }
}
