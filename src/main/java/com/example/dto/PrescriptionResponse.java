package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PrescriptionResponse(
    UUID id,
    DoctorInfo doctor,
    PatientInfo patient,
    UserInfo processedBy,
    LocalDate prescriptionDate,
    BigDecimal totalAmount,
    String status,
    String notes,
    LocalDateTime createdAt
) {}

record DoctorInfo(UUID id, String fullName, String specialty, String licenseNo) {}
record PatientInfo(UUID id, String fullName, LocalDate dateOfBirth, String phone) {}
record UserInfo(UUID id, String name, String email) {}