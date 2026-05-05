package com.example.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CommissionSummaryDTO {
    private UUID doctorId;
    private String doctorName;
    private BigDecimal totalPendingCommissions;
    private int pendingCommissionCount;

    public CommissionSummaryDTO() {}

    public CommissionSummaryDTO(UUID doctorId, String doctorName, BigDecimal totalPendingCommissions, int pendingCommissionCount) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.totalPendingCommissions = totalPendingCommissions;
        this.pendingCommissionCount = pendingCommissionCount;
    }

    // Getters and Setters
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public BigDecimal getTotalPendingCommissions() { return totalPendingCommissions; }
    public void setTotalPendingCommissions(BigDecimal totalPendingCommissions) { this.totalPendingCommissions = totalPendingCommissions; }

    public int getPendingCommissionCount() { return pendingCommissionCount; }
    public void setPendingCommissionCount(int pendingCommissionCount) { this.pendingCommissionCount = pendingCommissionCount; }
}
