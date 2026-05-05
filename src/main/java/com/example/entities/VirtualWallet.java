package com.example.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "virtual_wallets")
public class VirtualWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, unique = true)
    private Doctor doctor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "total_earned", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalEarned = BigDecimal.ZERO;

    @Column(name = "total_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public VirtualWallet() {
        this.updatedAt = LocalDateTime.now();
    }

    public VirtualWallet(Doctor doctor) {
        this();
        this.doctor = doctor;
    }

    // Business methods
    public void addCommission(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.totalEarned = this.totalEarned.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void processPayment(BigDecimal amount) {
        if (amount.compareTo(this.balance) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds balance");
        }
        this.balance = this.balance.subtract(amount);
        this.totalPaid = this.totalPaid.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void resetBalance() {
        this.balance = BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { 
        this.balance = balance;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getTotalEarned() { return totalEarned; }
    public void setTotalEarned(BigDecimal totalEarned) { 
        this.totalEarned = totalEarned;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { 
        this.totalPaid = totalPaid;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
